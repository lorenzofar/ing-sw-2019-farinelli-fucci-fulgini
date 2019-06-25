package it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PlayerRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PaymentHandler;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurn;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TargetingScopeControllerTest {

    private static Match match;
    private static Player currentPlayer;
    private static PlayerTurn currentTurn;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static Map<String, PersistentView> views;
    private static PersistentView view;
    private static ControllerFactory factory;

    @BeforeClass
    public static void classSetup() {
        /* Disable logging */
        ModelTestUtil.disableLogging();

        /* Load schemas and assets */
        ModelTestUtil.loadCreatorResources();

        validConfig = new MatchConfiguration(0, 5);

        /* Create list of unique users */
        validNames = new HashSet<>();
        validNames.add("bzoto");
        validNames.add("loSqualo");
        validNames.add("zoniMyLord");
    }

    @Before
    public void setUp() {
        /* Create a match */
        match = MatchCreator.createMatch(validNames, validConfig);

        /* Create views of players which respond with their names */
        views = new HashMap<>();
        validNames.forEach(n -> {
            PersistentView v = mock(PersistentView.class);
            when(v.getUsername()).thenReturn(n);
            views.put(n,v);
        });
        view = views.get("bzoto");

        /* Create a stub of the controller factory (only stub relevant methods) */
        factory = mock(ControllerFactory.class);
        when(factory.createPaymentHandler()).thenReturn(new PaymentHandler(match));

        /* Set up the current turn */
        currentPlayer = match.getPlayerByName("bzoto");
        currentTurn = new PlayerTurn(currentPlayer);
        match.setCurrentTurn(currentTurn);
    }

    private int countAmmo(Player player) {
        return player.getAmmo().values().stream()
                .mapToInt(c -> c == null ? 0 : c)
                .sum();
    }

    @Test
    public void use_noDamagedPlayers_shouldReturnFalseAndNotify() throws InterruptedException {
        /* Create the controller */
        TargetingScopeController controller = new TargetingScopeController(match, factory);

        /* There are no damaged players */
        assertFalse(controller.use(view));

        /* Check that the user received no requests, but a warning */
        verify(view, never()).sendChoiceRequest(any());
        verify(view).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that he didn't pay any cost */
        Map<AmmoCube, Integer> playerAmmo = currentPlayer.getAmmo();
        for (Integer count : playerAmmo.values()) {
            assertEquals(1, (int) count);
        }
    }

    @Test
    public void use_oneDamagedPlayer_shouldAutoSelect() throws InterruptedException {
        /* Add a damaged player */
        Player damaged = match.getPlayerByName("loSqualo");
        damaged.getPlayerBoard().addDamage(currentPlayer, 1);
        currentTurn.addDamagedPlayer(damaged);

        /* Create the controller */
        TargetingScopeController controller = new TargetingScopeController(match, factory);

        /* Use the powerup: should auto-select and auto-pay */
        assertTrue(controller.use(view));

        /* Check that the user received no requests and no warnings */
        verify(view, never()).sendChoiceRequest(any());
        verify(view, never()).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that he paid the cost */
        assertEquals(2, countAmmo(currentPlayer));

        /* Check that the victim received the additional damage */
        assertEquals(2, damaged.getPlayerBoard().getDamageCount());
    }

    @Test
    public void use_multipleDamagedPlayers_shouldAskUser() throws InterruptedException {
        /* Add multiple damaged players */
        List<Player> damaged = Arrays.stream(new String[]{"loSqualo", "zoniMyLord"})
                .map(match::getPlayerByName)
                .collect(Collectors.toList());
        damaged.forEach(p -> {
            p.getPlayerBoard().addDamage(currentPlayer, 1);
            currentTurn.addDamagedPlayer(p);
        });

        /* Set up user's response */
        when(view.sendChoiceRequest(any(PlayerRequest.class)))
                .thenAnswer((SendChoiceRequestAnswer<String>) req ->
                        new CompletableChoice<>(req).complete("loSqualo")
                );

        /* Create the controller */
        TargetingScopeController controller = new TargetingScopeController(match, factory);

        /* Use the powerup: should ask selection and and auto-pay */
        assertTrue(controller.use(view));

        /* Check that the user received one request and no warnings */
        verify(view).sendChoiceRequest(any(PlayerRequest.class));
        verify(view, never()).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that he paid the cost */
        assertEquals(2, countAmmo(currentPlayer));

        /* Check that the victim received the additional damage */
        assertEquals(2, match.getPlayerByName("loSqualo").getPlayerBoard().getDamageCount());

        /* Check that the other player received no additional damage */
        assertEquals(1, match.getPlayerByName("zoniMyLord").getPlayerBoard().getDamageCount());
    }

    @Test
    public void use_cannotPay_shouldBeNotified() throws Exception {
        /* Add multiple damaged players */
        List<Player> damaged = Arrays.stream(new String[]{"loSqualo", "zoniMyLord"})
                .map(match::getPlayerByName)
                .collect(Collectors.toList());
        damaged.forEach(p -> {
            p.getPlayerBoard().addDamage(currentPlayer, 1);
            currentTurn.addDamagedPlayer(p);
        });

        /* Remove all ammo from player */
        currentPlayer.payAmmo(currentPlayer.getAmmo());

        /* Set up user's response */
        when(view.sendChoiceRequest(any(PlayerRequest.class)))
                .thenAnswer((SendChoiceRequestAnswer<String>) req ->
                        new CompletableChoice<>(req).complete("loSqualo")
                );

        /* Create the controller */
        TargetingScopeController controller = new TargetingScopeController(match, factory);

        /* Use the powerup: should ask selection and fail on payment */
        assertFalse(controller.use(view));

        /* Check that the user received one request and a warning */
        verify(view).sendChoiceRequest(any(PlayerRequest.class));
        verify(view).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that he did not pay the cost */
        assertEquals(0, countAmmo(currentPlayer));

        /* Check that the damaged players did not receive the additional damage */
        damaged.forEach(p ->
                assertEquals(1, p.getPlayerBoard().getDamageCount())
        );
    }
}
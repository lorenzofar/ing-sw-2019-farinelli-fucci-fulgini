package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PaymentHandler;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Array;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class MovementEffectTest {

    private static Match match;
    private static Board board;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static Map<String, PersistentView> views;
    private static PersistentView view;
    private static ControllerFactory factory;

    @BeforeClass
    public static void classSetup() throws Exception {
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
        board = match.getBoard();

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
    }

    /* ============ CAN BE USED ============= */

    @Test
    public void canBeUsed_dependsOnNoEffects_notExecuted_emptySet_shouldBeUsable() {
        MovementEffect effect = new MovementEffect("mov", match, factory);

        Set<String> completedEffects = Collections.emptySet();

        assertTrue(effect.canBeUsed(completedEffects));
    }

    @Test
    public void canBeUsed_dependsOnNoEffects_notExecuted_setWithOtherEffects_shouldBeUsable() {
        MovementEffect effect = new MovementEffect("mov", match, factory);

        Set<String> completedEffects = Collections.singleton("anotherEffect");

        assertTrue(effect.canBeUsed(completedEffects));
    }

    @Test
    public void canBeUsed_dependsOnNoEffects_alreadyExecuted_shouldNotBeUsable() {
        MovementEffect effect = new MovementEffect("mov", match, factory);

        Set<String> completedEffects = Collections.singleton("mov");

        assertFalse(effect.canBeUsed(completedEffects));
    }

    @Test
    public void canBeUsed_dependsOnEffects_dependencyNotExecuted_shouldNotBeUsable() {
        MovementEffect effect = new MovementEffect("mov", match, factory);
        effect.setDependsOnEffects(new HashSet<>(Arrays.asList("one", "two", "three")));

        Set<String> completedEffects = new HashSet<>(Arrays.asList("one", "three"));

        assertFalse(effect.canBeUsed(completedEffects));
    }

    @Test
    public void canBeUsed_dependsOnEffects_dependenciesExecuted_shouldBeUsable() {
        MovementEffect effect = new MovementEffect("mov", match, factory);
        effect.setDependsOnEffects(new HashSet<>(Arrays.asList("one", "two", "three")));

        Set<String> completedEffects = new HashSet<>(Arrays.asList("one", "three", "two", "four"));

        assertTrue(effect.canBeUsed(completedEffects));
    }

    /* ========================= USE ============================== */

    @Test
    public void use_noCost_shouldNotAskToPay() throws InterruptedException {
        /* Set up the player */
        Player player = match.getPlayerByName("bzoto"); /* He has one ammo for each color */
        board.movePlayer(player, board.getSquare(0, 0));

        /* Set up the effect */
        MovementEffect effect = new MovementEffect("mov", match, factory);

        effect.use(view);

        /* Check no interaction with user */
        verify(view, never()).sendChoiceRequest(any());
        verify(view, never()).showMessage(anyString(), any());

        /* Check that the user still has his ammo */
        Map<AmmoCube, Integer> playerAmmo = player.getAmmo();
        playerAmmo.forEach((cube, count) -> assertEquals(1, (int) count));
    }

    @Test
    public void use_cost_cantPay_shouldNotAskToMove() throws InterruptedException {
        /* Set up the player */
        Player player = match.getPlayerByName("bzoto"); /* He has one ammo for each color */
        board.movePlayer(player, board.getSquare(0, 0));

        /* Set up the effect */
        MovementEffect effect = new MovementEffect("mov", match, factory);
        effect.setCost(Arrays.asList(AmmoCubeCost.RED, AmmoCubeCost.RED));

        effect.use(view);

        /* Check that the user has been notified */
        verify(view, never()).sendChoiceRequest(any());
        verify(view).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that the user still has his ammo */
        Map<AmmoCube, Integer> playerAmmo = player.getAmmo();
        playerAmmo.forEach((cube, count) -> assertEquals(1, (int) count));
    }

    @Test
    public void use_cost_canPay_shouldBeAbleToMove() throws InterruptedException {
        /* Set up the player */
        Player player = match.getPlayerByName("bzoto"); /* He has one ammo for each color */
        board.movePlayer(player, board.getSquare(0, 0));

        /* Set up the effect */
        MovementEffect effect = new MovementEffect("mov", match, factory);
        effect.setCost(Arrays.asList(AmmoCubeCost.RED, AmmoCubeCost.YELLOW, AmmoCubeCost.BLUE));
        effect.setMaxMoves(1);

        /* Set up user's answer */
        CoordPair destination = new CoordPair(1,0);
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer((SendChoiceRequestAnswer<CoordPair>) req ->
                new CompletableChoice<>(req).complete(destination)
        );

        effect.use(view);

        /* Check that the player has been moved to the right place */
        assertThat(player.getCurrentSquare(), is(board.getSquare(destination)));

        /* Check that the user paid his ammo */
        Map<AmmoCube, Integer> playerAmmo = player.getAmmo();
        playerAmmo.forEach((cube, count) -> assertEquals(0, (int) count));
    }

    @Test
    public void use_noSquaresToMove_shouldNotAskPlayer() throws InterruptedException {
        /* Set up the player */
        Player player = match.getPlayerByName("bzoto"); /* He has one ammo for each color */
        board.movePlayer(player, board.getSquare(0, 0));

        /* Set up the effect */
        MovementEffect effect = new MovementEffect("mov", match, factory);
        effect.setCost(Arrays.asList(AmmoCubeCost.RED, AmmoCubeCost.YELLOW, AmmoCubeCost.BLUE));
        effect.setMaxMoves(0); /* Cannot move */

        effect.use(view);

        /* Check no interaction with user */
        verify(view, never()).sendChoiceRequest(any());
        verify(view, never()).showMessage(anyString(), any());

        /* Check that the player is still in his place */
        assertThat(player.getCurrentSquare(), is(board.getSquare(0, 0)));

        /* Check that the user paid his ammo before using the effect */
        Map<AmmoCube, Integer> playerAmmo = player.getAmmo();
        playerAmmo.forEach((cube, count) -> assertEquals(0, (int) count));
    }
}
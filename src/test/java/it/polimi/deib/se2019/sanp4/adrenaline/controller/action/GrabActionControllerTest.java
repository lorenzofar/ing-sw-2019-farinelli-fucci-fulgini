package it.polimi.deib.se2019.sanp4.adrenaline.controller.action;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.NotEnoughAmmoException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PowerupCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.CancelRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.FirstChoiceAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.AmmoSquare;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
import java.util.concurrent.CancellationException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GrabActionControllerTest {
    private static Match match;
    private static Board board;
    private static String name = "bzoto";
    private static Player player;
    private static AmmoSquare ammoSquare;
    private static MatchConfiguration validConfig;

    private static AmmoCard ACwithPowerup;
    private static AmmoCard ACwithoutPowerup;
    @Mock
    private static PersistentView view;
    @Mock
    private static ControllerFactory factory;


    /* NOTE TO MAINTAINER: WE USE A SINGLE INSTANCE OF MATCH, SO CLEAN THE SIDE EFFECTS AFTER THE TESTS */
    @BeforeClass
    public static void classSetup() throws NotEnoughAmmoException {
        /* Disable logging */
        ModelTestUtil.disableLogging();

        /* Load schemas and assets */
        ModelTestUtil.loadCreatorResources();

        validConfig = new MatchConfiguration(0, 5);

        /* Create a match */
        match = MatchCreator.createMatch(Collections.singleton(name), validConfig);

        /* Save some static variables */
        player = match.getPlayerByName(name);
        board = match.getBoard();
        ammoSquare = (AmmoSquare) match.getBoard().getSquare(0,0);

        /* Remove initial ammo from the player */
        player.payAmmo(player.getAmmo());

        /* Generate ammo cards */
        Map<AmmoCube, Integer> m1 = new HashMap<>(3);
        m1.put(AmmoCube.RED, 1);
        m1.put(AmmoCube.BLUE, 1);
        ACwithPowerup = new AmmoCard(0, m1, true); /* RBP */

        Map<AmmoCube, Integer> m2 = new HashMap<>(m1);
        m2.put(AmmoCube.RED, 2);
        ACwithoutPowerup = new AmmoCard(1, m2, false); /* RRB */
    }

    @Before
    public void setUp() {
        /* The view of the player must respond with his name */
        when(view.getUsername()).thenReturn(name);
        /* TODO: Mock payment handler */
    }

    @After
    public void tearDown() throws Exception {
        /* Revert the changes */
        player.payAmmo(player.getAmmo()); /* Put back the ammo */

        /* Put back the powerups */
        player.getPowerups().forEach(p -> {
            match.getPowerupStack().discard(p);
            player.removePowerup(p);
        });

        /* Remove any ammo card from the square */
        if (ammoSquare.isFull()) {
            match.getAmmoStack().discard(ammoSquare.grabAmmo());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void execute_playerNotSpawned_shouldThrow() {
        Match match = MatchCreator.createMatch(Collections.singleton(name), validConfig);
        GrabActionController controller = new GrabActionController(match, view, factory);

        controller.execute();
    }

    /* ======= GRAB FROM AMMO SQUARE ======= */

    @Test
    public void execute_AmmoSquare_ammoCardWithPowerups_playerHasNoResources_shouldGetResources() {
        /* Move the player to an ammo square */
        board.movePlayer(player, ammoSquare);

        /* Add an ammo card to the square */
        ammoSquare.insertAmmo(ACwithPowerup); /* RBP */

        /* Execute the action */
        GrabActionController controller = new GrabActionController(match, view, factory);
        controller.execute();

        /* Check that the player got the ammo and the powerup */
        Map<AmmoCube, Integer> playerAmmo = player.getAmmo();
        assertEquals(1, (int) playerAmmo.get(AmmoCube.RED));
        assertEquals(1, (int) playerAmmo.get(AmmoCube.BLUE));
        assertEquals(0, (int) playerAmmo.get(AmmoCube.YELLOW));

        assertEquals(1, player.getPowerups().size());

        /* Check that there has been no interaction */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that the ammo card is not in the square */
        assertFalse(ammoSquare.isFull());
    }

    @Test
    public void execute_AmmoSquare_ammoCardWithoutPowerups_playerHasNoResources_shouldGetResources() {
        /* Move the player to an ammo square */
        board.movePlayer(player, ammoSquare);

        /* Add an ammo card to the square */
        ammoSquare.insertAmmo(ACwithoutPowerup); /* RRB */

        /* Execute the action */
        GrabActionController controller = new GrabActionController(match, view, factory);
        controller.execute();

        /* Check that the player got the ammo and the powerup */
        Map<AmmoCube, Integer> playerAmmo = player.getAmmo();
        assertEquals(2, (int) playerAmmo.get(AmmoCube.RED));
        assertEquals(1, (int) playerAmmo.get(AmmoCube.BLUE));
        assertEquals(0, (int) playerAmmo.get(AmmoCube.YELLOW));

        assertTrue(player.getPowerups().isEmpty());

        /* Check that there has been no interaction */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that the ammo card is not in the square */
        assertFalse(ammoSquare.isFull());
    }

    @Test
    public void execute_AmmoSquare_ammoCardWithPowerups_playerHasMaxPowerups_discardsDrawn_shouldKeepInitialPowerups()
            throws Exception {
        /* Move the player to an ammo square */
        board.movePlayer(player, ammoSquare);

        /* Add an ammo card to the square */
        ammoSquare.insertAmmo(ACwithPowerup); /* RBP */

        /* Add powerups to the user */
        for (int i = 0; i < Player.MAX_POWERUPS; i++) {
            player.addPowerup(match.getPowerupStack().draw());
        }
        List<PowerupCard> initialPowerups = player.getPowerups();

        /* Set up the player response: the drawn powerup is always the first one */
        when(view.sendChoiceRequest(any(PowerupCardRequest.class))).thenAnswer(new FirstChoiceAnswer());

        /* Execute the action */
        GrabActionController controller = new GrabActionController(match, view, factory);
        controller.execute();

        /* Check that the player got the ammo */
        Map<AmmoCube, Integer> playerAmmo = player.getAmmo();
        assertEquals(1, (int) playerAmmo.get(AmmoCube.RED));
        assertEquals(1, (int) playerAmmo.get(AmmoCube.BLUE));
        assertEquals(0, (int) playerAmmo.get(AmmoCube.YELLOW));

        /* Check that he has the same powerups as before */
        assertTrue(player.getPowerups().containsAll(initialPowerups));

        /* Check that there has been exactly one interaction */
        verify(view).sendChoiceRequest(any(PowerupCardRequest.class));

        /* Check that the ammo card is not in the square */
        assertFalse(ammoSquare.isFull());
    }

    @Test
    public void execute_AmmoSquare_ammoCardWithPowerups_playerHasMaxPowerups_discardsFromHisHand_shouldHaveDrawnPowerup()
            throws Exception {
        /* Move the player to an ammo square */
        board.movePlayer(player, ammoSquare);

        /* Add an ammo card to the square */
        ammoSquare.insertAmmo(ACwithPowerup); /* RBP */

        /* Add powerups to the user */
        for (int i = 0; i < Player.MAX_POWERUPS; i++) {
            player.addPowerup(match.getPowerupStack().draw());
        }

        /* Set up the player response: the drawn powerup is always the first one */
        final List<PowerupCard> choices = new ArrayList<>(4);
        when(view.sendChoiceRequest(any(PowerupCardRequest.class)))
                .thenAnswer((SendChoiceRequestAnswer<PowerupCard>) req -> {
                    choices.addAll(req.getChoices());
                    return new CompletableChoice<>(req).complete(req.getChoices().get(1));
                });

        /* Execute the action */
        GrabActionController controller = new GrabActionController(match, view, factory);
        controller.execute();

        /* Check that the player got the ammo */
        Map<AmmoCube, Integer> playerAmmo = player.getAmmo();
        assertEquals(1, (int) playerAmmo.get(AmmoCube.RED));
        assertEquals(1, (int) playerAmmo.get(AmmoCube.BLUE));
        assertEquals(0, (int) playerAmmo.get(AmmoCube.YELLOW));

        /* Check that he has the new powerup */
        choices.remove(1);
        assertTrue(player.getPowerups().containsAll(choices));

        /* Check that there has been exactly one interaction */
        verify(view).sendChoiceRequest(any(PowerupCardRequest.class));

        /* Check that the ammo card is not in the square */
        assertFalse(ammoSquare.isFull());
    }

    @Test
    public void execute_AmmoSquare_ammoCardWithPowerups_playerHasMaxPowerups_cancelRequest_shouldKeepInitialPowerupsAndThrow()
            throws Exception {
        /* Move the player to an ammo square */
        board.movePlayer(player, ammoSquare);

        /* Add an ammo card to the square */
        ammoSquare.insertAmmo(ACwithPowerup); /* RBP */

        /* Add powerups to the user */
        for (int i = 0; i < Player.MAX_POWERUPS; i++) {
            player.addPowerup(match.getPowerupStack().draw());
        }
        List<PowerupCard> initialPowerups = player.getPowerups();

        /* Set up the player response: cancel */
        when(view.sendChoiceRequest(any(PowerupCardRequest.class))).thenAnswer(new CancelRequestAnswer());

        /* Execute the action */
        GrabActionController controller = new GrabActionController(match, view, factory);
        try {
            controller.execute();
            fail();
        } catch (CancellationException e) {
            /* OK */
        }

        /* Check that the player got the ammo */
        Map<AmmoCube, Integer> playerAmmo = player.getAmmo();
        assertEquals(1, (int) playerAmmo.get(AmmoCube.RED));
        assertEquals(1, (int) playerAmmo.get(AmmoCube.BLUE));
        assertEquals(0, (int) playerAmmo.get(AmmoCube.YELLOW));

        /* Check that he has the same powerups as before */
        assertTrue(player.getPowerups().containsAll(initialPowerups));

        /* Check that the ammo card is not in the square */
        assertFalse(ammoSquare.isFull());
    }

    @Test
    public void execute_AmmoSquare_noAmmoCard_shouldNotThrowAndNotifyPlayer()
            throws Exception {
        /* Move the player to an ammo square */
        board.movePlayer(player, ammoSquare);

        /* Add no ammo to the player */

        /* Execute the action */
        GrabActionController controller = new GrabActionController(match, view, factory);
        controller.execute();

        /* Check that the player has been notified */
        verify(view).showMessage(anyString(), eq(MessageType.WARNING));
    }
}
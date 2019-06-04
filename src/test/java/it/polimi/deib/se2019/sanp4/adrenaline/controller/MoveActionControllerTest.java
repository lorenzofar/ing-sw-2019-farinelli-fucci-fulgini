package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.action.MoveActionController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.CancelRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MoveActionControllerTest {

    private static Match match;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static Map<String, PersistentView> views;
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
    }

    @Test(expected = IllegalStateException.class)
    public void execute_PlayerNotSpawned_shouldThrow() throws InterruptedException {
        /* The match has just been created => the player is not spawned */
        PersistentView view = views.get("loSqualo");

        MoveActionController controller = new MoveActionController(match);

        controller.execute(view, 3);
    }

    @Test(expected = NullPointerException.class)
    public void execute_NullView_shouldThrow() throws InterruptedException {
        MoveActionController controller = new MoveActionController(match);

        controller.execute(null, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void execute_NegativeSteps_shouldThrow() throws InterruptedException {
        /* The match has just been created => the player is not spawned */
        Player player = match.getPlayerByName("loSqualo");
        PersistentView view = views.get("loSqualo");

        /* Spawn the player somewhere */
        Board board = match.getBoard();
        board.movePlayer(player, board.getSquare(0,0));

        MoveActionController controller = new MoveActionController(match);

        controller.execute(view, -5);
    }

    @Test
    public void execute_PlayerResponds_shouldMoveHimOnTheChoice() throws InterruptedException {
        /* The match has just been created => the player is not spawned */
        Player player = match.getPlayerByName("loSqualo");
        PersistentView view = views.get("loSqualo");

        /* Spawn the player somewhere */
        Board board = match.getBoard();
        board.movePlayer(player, board.getSquare(0,0));

        /* Prepare the response: we know that the square (0,1) is among the choices */
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer((SendChoiceRequestAnswer<CoordPair>) req ->
                new CompletableChoice<>(req).complete(new CoordPair(0,1)));

        MoveActionController controller = new MoveActionController(match);

        controller.execute(view, 1);

        /* Check that the player is now at the right position */
        assertEquals(new CoordPair(0,1), player.getCurrentSquare().getLocation());
    }

    @Test
    public void execute_Cancellation_shouldStayInTheSamePosition() throws InterruptedException {
        /* The match has just been created => the player is not spawned */
        Player player = match.getPlayerByName("loSqualo");
        PersistentView view = views.get("loSqualo");

        /* Spawn the player somewhere */
        Board board = match.getBoard();
        board.movePlayer(player, board.getSquare(0,0));

        /* Cancel the response */
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer(new CancelRequestAnswer());

        /* Call the method and check that it throws an exception */
        try {
            MoveActionController controller = new MoveActionController(match);
            controller.execute(view, 1);
            fail();
        } catch (CancellationException e) {
            /* Check that the player is in the same location */
            assertEquals(new CoordPair(0,0), player.getCurrentSquare().getLocation());
        }
    }
}
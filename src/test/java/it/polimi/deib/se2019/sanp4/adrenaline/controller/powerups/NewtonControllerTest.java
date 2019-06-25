package it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PlayerRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
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

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewtonControllerTest {

    private static Match match;
    private static Board board;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static Map<String, PersistentView> views;
    private static PersistentView view;

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
        board = match.getBoard();

        /* Create views of players which respond with their names */
        views = new HashMap<>();
        validNames.forEach(n -> {
            PersistentView v = mock(PersistentView.class);
            when(v.getUsername()).thenReturn(n);
            views.put(n,v);
        });
        view = views.get("bzoto");

        /* Set up the current player */
        Player currentPlayer = match.getPlayerByName("bzoto");
        board.movePlayer(currentPlayer, board.getSquare(0, 0));
    }

    @Test
    public void use_noOtherPlayersSpawned_shouldReturnFalseAndNotify() throws InterruptedException {
        /* The other players have not spawned by default */

        /* Create the controller */
        NewtonController controller = new NewtonController(match);

        /* Use it: should detect no players to move */
        controller.use(view);

        /* Check that the user received no request, but a warning */
        verify(view, never()).sendChoiceRequest(any());
        verify(view).showMessage(anyString(), eq(MessageType.WARNING));
    }

    @Test
    public void use_otherPlayersSpawned_shouldBeAbleToMove() throws InterruptedException {
        /* Spawn the other players */
        Player p1 = match.getPlayerByName("loSqualo");
        board.movePlayer(p1, board.getSquare(1,1));
        Player p2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(p2, board.getSquare(3,2));

        /* Set up the user's answer */
        final List<String> givenPlayers = new ArrayList<>(2);
        final List<CoordPair> givenSquares = new ArrayList<>(2);
        when(view.sendChoiceRequest(any(PlayerRequest.class)))
                .thenAnswer((SendChoiceRequestAnswer<String>) req -> {
                    givenPlayers.addAll(req.getChoices());
                    return new CompletableChoice<>(req).complete("loSqualo");
                });
        when(view.sendChoiceRequest(any(SquareRequest.class)))
                .thenAnswer((SendChoiceRequestAnswer<CoordPair>) req -> {
                    givenSquares.addAll(req.getChoices());
                    return new CompletableChoice<>(req).complete(new CoordPair(3,1));
                });

        /* Create the controller */
        NewtonController controller = new NewtonController(match);

        /* Use it: should detect both players can be moved */
        controller.use(view);

        /* Check that the user received two requests and no warnings */
        verify(view, times(2)).sendChoiceRequest(any());
        verify(view, never()).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that the sent choices were as expected */
        assertEquals(2, givenPlayers.size());
        assertTrue(givenPlayers.contains(p1.getName()));
        assertTrue(givenPlayers.contains(p2.getName()));

        assertEquals(5, givenSquares.size());
        assertTrue(givenSquares.contains(new CoordPair(1,1)));
        assertTrue(givenSquares.contains(new CoordPair(1,0)));
        assertTrue(givenSquares.contains(new CoordPair(2,1)));
        assertTrue(givenSquares.contains(new CoordPair(3,1)));
        assertTrue(givenSquares.contains(new CoordPair(1,2)));

        /* Check that the selected player has been moved to the selected square */
        assertEquals(board.getSquare(3,1), p1.getCurrentSquare());
    }
}
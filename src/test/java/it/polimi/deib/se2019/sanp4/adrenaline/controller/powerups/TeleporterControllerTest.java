package it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PlayerRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
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
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TeleporterControllerTest {

    private static Match match;
    private static Board board;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static Map<String, PersistentView> views;
    private static PersistentView view;
    private static Player currentPlayer;

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
        currentPlayer = match.getPlayerByName("bzoto");
        board.movePlayer(currentPlayer, board.getSquare(0, 0));
    }

    @Test
    public void use_moveToSquare_shouldMove() throws InterruptedException {
        /* Create the controller */
        TeleporterController controller = new TeleporterController(match);

        /* Set up user's answer */
        List<CoordPair> givenSquares = new ArrayList<>();
        when(view.sendChoiceRequest(any(SquareRequest.class)))
                .thenAnswer((SendChoiceRequestAnswer<CoordPair>) req -> {
                    givenSquares.addAll(req.getChoices());
                    return new CompletableChoice<>(req).complete(new CoordPair(1,1));
                });

        /* Use the effect */
        controller.use(view);

        /* Check that there has been a single request and no warnings */
        verify(view).sendChoiceRequest(any());
        verify(view, never()).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that the choices were as expected */
        List<CoordPair> expected = board.getSquares().stream()
                .map(Square::getLocation)
                .collect(Collectors.toList());
        assertTrue(givenSquares.containsAll(expected));
        assertTrue(expected.containsAll(givenSquares));

        /* Check that the player has been moved */
        assertEquals(board.getSquare(1,1), currentPlayer.getCurrentSquare());
    }
}
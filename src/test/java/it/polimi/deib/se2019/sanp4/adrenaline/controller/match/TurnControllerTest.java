package it.polimi.deib.se2019.sanp4.adrenaline.controller.match;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PlayerOperationRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.*;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.CancelRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum.*;
import static it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurnState.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TurnControllerTest {

    private static Match match;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static Map<String, PersistentView> views;
    private static ControllerFactory factory;
    private static SpawnController spawnController;

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

        /* Create a stub of the controller factory (only stub relevant methods) */
        factory = mock(ControllerFactory.class);
        spawnController = mock(SpawnController.class);
        when(factory.createSpawnController()).thenReturn(spawnController);
    }

    private static ActionCard actionCardWithFinalAction() {
        return new ActionCard(2, ActionCardEnum.REGULAR, Collections.singletonList(RUN), RELOAD);
    }

    private static ActionCard actionCardWithNoFinalAction() {
        return new ActionCard(2, ActionCardEnum.FRENZY1, Collections.singletonList(RUN), null);
    }

    @Test
    public void runTurn_playerIsNotSpawned_shouldAskToSpawn() throws InterruptedException {
        Player p = match.getPlayerByName("bzoto");
        PersistentView view = views.get("bzoto");

        /* Create the turn controller */
        PlayerTurn turn = new PlayerTurn(p);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Setup the mock of the user */
        /* When it gets the request to select the operation, just end the turn */
        doAnswer((SendChoiceRequestAnswer<PlayerOperationEnum>) req ->
                new CompletableChoice<>(req).complete(PlayerOperationEnum.END_TURN)
        ).when(view).sendChoiceRequest(any(PlayerOperationRequest.class));

        /* Run the turn */
        controller.runTurn();

        /* Check that the spawn controller has been asked to spawn the player */
        verify(spawnController).initialSpawn(view);

        /* Check that the turn is over */
        assertThat(turn.getTurnState(), is(OVER));
    }

    @Test
    public void runTurn_playerIsSpawned_shouldNotAskToSpawn() throws InterruptedException {
        Player p = match.getPlayerByName("bzoto");
        PersistentView view = views.get("bzoto");

        /* Spawn the player somewhere */
        Square s = match.getBoard().getSquare(0,0);
        match.getBoard().movePlayer(p,s);

        /* Create the turn controller */
        PlayerTurn turn = new PlayerTurn(p);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Setup the mock of the user */
        /* When it gets the request to select the operation, just end the turn */
        doAnswer((SendChoiceRequestAnswer<PlayerOperationEnum>) req ->
                new CompletableChoice<>(req).complete(PlayerOperationEnum.END_TURN)
        ).when(view).sendChoiceRequest(any(PlayerOperationRequest.class));

        /* Run the turn */
        controller.runTurn();

        /* Check that the spawn controller has been asked to spawn the player */
        verify(spawnController, never()).initialSpawn(any());

        /* Check that the turn is over */
        assertThat(turn.getTurnState(), is(OVER));
    }

    @Test
    public void runTurn_canceledRequestDuringTurn_shouldEndNormally() throws InterruptedException {
        Player p = match.getPlayerByName("bzoto");
        PersistentView view = views.get("bzoto");

        /* Create the turn controller */
        PlayerTurn turn = new PlayerTurn(p);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Set the view to cancel the request */
        when(view.sendChoiceRequest(any(PlayerOperationRequest.class))).thenAnswer(new CancelRequestAnswer());

        /* Run the turn and check that it ends with no exceptions */
        controller.runTurn();

        assertThat(turn.getTurnState(), is(OVER));
    }

    /* ======= UPDATE TURN STATE AFTER ACTION ======= */

    @Test
    public void updateTurnStateAfterAction_mainActionExecuted_remainingActions_shouldSetSelecting() {
        Player p = match.getPlayerByName("bzoto");

        /* Set a proper action card (max 2 actions) */
        p.setActionCard(actionCardWithFinalAction());

        /* Prepare the turn */
        PlayerTurn turn = new PlayerTurn(p);
        turn.setTurnState(BUSY);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Then test the method */
        controller.updateTurnStateAfterAction(RUN);

        assertThat(turn.getTurnState(), is(SELECTING));
        assertThat(turn.getRemainingActions(), is(1));
    }

    @Test
    public void updateTurnStateAfterAction_mainActionExecuted_noRemainingActions_hasFinalAction_shouldSetSelecting() {
        Player p = match.getPlayerByName("bzoto");

        /* Set a proper action card (max 2 actions) */
        p.setActionCard(actionCardWithFinalAction());

        /* Prepare the turn */
        PlayerTurn turn = new PlayerTurn(p);
        turn.setTurnState(BUSY);
        turn.setRemainingActions(1);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Then test the method */
        controller.updateTurnStateAfterAction(RUN);

        assertThat(turn.getTurnState(), is(SELECTING));
        assertThat(turn.getRemainingActions(), is(0));
    }

    @Test
    public void updateTurnStateAfterAction_mainActionExecuted_noRemainingActions_hasNoFinalAction_shouldEndTurn() {
        Player p = match.getPlayerByName("bzoto");

        /* Set a proper action card (max 2 actions) */
        p.setActionCard(actionCardWithNoFinalAction());

        /* Prepare the turn */
        PlayerTurn turn = new PlayerTurn(p);
        turn.setTurnState(BUSY);
        turn.setRemainingActions(1);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Then test the method */
        controller.updateTurnStateAfterAction(RUN);

        assertThat(turn.getTurnState(), is(OVER));
        assertThat(turn.getRemainingActions(), is(0));
    }

    @Test
    public void updateTurnStateAfterAction_noActionExecuted_remainingActions_shouldSetSelecting() {
        Player p = match.getPlayerByName("bzoto");

        /* Set a proper action card (max 2 actions) */
        p.setActionCard(actionCardWithNoFinalAction());

        /* Prepare the turn */
        PlayerTurn turn = new PlayerTurn(p);
        turn.setTurnState(SELECTING);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Then test the method */
        controller.updateTurnStateAfterAction(null);

        assertThat(turn.getTurnState(), is(SELECTING));
        assertThat(turn.getRemainingActions(), is(2)); /* The remaining actions are untouched */
    }

    @Test
    public void updateTurnStateAfterAction_noActionExecuted_noRemainingActions_shouldEndTurn() {
        Player p = match.getPlayerByName("bzoto");

        /* Set a proper action card (max 2 actions) */
        p.setActionCard(actionCardWithNoFinalAction());

        /* Prepare the turn */
        PlayerTurn turn = new PlayerTurn(p);
        turn.setTurnState(SELECTING);
        turn.setRemainingActions(0);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Then test the method */
        controller.updateTurnStateAfterAction(null);

        assertThat(turn.getTurnState(), is(OVER));
        assertThat(turn.getRemainingActions(), is(0)); /* The remaining actions are untouched */
    }

    @Test
    public void updateTurnStateAfterAction_finalActionExecuted_remainingActions_shouldEndTurn() {
        Player p = match.getPlayerByName("bzoto");

        /* Set a proper action card (max 2 actions) */
        p.setActionCard(actionCardWithFinalAction());

        /* Prepare the turn */
        PlayerTurn turn = new PlayerTurn(p);
        turn.setTurnState(BUSY);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Then test the method */
        controller.updateTurnStateAfterAction(RELOAD);

        assertThat(turn.getTurnState(), is(OVER));
        assertThat(turn.getRemainingActions(), is(0)); /* The remaining actions are untouched */
    }

    @Test
    public void updateTurnStateAfterAction_finalActionExecuted_noRemainingActions_shouldEndTurn() {
        Player p = match.getPlayerByName("bzoto");

        /* Set a proper action card (max 2 actions) */
        p.setActionCard(actionCardWithFinalAction());

        /* Prepare the turn */
        PlayerTurn turn = new PlayerTurn(p);
        turn.setTurnState(BUSY);
        turn.setRemainingActions(0);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Then test the method */
        controller.updateTurnStateAfterAction(RELOAD);

        assertThat(turn.getTurnState(), is(OVER));
        assertThat(turn.getRemainingActions(), is(0)); /* The remaining actions are untouched */
    }
}
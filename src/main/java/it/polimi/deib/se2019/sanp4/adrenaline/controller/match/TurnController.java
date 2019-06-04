package it.polimi.deib.se2019.sanp4.adrenaline.controller.match;

import it.polimi.deib.se2019.sanp4.adrenaline.common.AdrenalineProperties;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ActionRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PlayerOperationRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerOperationEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.SpawnController;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurn;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurnState;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurnState.*;
import static it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene.*;

/**
 * It is responsible for controlling the flow of a single turn.
 * <ul>
 *     <li>Initial spawn of the player</li>
 *     <li>Operation selection</li>
 *     <li>Action selection</li>
 * </ul>
 */
public class TurnController {

    /** View of the current player */
    private final PersistentView currentView;

    /** Views of all the players */
    private final Map<String, PersistentView> views;

    /** Reference to the match */
    private final Match match;

    /** Reference to the controlled turn */
    private final PlayerTurn turn;

    /** Factory to create other controllers */
    private final ControllerFactory factory;

    /** Turn duration */
    private final int timeout;

    private static final Logger logger = Logger.getLogger(TurnController.class.getName());

    /**
     * Creates a new controller for the turn of the match.
     * The match instance must have a current turn with a player associated to it
     * @param turn Instance of the turn to be controlled, not null
     * @param match Instance of the match whose current turn has to be controlled, not null
     * @param views Map (username, view) with the views of the players, not null
     * @param factory factory to create the needed controllers, not null
     * @throws NullPointerException If any of the parameters is null, if the current turn is null
     * and if the owner of the current turn does not exist
     */
    public TurnController(PlayerTurn turn, Match match, Map<String, PersistentView> views, ControllerFactory factory) {
        this.views = views;
        this.match = match;
        this.turn = turn;
        this.factory = factory;

        /* Determine the current player */
        this.currentView = views.get(turn.getTurnOwner().getName());

        /* Determine the duration of the turn */
        timeout = Integer.parseInt((String) AdrenalineProperties.getProperties()
                .getOrDefault("adrenaline.timeout.turn", "180"));
    }

    /**
     * Runs the turn until it ends because the user has performed all his actions
     * or because he has been suspended.
     * or {@link PlayerTurnState#SELECTING}
     * @throws InterruptedException If the thread gets interrupted
     */
    public void runTurn() throws InterruptedException {
        /* Start the timer of the turn */
        currentView.startTimer(() -> null, timeout, TimeUnit.SECONDS);

        try {
            setViewScenes(); /* Set the proper scene for the current player and the others */

            if (turn.getTurnState() == INITIAL_SPAWN) {
                /* Ask the player to spawn */
                SpawnController spawnController = factory.createSpawnController();
                spawnController.initialSpawn(currentView);
                turn.setTurnState(SELECTING);
            }

            /* Ask the user to perform operations until the turn is over */
            while (turn.getTurnState() != OVER) {
                PlayerOperationEnum operation = askToSelectOperation(currentView);
                performOperation(operation);
            }

            /* Stop the timer: the turn ended without cancellations */
            currentView.stopTimer();
        } catch (CancellationException e) {
            /* The player of this turn has been suspended */
            logger.log(Level.INFO, "Caught a cancellation during \"{0}\"'s turn, ending it");
            turn.setTurnState(OVER);
        } finally {
            resetViewScenes(); /* Set all player scenes to idle */
        }
    }

    /**
     * Selects the scenes of the player views to begin the turn:
     * <ul>
     *     <li>The owner of the turn gets {@link ViewScene#TURN_PLAYING}</li>
     *     <li>The other players get {@link ViewScene#TURN_IDLE}</li>
     * </ul>
     */
    void setViewScenes() {
        for (PersistentView v : views.values()) {
            v.selectScene(v == currentView ? TURN_PLAYING : TURN_IDLE);
        }
    }

    /**
     * Selects the scenes of the player views at the end of the turn:
     * All the players get {@link ViewScene#TURN_IDLE}
     */
    void resetViewScenes() {
        for (PersistentView v : views.values()) {
            v.selectScene(TURN_IDLE);
        }
    }

    /**
     * Asks the player to select a {@link PlayerOperationEnum} which he wants to perform
     * @param view the view of the player
     * @return the selected operation
     * @throws CancellationException If the request to the view is cancelled
     * @throws InterruptedException If the thread gets interrupted
     */
    PlayerOperationEnum askToSelectOperation(PersistentView view) throws InterruptedException {
        /* The player can choose any of the available operations */
        PlayerOperationRequest req = new PlayerOperationRequest(Arrays.asList(PlayerOperationEnum.values()));
        return view.sendChoiceRequest(req).get();
    }

    /**
     * Performs the selected operation by calling the right handler.
     * If the turn is over after performing a particular operation, the handler of the operation
     * is responsible to set the turn state
     * @param operation The operation which needs to be performed
     * @throws CancellationException If any request to the view is cancelled while performing the operation
     * @throws InterruptedException If the thread gets interrupted
     */
    void performOperation(PlayerOperationEnum operation) throws InterruptedException {
        switch (operation) {
            case PERFORM_ACTION:
                performActionHandler();
                break;
            case USE_POWERUP:
                usePowerupHandler();
                break;
            case END_TURN:
                endTurnHandler();
                break;
        }
    }

    void usePowerupHandler() {
        /* TODO: Implement this method */
    }

    /**
     * Sets the state of the current turn to {@link PlayerTurnState#OVER}
     */
    void endTurnHandler() {
        turn.setTurnState(OVER);
    }

    /**
     * Handles the request of the player to choose an action:
     * <ol>
     *     <li>The possible actions are determined</li>
     *     <li>The user is asked to select an action</li>
     *     <li>The action is executed by using its controller(s)</li>
     * </ol>
     * @throws CancellationException If any request to the view is cancelled while performing the operation
     * @throws InterruptedException If the thread gets interrupted
     */
    void performActionHandler() throws InterruptedException {
        /* Determine possible actions */
        List<ActionEnum> actions = new ArrayList<>(turn.getAvailableActions());

        /* Send a request to choose the action to the player */
        ActionEnum action = askToChooseAction(currentView, actions);

        /* The user chose his action */
        if (action != null) {
            turn.setTurnState(BUSY);
            runAction(action);
        }

        /* Update the state according to the previous state and the selected action */
        updateTurnStateAfterAction(action);
    }

    /**
     * Asks the given view to select one or zero actions from the given list
     * @param view The view of the player
     * @param actions The list of actions to choose from
     * @return The selected action or {@code null} if no action has been selected
     * @throws CancellationException If the request to the user gets cancelled
     * @throws InterruptedException If the thread gets interrupted
     */
    ActionEnum askToChooseAction(PersistentView view, List<ActionEnum> actions) throws InterruptedException {
        ActionRequest req = new ActionRequest("Select the action you wish to perform", actions, true);
        return view.sendChoiceRequest(req).get();
    }

    /**
     * After one or no action has been performed, updates the state of the turn:
     * <ul>
     *     <li>If the action was a "main" action, then decrements the remaining actions and resets the current action</li>
     *     <li>If the action was a final action, then the turn is over</li>
     *     <li>If no action has been performed and the user has no remaining actions, it means that he refused
     *     to perform the final action, so the turn is over</li>
     * </ul>
     * @param performedAction The action that has been performed by the player, nullable
     */
    void updateTurnStateAfterAction(ActionEnum performedAction) {
        ActionCard actionCard = turn.getTurnOwner().getActionCard();
        int remainingActions = turn.getRemainingActions();
        PlayerTurnState nextState = turn.getTurnState();

        if (performedAction != null) {
            if (performedAction != actionCard.getFinalAction()) { /* Performed main action */
                /* Decrement the remaining "main" actions */
                remainingActions = remainingActions - 1;
                nextState = SELECTING;

                if (remainingActions == 0 && !actionCard.hasFinalAction()) {
                    // If the user has a final action he may want to perform it (the state remains to SELECTING
                    // If he does not have it, then the turn is over
                    nextState = OVER;
                }

            } else { /* Performed final action */
                remainingActions = 0;
                nextState = OVER;
            }
        } else {
            /* The user chose to perform no action */
            if (remainingActions == 0) {
                /* He chose not to perform his final action => the turn is over */
                nextState = OVER;
            }
        }

        turn.setRemainingActions(remainingActions);
        turn.setTurnState(nextState);
    }

    /**
     * Runs the given action on the current player by calling the proper controllers
     * @param action The selected action, not null
     * @throws CancellationException If any request to the view is cancelled while performing the operation
     * @throws InterruptedException If the thread gets interrupted
     */
    void runAction(ActionEnum action) throws InterruptedException {
        /* TODO: Implement actions */
        switch (action) {
            case RUN:
                /* The player can move for max. 3 steps */
                factory.createMoveActionController()
                        .execute(currentView, 3);
                break;
            case GRAB:
                break;
            case SHOOT:
                break;
            case RELOAD:
                break;
            case ADRENALINE_GRAB:
                break;
            case ADRENALINE_SHOOT:
                break;
            case FRENZY2_SHOOT:
                break;
            case FRENZY2_RUN:
                /* The player can move for max. 4 steps */
                factory.createMoveActionController()
                        .execute(currentView, 4);
                break;
            case FRENZY2_GRAB:
                break;
            case FRENZY1_SHOOT:
                break;
            case FRENZY1_GRAB:
                break;
        }
    }
}

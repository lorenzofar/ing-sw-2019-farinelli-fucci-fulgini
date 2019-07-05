package it.polimi.deib.se2019.sanp4.adrenaline.controller.match;

import it.polimi.deib.se2019.sanp4.adrenaline.common.AdrenalineProperties;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ActionRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PlayerOperationRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PowerupCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups.PowerupController;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerOperationEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurn;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurnState;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurnState.*;
import static it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene.*;

/**
 * It is responsible for controlling the flow of a single turn.
 * <ul>
 * <li>Initial spawn of the player</li>
 * <li>Operation selection</li>
 * <li>Action selection</li>
 * <li>Powerup selection and removal of the cards</li>
 * </ul>
 * @author Alessandro Fulgini
 */
public class TurnController {

    private static final String MESSAGE_SELECT_ACTION = "Select the action you wish to perform";

    private static final String MESSAGE_SELECT_POWERUP = "Select the powerup you want to use";

    private static final String MESSAGE_NO_POWERUPS = "You have no usable powerups";

    /**
     * View of the current player
     */
    private final PersistentView currentView;

    /**
     * Views of all the players
     */
    private final Map<String, PersistentView> views;

    /**
     * Reference to the match
     */
    private final Match match;

    /**
     * Reference to the controlled turn
     */
    private final PlayerTurn turn;

    /**
     * Factory to create other controllers
     */
    private final ControllerFactory factory;

    /**
     * Turn duration
     */
    private final int timeout;

    private static final Logger logger = Logger.getLogger(TurnController.class.getName());

    /**
     * Creates a new controller for the turn of the match.
     * The match instance must have a current turn with a player associated to it
     *
     * @param turn    Instance of the turn to be controlled, not null
     * @param match   Instance of the match whose current turn has to be controlled, not null
     * @param views   Map (username, view) with the views of the players, not null
     * @param factory factory to create the needed controllers, not null
     * @throws NullPointerException If any of the parameters is null, if the current turn is null
     *                              and if the owner of the current turn does not exist
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
     *
     * @throws InterruptedException If the thread gets interrupted
     */
    public void runTurn() throws InterruptedException {
        logger.log(Level.FINE, "Starting \"{0}\" turn", currentView.getUsername());

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

            logger.log(Level.FINE, "Turn \"{0}\" ended normally", currentView.getUsername());
        } catch (CancellationException e) {
            /* The player of this turn has to be suspended */
            match.suspendPlayer(currentView.getUsername());
            logger.log(Level.INFO, "Caught a cancellation during \"{0}\" turn, " +
                            "suspending the player and ending turn", currentView.getUsername());
            turn.setTurnState(OVER);
        } finally {
            resetViewScenes(); /* Set all player scenes to idle */
        }
    }

    /**
     * Selects the scenes of the player views to begin the turn:
     * <ul>
     * <li>The owner of the turn gets {@link ViewScene#TURN_PLAYING}</li>
     * <li>The other players get {@link ViewScene#TURN_IDLE}</li>
     * </ul>
     */
    private void setViewScenes() {
        for (PersistentView v : views.values()) {
            v.selectScene(v == currentView ? TURN_PLAYING : TURN_IDLE);
        }
    }

    /**
     * Selects the scenes of the player views at the end of the turn:
     * All the players get {@link ViewScene#TURN_IDLE}, by only changing the current player's scene,
     * since the others are already in IDLE
     */
    private void resetViewScenes() {
        currentView.selectScene(TURN_IDLE);
    }

    /**
     * Asks the player to select a {@link PlayerOperationEnum} which he wants to perform
     *
     * @param view the view of the player
     * @return the selected operation
     * @throws CancellationException If the request to the view is cancelled
     * @throws InterruptedException  If the thread gets interrupted
     */
    private PlayerOperationEnum askToSelectOperation(PersistentView view) throws InterruptedException {
        /* The player can choose any of the available operations */
        PlayerOperationRequest req = new PlayerOperationRequest(Arrays.asList(PlayerOperationEnum.values()));
        return view.sendChoiceRequest(req).get();
    }

    /**
     * Performs the selected operation by calling the right handler.
     * If the turn is over after performing a particular operation, the handler of the operation
     * is responsible to set the turn state
     *
     * @param operation The operation which needs to be performed
     * @throws CancellationException If any request to the view is cancelled while performing the operation
     * @throws InterruptedException  If the thread gets interrupted
     */
    private void performOperation(PlayerOperationEnum operation) throws InterruptedException {
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
            default:
                logger.log(Level.SEVERE, "Unsupported operation: \"{0}\"", operation.name());
        }
    }

    /* ============== POWERUPS ================= */

    /**
     * Handles the request of the player to use a powerup:
     * <ul>
     * <li>Determines which powerups can be used, if there are none the user is notified</li>
     * <li>Asks the user to select one of those powerups</li>
     * <li>Let him use the powerups</li>
     * <li>If the powerups has been used correctly, then the card is removed from the player's hands</li>
     * </ul>
     *
     * @throws CancellationException If any request to the view is cancelled while performing the operation
     * @throws InterruptedException  If the thread gets interrupted
     */
    private void usePowerupHandler() throws InterruptedException {
        Player player = turn.getTurnOwner();
        /* Determine the usable powerups */
        List<PowerupCard> powerups = player.getPowerups().stream()
                /* Tagback can't be used during the current turn: we remove it immediately */
                .filter(powerupCard -> powerupCard.getType() != PowerupEnum.TAGBACK)
                .collect(Collectors.toList());

        if (powerups.isEmpty()) {
            /* Notify the player and terminate */
            currentView.showMessage(MESSAGE_NO_POWERUPS, MessageType.WARNING);
            return;
        }

        /* Ask the player to select a powerup */
        PowerupCardRequest req = new PowerupCardRequest(MESSAGE_SELECT_POWERUP, powerups, false);
        PowerupCard selectedPowerup = currentView.sendChoiceRequest(req).get();

        /* Remove the powerup from the player's hands so he can't use it to pay costs */
        player.removePowerup(selectedPowerup);

        /* Create the controller and use the powerup */
        boolean used = false;
        try {
            /* Use the factory to create the right controller */
            PowerupController powerupController = factory.createPowerupController(selectedPowerup.getType());

            /* Then use the effect of the powerup */
            used = powerupController.use(currentView); /* May throw */
        } finally {
            if (used) {
                match.getPowerupStack().discard(selectedPowerup); /* Discard if used */
            } else {
                try {
                    player.addPowerup(selectedPowerup); /* Give it back if not used */
                } catch (FullCapacityException e) {
                    /* Does not happen because we removed the powerup before */
                }
            }
        }
    }

    /**
     * Sets the state of the current turn to {@link PlayerTurnState#OVER}
     */
    private void endTurnHandler() {
        turn.setTurnState(OVER);
    }

    /* ============== ACTIONS ================= */

    /**
     * Handles the request of the player to choose an action:
     * <ol>
     * <li>The possible actions are determined</li>
     * <li>The user is asked to select an action</li>
     * <li>The action is executed by using its controller(s)</li>
     * </ol>
     *
     * @throws CancellationException If any request to the view is cancelled while performing the operation
     * @throws InterruptedException  If the thread gets interrupted
     */
    private void performActionHandler() throws InterruptedException {
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
     *
     * @param view    The view of the player
     * @param actions The list of actions to choose from
     * @return The selected action or {@code null} if no action has been selected
     * @throws CancellationException If the request to the user gets cancelled
     * @throws InterruptedException  If the thread gets interrupted
     */
    private ActionEnum askToChooseAction(PersistentView view, List<ActionEnum> actions) throws InterruptedException {
        ActionRequest req = new ActionRequest(MESSAGE_SELECT_ACTION, actions, true);
        return view.sendChoiceRequest(req).get();
    }

    /**
     * After one or no action has been performed, updates the state of the turn:
     * <ul>
     * <li>If the action was a "main" action, then decrements the remaining actions and resets the current action</li>
     * <li>If the action was a final action, then the turn is over</li>
     * <li>If no action has been performed and the user has no remaining actions, it means that he refused
     * to perform the final action, so the turn is over</li>
     * </ul>
     *
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
     *
     * @param action The selected action, not null
     * @throws CancellationException If any request to the view is cancelled while performing the operation
     * @throws InterruptedException  If the thread gets interrupted
     */
    private void runAction(ActionEnum action) throws InterruptedException {
        switch (action) {
            case RUN:
                factory.createMoveActionController()
                        .execute(currentView, 3); /* Move max. 3 steps */
                break;
            case GRAB:
                factory.createMoveActionController()
                        .execute(currentView, 1); /* Move max. 1 step */
                factory.createGrabActionController(currentView)
                        .execute(); /* Grab */
                break;
            case SHOOT:
                factory.createShootActionController()
                        .execute(currentView); /* Shoot */
                break;
            case RELOAD:
                factory.createReloadActionController()
                        .execute(currentView); /* Reload any number of weapons */
                break;
            case ADRENALINE_GRAB:
            case FRENZY2_GRAB:
                factory.createMoveActionController()
                        .execute(currentView, 2); /* Move max. 2 steps */
                factory.createGrabActionController(currentView)
                        .execute(); /* Grab */
                break;
            case ADRENALINE_SHOOT:
                factory.createMoveActionController()
                        .execute(currentView, 1); /* Move max. 1 step */
                factory.createShootActionController()
                        .execute(currentView); /* Shoot */
                break;
            case FRENZY2_SHOOT:
                factory.createMoveActionController()
                        .execute(currentView, 1); /* Move max. 1 step */
                factory.createReloadActionController()
                        .execute(currentView); /* Reload any number of weapons */
                factory.createShootActionController()
                        .execute(currentView); /* Shoot */
                break;
            case FRENZY2_RUN:
                factory.createMoveActionController()
                        .execute(currentView, 4); /* Move max. 4 steps */
                break;
            case FRENZY1_SHOOT:
                factory.createMoveActionController()
                        .execute(currentView, 2); /* Move max. 2 steps */
                factory.createShootActionController()
                        .execute(currentView); /* Shoot */
                break;
            case FRENZY1_GRAB:
                factory.createMoveActionController()
                        .execute(currentView, 3); /* Move max. 3 steps */
                factory.createGrabActionController(currentView)
                        .execute(); /* Grab */
                break;
            default:
                logger.log(Level.SEVERE, "Unsupported action: \"{0}\"", action.name());
        }
    }
}

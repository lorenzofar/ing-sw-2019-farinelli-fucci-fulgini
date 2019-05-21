package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A representation of the turn of a player
 * It contains information about:
 * <ul>
 *     <li>The current action performed by the player</li>
 *     <li>The number of actions available for the player to perform</li>
 *     <li>The player the turn belongs to</li>
 *     <li>The list of all the players damaged during the turn</li>
 * </ul>
 */
public class PlayerTurn{

    /** The current action performed by the player */
    private ActionEnum currentAction;

    /** The number of actions the player can still perform */
    private int remainingActions;

    /** The player the turn belongs to */
    private Player turnOwner;

    /** If the player has already performed the final action */
    private boolean hasPerformedFinalAction;

    /** The state of the turn */
    private PlayerTurnState state;

    /** The list of players that received a damage in the turn */
    private Set<Player> hitPlayers;

    /**
     * Creates a new turn for the specified player.
     * @param player The object representing the player, not null
     */
    public PlayerTurn(Player player){
        if(player == null){
            throw new NullPointerException("Player cannot be null");
        }
        currentAction = null;
        hitPlayers = new LinkedHashSet<>();
        turnOwner = player;
        state = PlayerTurnState.SELECTING;
        // Set the remaining actions according to the player's action card
        remainingActions = player.getActionCard().getMaxActions();
    }

    /**
     * Retrieves the list of actions the player can perform
     * @return The list of objects representing the actions
     */
    public List<ActionEnum> getAvailableActions(){
        return (List<ActionEnum>) turnOwner.getActionCard().getActions();
    }

    /**
     * Checks whether the provided action can be performed by the player
     * @param action The action to test, not null
     * @return {@code true} if the action can be performed, {@code false} otherwise
     */
    public boolean canPerformAction(ActionEnum action){
        if(action == null){
            throw new NullPointerException("Action cannot be null");
        }
        if(remainingActions == 0){
            // Return false if the player has no actions left
            return false;
        }
        // Retrieve the action card of the player
        ActionCard playerActionCard = turnOwner.getActionCard();
        // Check whether the action card contains the action or if it is the final one
        return playerActionCard.hasAction(action) || (playerActionCard.hasFinalAction() && action.toString().equals(playerActionCard.getFinalAction().toString()));
    }

    /**
     * Checks whether the provided action is being performed by the player
     * @param action The action to test, not null
     * @return {@code true} is the action is active, {@code false} otherwise
     */
    public boolean isActionActive(ActionEnum action){
        if(action == null){
            throw new NullPointerException("Action cannot be null");
        }
        return currentAction.toString().equals(action.toString());
    }

    /**
     * Retrieves the state the turn is into
     * @return The object representing the state
     */
    public PlayerTurnState getTurnState(){ return this.state; }

    /**
     * Sets the state the turn is into
     * @param state The object representing the state, not null
     */
    public void setTurnState(PlayerTurnState state){
        if(state == null){
            throw new NullPointerException("State cannot be null");
        }
        this.state = state;
    }

    /**
     * Retrieves the list of players that received a damage during this turn
     * @return The list of objects representing the players
     */
    public List<Player> getHitPlayers(){
        return new ArrayList<>(this.hitPlayers);
    }

    /**
     * Retrieves the player the turn belongs to
     * @return The object representing the player
     */
    public Player getTurnOwner() {
        return turnOwner;
    }

    /**
     * Retrieves the action that is currently being performed
     * @return The object representing the action
     */
    public ActionEnum getCurrentAction() {
        return currentAction;
    }

    /**
     * Retrieves the number of actions the user can still perform
     * @return The count of remaining actions
     */
    public int getRemainingActions() {
        return remainingActions;
    }
}
package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    /** The state of the turn */
    private PlayerTurnState state;

    /** The list of players that received a damage in the turn */
    private List<Player> hitPlayers;

    /**
     * Creates a new turn for the specified player.
     * @param player The object representing the player
     */
    PlayerTurn(Player player){
        currentAction = null;
        hitPlayers = new ArrayList<>();
        turnOwner = player;
        state = PlayerTurnState.SELECTING;
        //TODO: Determine number of remaining actions
    }

    /**
     * Retrieves the list of actions the player can perform
     * @return The list of objects representing the actions
     */
    public List<ActionEnum> getAvailableActions(){
        return null;
    }

    /**
     * Checks whether the provided action can be performed by the player
     * @param action The action to test
     * @return True if the action can be performed, false if not
     */
    public boolean canPerformAction(ActionEnum action){
        //TODO:
        return false;
    }

    /**
     * Checks whether the provided action is being performed by the player
     * @param action The action to test
     * @return True is the action is active, false if not
     */
    public boolean isActionActive(ActionEnum action){
        //TODO:
        return false;
    }

    /* ===== GETTERS ===== */

    /**
     * Retrieves the state the turn is into
     * @return The object representing the state
     */
    public PlayerTurnState getTurnState(){ return this.state; }

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
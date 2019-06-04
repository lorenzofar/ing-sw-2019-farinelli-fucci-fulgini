package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerTurnView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.PlayerTurnUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.*;

import static it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurnState.*;

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
public class PlayerTurn extends Observable{

    /** The number of actions the player can still perform */
    private int remainingActions;

    /** The player the turn belongs to */
    private Player turnOwner;

    /** The state of the turn */
    private PlayerTurnState state;

    /** The list of players that received a damage in the turn */
    private Set<Player> damagedPlayers;

    /**
     * Creates a new turn for the specified player.
     * @param player The object representing the player, not null
     */
    public PlayerTurn(Player player){
        if(player == null){
            throw new NullPointerException("Player cannot be null");
        }
        damagedPlayers = new LinkedHashSet<>();
        turnOwner = player;
        // Set the remaining actions according to the player's action card
        remainingActions = player.getActionCard().getMaxActions();

        /* Determine if the player still has to spawn */
        if (player.getCurrentSquare() == null) {
            state = PlayerTurnState.INITIAL_SPAWN;
        } else {
            state = PlayerTurnState.SELECTING;
        }
    }

    /**
     * Retrieves the list of actions the player can perform, according to the current state of the turn
     * <ul>
     *     <li>If the turn is over, the collection is empty</li>
     *     <li>If the user still has remaining actions, all the actions in the action card are returned</li>
     *     <li>If the user has finished his remaining actions, then the final action is returned</li>
     * </ul>
     * @return A read-only collection with the actions that the user can choose to perform
     */
    public Collection<ActionEnum> getAvailableActions(){
        ActionCard actionCard = turnOwner.getActionCard();
        if (state == OVER) {
            return Collections.emptyList(); /* No actions to perform */
        } else if(remainingActions > 0) {
            return actionCard.getActions(); /* Main + Final actions */
        } else if (actionCard.hasFinalAction()) {
            return Collections.singletonList(actionCard.getFinalAction()); /* Only final action */
        } else {
            return Collections.emptyList(); /* No action to perform */
        }
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
        if (this.state != state) this.notifyObservers(new PlayerTurnUpdate(this.generateView()));
        this.state = state;
    }

    /**
     * Adds given player to the set of players damaged during this turn
     * @param player The player to be added
     */
    public void addDamagedPlayer(Player player) {
        damagedPlayers.add(player);
    }

    /**
     * Retrieves the list of players that received a damage during this turn
     * @return A set of objects representing the players
     */
    public Set<Player> getDamagedPlayers(){
        return Collections.unmodifiableSet(damagedPlayers);
    }

    /**
     * Retrieves the player the turn belongs to
     * @return The object representing the player
     */
    public Player getTurnOwner() {
        return turnOwner;
    }

    /**
     * Retrieves the number of actions the user can still perform
     * @return The count of remaining actions
     */
    public int getRemainingActions() {
        return remainingActions;
    }

    /**
     * Sets the number of actions the user can still perform
     * @param remainingActions The number of actions the player can perform
     */
    public void setRemainingActions(int remainingActions) {
        if (remainingActions < 0) {
            throw new IllegalArgumentException("The number of remaining actions cannot be negative");
        }
        this.remainingActions = remainingActions;
    }

    /**
     * Generates the {@link PlayerTurnView} of the player turn
     * @return the player turn view
     */
    public PlayerTurnView generateView(){
        PlayerTurnView view = new PlayerTurnView(this.getTurnOwner().getName());
        view.setRemainingActions(this.getRemainingActions());
        view.setState(this.getTurnState());
        return view;
    }
}
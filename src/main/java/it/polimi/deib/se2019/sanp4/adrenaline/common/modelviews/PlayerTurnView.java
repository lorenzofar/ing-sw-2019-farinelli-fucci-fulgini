package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurnState;

/**
 * A lightweight representation of a turn in the view
 */
public class PlayerTurnView {

    /**
     * The count of remaining available actions
     */
    private int remainingActions;

    /**
     * The username of the player owning the turn
     */
    private String player;

    /**
     * The state of the turn
     */
    private PlayerTurnState state;

    public PlayerTurnView(String player) {
        if (player == null) {
            throw new NullPointerException("Player cannot be null");
        }
        this.player = player;
        this.state = PlayerTurnState.SELECTING; // Set the initial state of the turn
    }

    /**
     * Retrieves the number of remaining actions available for the player
     *
     * @return The number of remaining actions
     */
    public int getRemainingActions() {
        return remainingActions;
    }

    /**
     * Sets the number of remaining actions
     * If the provided number is negative, nothing happens
     *
     * @param remainingActions The number of remaining actions
     */
    public void setRemainingActions(int remainingActions) {
        if (remainingActions >= 0) {
            this.remainingActions = remainingActions;
        }
    }

    /**
     * Retrieves the player that is owning the turn
     *
     * @return The username of the player
     */
    public String getPlayer() {
        return player;
    }

    /**
     * Retrieves the state of the turn
     *
     * @return The object representing the state
     */
    public PlayerTurnState getState() {
        return state;
    }

    /**
     * Sets the state of the turn
     * If the provided state is {@code null} nothing happens
     *
     * @param state The object representing the state
     */
    public void setState(PlayerTurnState state) {
        if (state != null) {
            this.state = state;
        }
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

/**
 * Identifies the state of a turn
 *
 * @author Lorenzo Farinelli
 */
public enum PlayerTurnState {
    /**
     * The player has to spawn for the first time
     */
    INITIAL_SPAWN("Spawning for the first time"),
    /**
     * The player is choosing an action from the ActionCard or a powerup
     */
    SELECTING("Choosing an action or a powerup"),
    /**
     * The player is performing an action or using a powerup
     */
    BUSY("Performing an action or using a powerup"),
    /**
     * The turn is over
     */
    OVER("Turn is over");

    private final String message;

    PlayerTurnState(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

}

package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

/**
 * Represents the operational state of the player.
 */
public enum PlayerState {
    /** Connection is active and is ready to play */
    ONLINE("Online"),
    /**
     * The player may be suspended because he didn't perform an action  before timeout,
     * connection is still open.
     */
    SUSPENDED("Suspended");

    private final String message;

    PlayerState(String message) {
        this.message = message;
    }

    /**
     * Returns whether the player can be chosen to play a turn or not.
     * @return {@code true} if the player is online, {@code false} otherwise.
     */
    public boolean canPlay() {
        return this.equals(ONLINE);
    }

    @Override
    public String toString() {
        return message;
    }
}

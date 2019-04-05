package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

/**
 * Represents the operational state of the player.
 */
public enum PlayerState {
    /** Connection is active and is ready to play */
    ONLINE,
    /**
     * The player may be suspended because he didn't perform an action  before timeout,
     * connection is still open.
     */
    SUSPENDED,
    /** The player intentionally left the game or the connection closed for other reason */
    LEFT;

    /**
     * Returns whether the player can be chosen to play a turn or not.
     * @return {@code true} if the player is online, {@code false} otherwise.
     */
    public boolean canPlay() {
        return this.equals(ONLINE);
    }
}

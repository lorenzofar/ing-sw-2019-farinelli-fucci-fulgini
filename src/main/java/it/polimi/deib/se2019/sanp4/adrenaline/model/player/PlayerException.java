package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

/**
 * Generic exception of the player package
 */
public class PlayerException extends Exception {
    private static final long serialVersionUID = -6053146334910460140L;

    /**
     * Creates a new exception with specified message
     * @param message The message, not null
     */
    public PlayerException(String message) {
        super(message);
    }
}

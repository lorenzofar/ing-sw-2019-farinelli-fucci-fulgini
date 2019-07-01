package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

/**
 * An exception used when an operation targeting a player fails
 * because the player cannot be affected by it (e.g. removing a player from a square)
 */
public class PlayerNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6751640880343681499L;

    /**
     * Creates an exception with no message
     */
    public PlayerNotFoundException(){ super(); }

    /**
     * Creates an exception with a specific message
     *
     * @param message The message
     */
    public PlayerNotFoundException(String message){ super(message); }
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

/**
 * An exception used when an operation targeting a player fails
 * because the player cannot be affected by it (e.g. removing a player from a square)
 */
public class PlayerNotFoundException extends Exception {

    private static final long serialVersionUID = 6751640880343681499L;

    public PlayerNotFoundException(){ super(); }

    public PlayerNotFoundException(String message){ super(message); }
}

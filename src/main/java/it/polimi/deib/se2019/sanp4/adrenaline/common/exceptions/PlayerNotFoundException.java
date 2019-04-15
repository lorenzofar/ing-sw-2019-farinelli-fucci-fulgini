package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

/**
 * An exception used when an operation targeting a player fails
 * because the player cannot be affected by it (e.g. removing a player from a square)
 */
public class PlayerNotFoundException extends Exception {

    public PlayerNotFoundException(){ super(); }

    public PlayerNotFoundException(String message){ super(message); }
}

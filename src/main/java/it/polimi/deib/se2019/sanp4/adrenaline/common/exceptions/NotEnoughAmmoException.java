package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

/**
 * Thrown when a player has not enough ammo cubes to pay a cost.
 */
public class NotEnoughAmmoException extends Exception {

    /**
     * Constructs the exception and sets a suitable message.
     */
    public NotEnoughAmmoException() {
        super("Player has not enough ammo");
    }
}

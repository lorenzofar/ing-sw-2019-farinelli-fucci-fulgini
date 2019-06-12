package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons;

/**
 * Specifies if a weapon can only shoot in a constrained direction or not.
 * {@see AbstractWeapon}
 */
public enum ShootingDirectionEnum {
    /**
     * The weapon can shoot in any direction
     */
    ANY,
    /**
     * The weapon can only shoot in cardinal directions: once a particular direction is chosen for
     * the first target, it will be applied also to the other targets
     */
    CARDINAL
}

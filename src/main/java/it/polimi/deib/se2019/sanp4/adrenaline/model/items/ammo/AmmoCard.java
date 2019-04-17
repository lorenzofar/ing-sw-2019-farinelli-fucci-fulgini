package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

import java.util.Collections;
import java.util.Map;

/**
 * An immutable class representing an ammo card.
 * It contains ammo cubes and an optional powerup card
 */
public class AmmoCard {

    /** Unique identifier of the card */
    private int id;

    /** Map describing how many cubes are available for each color */
    private Map<AmmoCube, Integer> cubes;

    /** Indicates whether the card holds a powerup or not */
    private boolean holdingPowerup;

    /** Default constructor only to be used by Jackson */
    private AmmoCard(){}

    /**
     * Creates a new ammo card holding the specified cubes
     * @param id The identifier of the card
     * @param cubes The cubes contained in the card, not null and must have non-negative values
     * @param holdingPowerup {@code true} if the card holds a powerup card, {@code false} otherwise
     */
    public AmmoCard(int id, Map<AmmoCube, Integer> cubes, boolean holdingPowerup){
        if(cubes == null){
            throw new NullPointerException("Cubes map cannot be null");
        }
        // Here we check whether it exists a value of the map which is negative
        if(cubes.entrySet().stream().anyMatch(entry -> entry.getValue() < 0)){
            throw new IllegalArgumentException("Cubes amount cannot be negative");
        }
        this.id = id;
        this.cubes = cubes;
        this.holdingPowerup = holdingPowerup;
    }

    /**
     * Retrieves the id of the card
     * @return The id of the card
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the cubes contained in the card
     * @return An unmodifiable map containing the amount of cubes for each color
     */
    public Map<AmmoCube, Integer> getCubes() {
        return Collections.unmodifiableMap(cubes);
    }

    /**
     * Determines whether or not the card contains a powerup
     * @return {@code true} if the card holds a powerup card, {@code false} otherwise
     */
    public boolean isHoldingPowerup(){
        return holdingPowerup;
    }

    /**
     * Check if this AmmoCard is equal to another Object
     * @param obj object on which to test equality
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if(!(obj instanceof AmmoCard)) return false;
        return ((AmmoCard) obj).getId() == this.id;
    }

    @Override
    public int hashCode() {
        return 17 + 31 * id;
    }
}

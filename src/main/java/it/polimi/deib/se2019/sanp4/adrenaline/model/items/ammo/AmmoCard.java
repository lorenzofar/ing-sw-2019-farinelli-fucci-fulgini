package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

import java.util.Map;

/**
 * A class representing an ammo card.
 * It contains ammo cubes and an optional powerup card
 */
public class AmmoCard {

    /** Unique identifier of the card */
    private int id;

    /** Map describing how many cubes are available for each color */
    private Map<AmmoCube, Integer> cubes;

    /** Indicates whether the card holds a powerup or not */
    private boolean powerup;

    /**
     * Creates a new ammo card holding the specified cubes
     * @param id The identifier of the card
     * @param cubes The cubes contained in the card, not null and must have non-negative values
     * @param hasPowerUp {@code true} if the card holds a powerup card, {@code false} otherwise
     */
    AmmoCard(int id, Map<AmmoCube, Integer> cubes, boolean hasPowerUp){
        if(cubes == null){
            throw new NullPointerException("Cubes map cannot be null");
        }
        // Here we check whether it exists a value of the map which is negative
        if(cubes.entrySet().stream().anyMatch(entry -> entry.getValue() < 0)){
            throw new IllegalArgumentException("Cubes amount cannot be negative");
        }
        this.id = id;
        this.cubes = cubes;
        this.powerup = hasPowerUp;
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
     * @return The object representing cubes with associated quantities
     */
    public Map<AmmoCube, Integer> getCubes() {
        return cubes;
    }

    /**
     * Determines whether or not the card contains a powerup
     * @return {@code true} if the card holds a powerup card, {@code false} otherwise
     */
    public boolean hasPowerup(){
        return powerup;
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.model.items.effects;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;

import java.util.ArrayList;
import java.util.List;

/** A class describing a light representation of an effect provided by a weapon*/
public class EffectDescription {

    /** A unique identifier of the effect */
    private String id;

    /** A human-readable identifier of the effect */
    private String name;

    /** A human-readable description of what the effect does */
    private String description;

    /** The list of ammo cubes the user has to pay to use the effect */
    private List<AmmoCubeCost> cost;

    /**
     * Creates a new object describing an effect
     * @param id The id of the effect
     * @param name The name of the effect
     * @param description The description of the effect
     * @param cost The list of objects representing the cost of the effect
     */
    public EffectDescription(String id, String name, String description, List<AmmoCubeCost> cost){
        this.id = id;
        this.name = name;
        this.description = description;
        this.cost = cost;
    }

    /**
     * Retrieves the identifier of the effect
     * @return The id of the effect
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the name of the weapon
     * @return The name of the weapon
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the description of the effect
     * @return The description of the effect
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the cost of the effect
     * @return The list of objects representing the cost of the effect
     */
    public List<AmmoCubeCost> getCost(){
        return new ArrayList<>(cost);
    }
}

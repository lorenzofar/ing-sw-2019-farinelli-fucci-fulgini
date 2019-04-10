package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.effects.EffectDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * A class describing a light representation of a weapon
 * Used to represent the weapon card drawn by a player
 */
public class WeaponCard {

    /* A unique identifier of the weapon */
    private String id;

    /** A human-readable identifier of the weapon */
    private String name;

    /** The list of ammo cubes the user has to pay to use the weapon */
    private List<AmmoCubeCost> cost;

    /** The list of effects provided by the weapon */
    private List<EffectDescription> effects;

    /**
     * Creates a new weapon card
     * @param id The identifier of the weapon
     * @param name The name of the weapon
     * @param cost The list of objects representing the cost of the weapon
     */
    WeaponCard(String id, String name, List<AmmoCubeCost> cost){
        this.id = id;
        this.name = name;
        this.cost = cost;
    }

    /**
     * Retrieved the identifier of the weapon
     * @return The identifier of the weapon
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
     * Retrieves the cost of the weapon
     * @return The list of objects representing the cost of the weapon
     */
    public List<AmmoCubeCost> getCost() {
        return new ArrayList<>(cost);
    }

    /**
     * Retrieves the effects provided by the weapon
     * @return The list of objects describing the effects
     */
    public List<EffectDescription> getEffects(){
        return new ArrayList<>(effects);
    }
 }
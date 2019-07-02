package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** A class describing a light representation of an effect provided by a weapon*/
public class EffectDescription implements Serializable {

    private static final long serialVersionUID = -4617447984038595221L;

    /** A unique identifier of the effect */
    private String id;

    /** A human-readable identifier of the effect */
    private String name;

    /** A human-readable description of what the effect does */
    private String description;

    /** The list of ammo cubes the user has to pay to use the effect */
    private List<AmmoCubeCost> cost;

    /** Default constructor only to be used by Jackson */
    private EffectDescription(){
        cost = new ArrayList<>(0); /* Default cost is zero */
    }

    /**
     * Creates a new object describing an effect
     * @param id The id of the effect
     * @param name The name of the effect, not null and not an empty string
     * @param description The description of the effect, not null and not an empty string
     * @param cost The list of objects representing the cost of the effect, not null
     */
    public EffectDescription(String id, String name, String description, List<AmmoCubeCost> cost){
        if(name == null || description == null || cost == null){
            throw new NullPointerException("Found null parameters");
        }
        if(id.isEmpty() || name.isEmpty() || description.isEmpty()){
            throw new IllegalArgumentException("Effect descriptions cannot be empty");
        }
        if(cost.contains(null)){
            throw new NullPointerException("Cost list cannot contain null objects");
        }
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
     * @return An unmodifiable list of objects representing the cost of the effect
     */
    public List<AmmoCubeCost> getCost(){
        return Collections.unmodifiableList(cost);
    }

    @Override
    public boolean equals(Object obj){
        if(obj == this) return true;
        if(!(obj instanceof EffectDescription)) return false;
        return ((EffectDescription) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
}

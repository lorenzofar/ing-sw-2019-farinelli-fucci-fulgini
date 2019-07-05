package it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.CubeInterface;

import java.io.Serializable;
import java.util.Objects;


/**
 * An immutable class representing a powerup card
 * It has an id,a name, a description (defined by the type) and a color.
 * <p>
 * Two powerup cards are considered equal if they have the same type and color.
 * </p>
 *
 * @author Alessandro Fulgini, Lorenzo Farinelli, Tiziano Fucci
 */
public class PowerupCard implements CubeInterface, Serializable {

    private static final long serialVersionUID = 6130416192426740957L;

    private final PowerupEnum type;
    private final AmmoCube cubeColor;

    /**
     * Creates a new powerup card
     *
     * @param type      the type of the powerup effect
     * @param cubeColor the color of the powerup (for spawn and ammo)
     */
    @JsonCreator
    public PowerupCard(
            @JsonProperty("type") PowerupEnum type,
            @JsonProperty("cubeColor") AmmoCube cubeColor
    ) {
        if (type == null || cubeColor == null) {
            throw new NullPointerException("Found null parameters");
        }
        this.type = type;
        this.cubeColor = cubeColor;
    }

    /**
     * Returns the type of this powerup, i.e. the identifier of its effect
     *
     * @return The type of this powerup
     */
    public PowerupEnum getType() {
        return type;
    }

    /**
     * Returns the name of the powerup effect
     *
     * @return The name of the powerup effect
     */
    @JsonIgnore
    public String getName() {
        return type.getName();
    }

    /**
     * Returns a textual description of this powerup's effect
     *
     * @return The description of the effect
     */
    @JsonIgnore
    public String getDescription() {
        return type.getDescription();
    }

    /**
     * Returns the color of this powerup
     *
     * @return The color of this powerup
     */
    @Override
    public AmmoCube getCubeColor() {
        return cubeColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PowerupCard that = (PowerupCard) o;
        return type == that.type &&
                cubeColor == that.cubeColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, cubeColor);
    }
}

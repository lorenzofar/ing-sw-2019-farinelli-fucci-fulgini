package it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.CubeInterface;

import java.io.Serializable;
import java.util.Objects;


/**
 * An immutable class representing a powerup card
 * It has an id,a name, a description and a color
 */
public class PowerupCard implements CubeInterface, Serializable {

    private static final long serialVersionUID = 6130416192426740957L;

    private final PowerupEnum type;
    private final AmmoCube cubeColor;

    /**
     * Creates a new powerup card
     * @param type the type of the powerup effect
     * @param ammoCube the color of the powerup (for spawn and ammo)
     */
    @JsonCreator
    public PowerupCard(
            @JsonProperty("type") PowerupEnum type,
            @JsonProperty("ammoCube") AmmoCube ammoCube
    ) {
        if (type == null || ammoCube == null) {
            throw new NullPointerException("Found null parameters");
        }
        this.type = type;
        this.cubeColor = ammoCube;
    }

    public PowerupEnum getType() {
        return type;
    }

    public String getName() {
        return type.getName();
    }

    public String getDescription() {
        return type.getDescription();
    }

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

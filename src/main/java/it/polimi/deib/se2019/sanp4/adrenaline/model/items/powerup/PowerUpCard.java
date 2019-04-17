package it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.CubeInterface;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;


/**
 * An immutable class representing a powerup card
 * It has an id,a name, a description and a color
 */
public class PowerUpCard implements CubeInterface {

    private final String id;
    private final String name;
    private final String description;
    private final AmmoCube cubeColor;

    public PowerUpCard(String id, String name, String description, AmmoCube ammoCube) {
        if(id == null || name == null || description == null || ammoCube == null){
            throw new NullPointerException("Found null parameters");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.cubeColor = ammoCube;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public AmmoCube getCubeColor() {
        return cubeColor;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == this) return true;
        if(!(obj instanceof PowerUpCard)) return false;
        return ((PowerUpCard)obj).getId().equals(this.id) && ((PowerUpCard) obj).getCubeColor() == this.cubeColor;
     }

    @Override
    public int hashCode(){
        int result = 17;
        result += 31 * id.hashCode();
        result += 31 * cubeColor.hashCode();
        return result;
     }
}

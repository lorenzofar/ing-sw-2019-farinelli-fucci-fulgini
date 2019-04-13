package it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.CubeInterface;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;

/* TODO: Describe this */
public class PowerUpCard implements CubeInterface {

    private final String id;
    private final String name;
    private final String description;
    private final AmmoCube cubeColor;

    public PowerUpCard(String id, String name, String description, AmmoCube ammoCube) {
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
}

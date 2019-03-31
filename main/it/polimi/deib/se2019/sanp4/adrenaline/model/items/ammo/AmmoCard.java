package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

import java.util.Map;

public class AmmoCard {
    private int id;
    private Map<AmmoCube, Integer> cubes;
    private boolean powerUp;

    AmmoCard(int id, Map<AmmoCube, Integer> cubes, boolean hasPowerUp){
        this.id = id;
        this.cubes = cubes;
        this.powerUp = hasPowerUp;
    }
}

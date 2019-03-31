package it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.CubeInterface;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.effects.PowerUpEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;

public class PowerUpCard implements CubeInterface {

    private AmmoCube ammoCube;
    private int id;
    private PowerUpEffect effect;

    @Override
    public AmmoCube getcubeColor() {
        return null;
    }
}

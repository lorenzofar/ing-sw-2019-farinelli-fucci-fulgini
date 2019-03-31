package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.effects.Effect;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.List;

public class Weapon {

    private Effect baseEffect;
    private WeaponState state;

    private List<AmmoCube> cost;

    public boolean isUsable(){
        return false;
    };

    public void reload(Player player){};
    public void payUsageCost(Player player){};
    public void reset(){};
}
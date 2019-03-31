package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

public interface WeaponState{
    public static boolean isUsable(){
        return true;
    }
    public static void reload(Player player, Weapon weapon){};
    public static void unload(Player shooter, Weapon weapon){};
    public static void reset(Weapon weapon){};
}
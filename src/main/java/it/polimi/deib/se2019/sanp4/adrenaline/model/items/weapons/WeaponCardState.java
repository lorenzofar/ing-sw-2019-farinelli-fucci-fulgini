package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

public interface WeaponCardState {
    boolean isUsable();
    void reload(Player player, WeaponCard weapon);
    void unload(Player shooter, WeaponCard weapon);
    void reset(WeaponCard weapon);
}
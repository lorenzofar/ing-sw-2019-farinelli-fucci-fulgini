package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

public class PickupState implements WeaponCardState {
    @Override
    public boolean isUsable() {
        return false;
    }

    @Override
    public void reload(Player player, WeaponCard weapon) {
        /* TODO: Implement this */
    }

    @Override
    public void unload(Player shooter, WeaponCard weapon) {
        /* TODO: Implement this */
    }

    @Override
    public void reset(WeaponCard weapon) {
        /* TODO: Implement this */
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

public class LoadedState implements WeaponCardState {
    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public void reload(Player player, WeaponCard weapon) {
        /*
        Since the weapon is already loaded,
        this method has no effects on it
        and does not charge the player with the reload cost
         */
    }

    @Override
    public void unload(WeaponCard weapon) {
        weapon.setState(new UnloadedState());
    }

    @Override
    public void reset(WeaponCard weapon) {
        weapon.setState(new PickupState());
    }
}

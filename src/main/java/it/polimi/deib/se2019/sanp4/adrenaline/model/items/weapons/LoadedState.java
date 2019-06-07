package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

public class LoadedState extends WeaponCardState {

    private static final long serialVersionUID = -549790312074151192L;

    public LoadedState(){
        super("loaded");
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public void reload(Player player, WeaponCard weapon) {
        if(player == null || weapon == null){
            throw new NullPointerException("Found null parameters");
        }
        /*
        Since the weapon is already loaded,
        this method has no effects on it
        and does not charge the player with the reload cost
         */
    }
}

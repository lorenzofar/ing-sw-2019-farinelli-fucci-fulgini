package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.NotEnoughAmmoException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

public class UnloadedState extends WeaponCardState {

    UnloadedState(){
        super("unloaded");
    }

    @Override
    public boolean isUsable() {
        return false;
    }

    @Override
    public void reload(Player player, WeaponCard weapon) {
        /*
         In this state the weapon is completely unloaded,
         hence all the cubes have to be paid.
         */
        try{
            /*
             Here we have to create a new payment map,
             since the weapons can contain the ANY cube
             */
            // Check whether the players owns the weapon card
            if(!player.hasWeaponCard(weapon.getId())) {
                throw new CardNotFoundException();
            }
            player.payAmmo(weapon.getCost());
            weapon.setState(new LoadedState());
        }
        catch(NotEnoughAmmoException | CardNotFoundException ex){
            // Here the user does not have enough ammo to pay the reload cost
        }
    }
}


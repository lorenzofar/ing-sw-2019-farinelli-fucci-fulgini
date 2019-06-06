package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.NotEnoughAmmoException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.List;

public class PickupState extends WeaponCardState {

    private static final long serialVersionUID = -5853062872021530478L;

    PickupState(){
        super("pickup");
    }

    @Override
    public boolean isUsable() {
        return false;
    }

    @Override
    public void reload(Player player, WeaponCard weapon) {
        if(player == null || weapon == null){
            throw new NullPointerException("Found null parameters");
        }
        /*
         In this state the weapon is partially loaded,
         hence the first element of the cost list has not to be paid.
         We then charge the user only the remaining amount of cubes
         (if there are no other cubes, the user pays nothing and the weapon becomes loaded)

         We use try/catches to avoid setting the new state when the user cannot pay */
        try{
            // Get the cost of the weapon card
            List<AmmoCubeCost> weaponCost = weapon.getCost();
            if(weaponCost.size() > 1){
                // Do not consider the first cube, since it's already loaded
                weaponCost = weaponCost.subList(1, weaponCost.size());
                // Make the user pay the cubes
                player.payAmmo(weaponCost);
            }
            weapon.setState(new LoadedState());
        }
        catch(NotEnoughAmmoException e){
            // Here the user does not have enough ammo to pay the reload cost
        }
    }
}

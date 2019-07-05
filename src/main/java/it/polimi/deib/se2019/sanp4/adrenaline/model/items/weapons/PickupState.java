package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.NotEnoughAmmoException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * State of a weapon before being grabbed.
 * <p>
 * The first cube in the cost list is already paid, so reloading results in paying the others.
 * Since the weapon is not fully loaded, it's not able to shoot in this state
 * </p>
 * <p>
 * A weapon must be in this state if and only if it sits in the deck or in a square.
 * A player grabbing a weapon card must first pay its reload cost, and if he then discards a weapon
 * in any state, it must be brought back to this state by calling {@link #reset(WeaponCard)}
 * </p>
 *
 * @author Alessandro Fulgini, Lorenzo Farinelli
 */
public class PickupState extends WeaponCardState {

    private static final Logger logger = Logger.getLogger(PickupState.class.getName());

    private static final long serialVersionUID = -5853062872021530478L;

    /**
     * Creates a new state for a non-grabbed weapon
     */
    public PickupState() {
        super("pickup");
    }

    /**
     * Returns {@code false} because a weapon cannot shoot in this state
     *
     * @return {@code false}
     */
    @Override
    public boolean isUsable() {
        return false;
    }

    /**
     * Tries to reload the weapon by making the player pay its remaining cost
     * The player is asked to pay for the remaining ammo cubes (all but the first):
     * <ul>
     * <li>If he can pay, the weapon will be brought to a usable state</li>
     * <li>If he can't pay, the weapon will remain in the current state</li>
     * </ul>
     *
     * @param player The player who reloads the weapon, not null
     * @param weapon The weapon holding this state, not null
     */
    @Override
    public void reload(Player player, WeaponCard weapon) {
        if (player == null || weapon == null) {
            throw new NullPointerException("Found null parameters");
        }
        /*
         In this state the weapon is partially loaded,
         hence the first element of the cost list has not to be paid.
         We then charge the user only the remaining amount of cubes
         (if there are no other cubes, the user pays nothing and the weapon becomes loaded)

         We use try/catches to avoid setting the new state when the user cannot pay */
        try {
            // Get the cost of the weapon card
            List<AmmoCubeCost> weaponCost = weapon.getCost();
            if (weaponCost.size() > 1) {
                // Do not consider the first cube, since it's already loaded
                weaponCost = weaponCost.subList(1, weaponCost.size());
                // Make the user pay the cubes
                player.payAmmo(weaponCost);
            }
            weapon.setState(new LoadedState());
        } catch (NotEnoughAmmoException e) {
            logger.log(Level.FINE, () ->
                    String.format("Player %s has not been able to reload weapon %s", player.getName(), weapon.getId()));
        }
    }
}

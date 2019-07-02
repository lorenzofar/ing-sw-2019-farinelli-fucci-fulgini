package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.NotEnoughAmmoException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

/**
 * State of a weapon which has just shot.
 * <p>
 * A weapon in this state cannot be used to shoot, but it can either be reloaded
 * by paying its entire cost or it can be discarded which will bring it back to
 * its original state.
 * </p>
 */
public class UnloadedState extends WeaponCardState {

    private static final long serialVersionUID = 135834435035814091L;

    /**
     * Creates a new state for a weapon which has just shot
     */
    public UnloadedState() {
        super("unloaded");
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
     * Tries to reload the weapon by making the player pay its full cost
     * The player is asked to pay for all the ammo cubes in the cost list:
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
         In this state the weapon is completely unloaded,
         hence all the cubes have to be paid.
         */
        try {
            /*
             Here we have to create a new payment map,
             since the weapons can contain the ANY cube
             */
            // Check whether the players owns the weapon card
            if (!player.hasWeaponCard(weapon.getId())) {
                throw new CardNotFoundException();
            }
            player.payAmmo(weapon.getCost());
            weapon.setState(new LoadedState());
        } catch (NotEnoughAmmoException | CardNotFoundException ex) {
            // Here the user does not have enough ammo to pay the reload cost
        }
    }
}


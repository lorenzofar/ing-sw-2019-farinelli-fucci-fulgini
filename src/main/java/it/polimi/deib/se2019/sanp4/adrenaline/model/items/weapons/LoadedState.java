package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

/**
 * State of a fully-loaded weapon which is ready to shoot.
 * <p>
 * A weapon in this state cannot be reloaded, and if it shoots it must be unloaded
 * </p>
 */
public class LoadedState extends WeaponCardState {

    private static final long serialVersionUID = -549790312074151192L;

    /**
     * Creates a new loaded weapon state
     */
    public LoadedState() {
        super("loaded");
    }

    /**
     * Returns {@code true} because a weapon can shoot in this state
     *
     * @return {@code true}
     */
    @Override
    public boolean isUsable() {
        return true;
    }

    /**
     * The weapon remains in this state because it cannot be loaded any further
     * The player will pay no cost
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
        Since the weapon is already loaded,
        this method has no effects on it
        and does not charge the player with the reload cost
         */
    }
}

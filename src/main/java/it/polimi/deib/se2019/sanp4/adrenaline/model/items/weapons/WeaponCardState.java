package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.io.Serializable;

/**
 * Represents the state of a weapon card.
 * <p>
 * It provides methods to check whether the weapon can be used to shoot and also to transition to
 * a new state from this one.
 * </p>
 *
 * @author Alessandro Fulgini, Lorenzo Farinelli, Tiziano Fucci
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoadedState.class, name = "loaded"),
        @JsonSubTypes.Type(value = PickupState.class, name = "pickup"),
        @JsonSubTypes.Type(value = UnloadedState.class, name = "unloaded")})
public abstract class WeaponCardState implements Serializable {

    private static final long serialVersionUID = -3648739297317158981L;

    private String type;

    /**
     * Creates a new weapon card state, with a string identifier
     *
     * @param type A string which identifies the state, not null and unique for every state
     */
    protected WeaponCardState(String type) {
        this.type = type;
    }

    /**
     * Returns whether a weapon in this state can be used to shoot or not
     *
     * @return {@code true} if a weapon in this state can be used to shoot, {@code false otherwise}
     */
    @JsonIgnore
    public abstract boolean isUsable();

    /**
     * Reloads the weapon if it's not fully loaded.
     * The player is asked to pay for the remaining ammo cubes:
     * <ul>
     * <li>If he can pay, the weapon will be brought to a usable state</li>
     * <li>If he can't pay, the weapon will remain in the current state</li>
     * </ul>
     *
     * @param player The player who reloads the weapon, not null
     * @param weapon The weapon holding this state, not null
     */
    public abstract void reload(Player player, WeaponCard weapon);

    /**
     * The weapon is set to a state where it cannot be used because it has just shot.
     *
     * @param weapon The weapon holding this state, not null
     */
    public void unload(WeaponCard weapon) {
        weapon.setState(new UnloadedState());
    }

    /**
     * Resets the weapon to the state it has when it's on a square (before being grabbed)
     *
     * @param weapon The weapon holding this state, not null
     */
    public void reset(WeaponCard weapon) {
        weapon.setState(new PickupState());
    }

    @Override
    public String toString() {
        return type;
    }
}
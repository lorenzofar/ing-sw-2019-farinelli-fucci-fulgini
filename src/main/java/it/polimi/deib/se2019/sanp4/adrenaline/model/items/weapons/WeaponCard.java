package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.WeaponCardUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A class describing a light representation of a weapon
 * Holds information about:
 * <ul>
 * <li>A textual description of the effects of the weapon, its reload cost
 * and the additional cost for each effect.</li>
 * <li>
 * The state of the weapon, which determines if the weapon can be used to shoot or not
 * and also how much to pay to reload it. The state transitions can be performed by calling the
 * methods {@link #reload(Player)}, {@link #unload()} and {@link #reset()}.
 * </li>
 * </ul>
 *
 * @author Alessandro Fulgini, Lorenzo Farinelli, Tiziano Fucci
 */
public class WeaponCard extends Observable<ModelUpdate> implements Serializable {

    private static final long serialVersionUID = 2151651272278660643L;

    /**
     * A unique identifier of the weapon
     */
    private String id;

    /**
     * A human-readable identifier of the weapon
     */
    private String name;

    /**
     * The list of ammo cubes the user has to pay to use the weapon
     */
    private List<AmmoCubeCost> cost;

    /**
     * The list of effects provided by the weapon
     */
    private List<EffectDescription> effects;

    /**
     * Current state of this weapon
     */
    private WeaponCardState state;

    /**
     * Default constructor only to be used by Jackson
     */
    private WeaponCard() {
        /* Provide some reasonable default values */
        cost = new ArrayList<>(0); /* Default cost is zero */
        effects = new ArrayList<>(0);
        state = new PickupState();
    }

    /**
     * Creates a new weapon card
     *
     * @param id      The identifier of the weapon
     * @param name    The name of the weapon, not null and not an empty string
     * @param cost    The list of objects representing the cost of the weapon, not null
     * @param effects The list of objects representing description of the effects in this weapon, not null
     */
    public WeaponCard(String id, String name, List<AmmoCubeCost> cost, List<EffectDescription> effects) {
        if (id == null || name == null || cost == null || effects == null) {
            throw new NullPointerException("Found null parameters");
        }
        if (id.isEmpty()) {
            throw new IllegalArgumentException("Weapon id cannot be empty");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Weapon name cannot be empty");
        }
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.effects = effects;
        this.state = new PickupState();
    }

    /**
     * Reloads the weapon by calling the reload method in {@link WeaponCardState}
     * and notifies all the observers
     *
     * @param player the player who reloads the weapon
     */
    public void reload(Player player) {
        this.state.reload(player, this);
        this.notifyObservers(new WeaponCardUpdate(this));
    }

    /**
     * Unloads the weapon by calling the unload method in {@link WeaponCardState}
     * and notifies all the observers
     */
    public void unload() {
        this.state.unload(this);
        this.notifyObservers(new WeaponCardUpdate(this));
    }

    /**
     * Resets the weapon by calling the reset method in {@link WeaponCardState}
     * and notifies all the observer
     */
    public void reset() {
        this.state.reset(this);
        this.notifyObservers(new WeaponCardUpdate(this));
    }

    /**
     * Returns whether this weapon can be used to shoot or not
     *
     * @return {@code true} if the weapon can shoot, {@code false} otherwise
     */
    @JsonIgnore
    public boolean isUsable() {
        return this.state.isUsable();
    }

    /**
     * Retrieved the identifier of the weapon
     *
     * @return The identifier of the weapon
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the name of the weapon
     *
     * @return The name of the weapon
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the cost of the weapon
     *
     * @return The list of objects representing the cost of the weapon
     */
    public List<AmmoCubeCost> getCost() {
        return Collections.unmodifiableList(cost);
    }

    /**
     * Retrieves the effects provided by the weapon
     *
     * @return The list of objects describing the effects
     */
    public List<EffectDescription> getEffects() {
        return Collections.unmodifiableList(effects);
    }

    /**
     * Retrieves the loading state of the weapon
     *
     * @return The object representing the state
     */
    public synchronized WeaponCardState getState() {
        return state;
    }

    /**
     * Sets the loading state of the weapon
     *
     * @param state The object representing the state
     * @return The object representing the new state
     */
    public synchronized WeaponCardState setState(WeaponCardState state) {
        this.state = state;
        this.notifyObservers(new WeaponCardUpdate(this));
        return this.state;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof WeaponCard)) return false;
        return ((WeaponCard) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

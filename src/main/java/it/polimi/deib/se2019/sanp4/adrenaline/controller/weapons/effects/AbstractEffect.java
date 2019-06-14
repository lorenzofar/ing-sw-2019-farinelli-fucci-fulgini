package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PaymentHandler;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;

/**
 * Represents an effect that can be used in a weapon.
 * <p>
 * The effect is identified inside the weapon by its {@code id} and it can depend on other effects.
 * This means that it can't be executed if those effects have not been executed yet.
 * </p>
 * <p>
 * The effect can also have an additional cost that the user is obliged to pay in order to use it.
 * </p>
 * <p>
 * If the effect {@link #isOptional()} it means that it can be executed before/after a mandatory effect;
 * If it is not optional, it cannot.
 * </p>
 * <p>
 * An {@link AbstractEffect} can only be executed once.
 * </p>
 */
public abstract class AbstractEffect {

    private static final String MESSAGE_COULD_NOT_PAY = "You don't have enough resources to pay for this effect";

    protected final String id;

    protected final Match match;

    protected final ControllerFactory factory;

    protected List<AmmoCubeCost> cost;

    protected Set<String> dependsOnEffects;

    protected boolean optional;

    /**
     * Creates a new abstract effect with given id, attached to the given match and factory.
     * The cost of the effect is set to zero and it does not depend on any effect.
     *
     * @param id      Unique identifier of the effect in the weapon, not null
     * @param match   The match which has to be controlled, not null
     * @param factory The factory used to create needed controllers, not null
     */
    public AbstractEffect(String id, Match match, ControllerFactory factory) {
        this.id = id;
        this.match = match;
        this.factory = factory;
        this.cost = Collections.emptyList();
        this.dependsOnEffects = Collections.emptySet();
    }

    /**
     * Returns the unique identifier of this effect in its weapon.
     *
     * @return The unique identifier of this effect in its weapon.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the additional cost to use this effect.
     *
     * @return A map with the number of cubes of each type needed to use the effect.
     */
    public List<AmmoCubeCost> getCost() {
        return cost;
    }

    /**
     * Sets the cost to use this effect.
     *
     * @param cost A map with the number of cubes of each type needed to use the effect, not null
     */
    public void setCost(List<AmmoCubeCost> cost) {
        this.cost = cost;
    }

    /**
     * Returns whether this effect is optional or not.
     *
     * @return {@code true} if the effect is optional, {@code false} otherwise
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Sets the effect to be mandatory or optional.
     *
     * @param optional {@code true} if the effect has to be optional, {@code false} if it has to be mandatory.
     */
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    /**
     * Returns the set of effects that this effect depends on for its execution.
     *
     * @return The set with the ids of effects this effect depends on.
     */
    public Set<String> getDependsOnEffects() {
        return dependsOnEffects;
    }

    /**
     * Sets the set of effects that this effect depends on for its execution.
     *
     * @param dependsOnEffects The set with the ids of effects this effect has to depend on, not null
     */
    public void setDependsOnEffects(Set<String> dependsOnEffects) {
        this.dependsOnEffects = dependsOnEffects;
    }

    /* ====================== UTILITIES ========================= */

    /**
     * Makes the player pay for the additional cost of this effect.
     * The items used to pay are actually removed from the player.
     * If the player has not been able to pay the cost due to lack of items he gets notified.
     *
     * @param view The view of the player using the weapon, not null
     * @return {@code true} if the player has been able to pay, {@code false} otherwise
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    protected boolean payAdditionalCost(PersistentView view) throws InterruptedException {
        if (cost.isEmpty()) {
            return true; /* There is no cost to pay */
        }

        /* Ask the payment handler to handle the payment */
        PaymentHandler paymentHandler = factory.createPaymentHandler();
        boolean hasPaid = paymentHandler
                .payAmmoCost(view, AmmoCubeCost.mapFromCollection(cost), true);

        if (!hasPaid) {
            view.showMessage(MESSAGE_COULD_NOT_PAY, MessageType.WARNING);
        }
        return hasPaid;
    }

    /* ========================= USE ============================ */

    /**
     * Returns whether this effect can be used or not based on the effects that
     * have been successfully executed so far.
     * This will return {@code false} if an effect that this depends on has not been executed
     * or if this effect has already been used.
     *
     * @param completedEffects The set of ids of effects that have been successfully executed before this, not null
     * @return {@code true} if the effect can be used, {@code false} otherwise
     */
    public boolean canBeUsed(Set<String> completedEffects) {
        return completedEffects.containsAll(dependsOnEffects) && !completedEffects.contains(id);
    }

    /**
     * Makes the user with given view use this effect in the current state of the match,
     * including the payment of the additional cost to use the effect.
     * This method must be called only if the effect can actually be used. This can be checked by calling
     * {@link #canBeUsed(Set)} first.
     * The return value indicates whether the effect has been executed successfully or not.
     * An effect has been executed successfully if:
     * <ul>
     * <li>The player has been able to pay the additional cost to use it</li>
     * <li>The player has been able to execute all the mandatory actions in the effect (e.g. shooting targets)</li>
     * <li>If an effect is <i>optional</i> even a partial execution will return {@code true}</li>
     * </ul>
     *
     * @param view The view of the player who wants to use this weapon, not null
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    public abstract boolean use(PersistentView view) throws InterruptedException;
}

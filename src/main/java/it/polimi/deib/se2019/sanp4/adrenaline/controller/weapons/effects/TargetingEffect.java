package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets.AbstractTarget;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;

/**
 * Represents an effect which applies damage to targets.
 * <p>
 * A target is one player or a set of players which will be selected all together
 * and will receive the same amount of damage and marks.
 * </p>
 * <p>
 * The targets are inserted in the weapon sequentially.
 * Two targets may have the same id in the same effect, this means that if the latter relies on a value
 * saved on a {@link AbstractWeapon} (square or player), it will be automatically selected.
 * </p>
 * <p>
 * The targets are run sequentially, in the order in which they are added.
 * This means that mandatory targets <b>must</b> be inserted before <i>optional</i> targets.
 * </p>
 *
 * @see AbstractTarget
 * @author Alessandro Fulgini
 */
public class TargetingEffect extends AbstractEffect {

    protected final List<AbstractTarget> targets;

    /**
     * Creates a new targeting effect with given id, attached to the given match and factory.
     * The cost of the effect is set to zero and it does not depend on any effect.
     * The list of targets is initially empty.
     *
     * @param id      Unique identifier of the effect in the weapon, not null
     * @param match   The match which has to be controlled, not null
     * @param factory The factory used to create needed controllers, not null
     */
    public TargetingEffect(String id, Match match, ControllerFactory factory) {
        super(id, match, factory);
        targets = new LinkedList<>();
    }

    /**
     * Returns a list with the targets of this effect, in their execution order.
     *
     * @return An unmodifiable view of the list of targets in this effect
     */
    public List<AbstractTarget> getTargets() {
        return Collections.unmodifiableList(targets);
    }

    /**
     * Appends a target to the end of the list of targets to execute.
     *
     * @param target The target to be appended, not null
     */
    public void appendTarget(AbstractTarget target) {
        targets.add(target);
    }

    /**
     * Makes the user with given view use this effect in the current state of the match,
     * including the payment of the additional cost to use the effect.
     * This method must be called only if the effect can actually be used. This can be checked by calling
     * {@link #canBeUsed(Set)} first.
     * The return value indicates whether the effect has been executed successfully or not.
     * The execution proceeds in the following way:
     * <ol>
     * <li>The player is asked to pay the usage cost, if he can't the effect terminates
     * and returns {@code false}</li>
     * <li>If the execution of a mandatory target does not complete successfully
     * (e.g. the shooter cannot select a player to shoot), the execution terminates
     * (subsequent targets are not executed) and returns {@code false}</li>
     * <li>if the execution of an optional target fails, then the next ones are still executed
     * and the method will return {@code true}</li>
     * </ol>
     *
     * <p>
     * This behavior is due to the fact that targets in this effect or in effects that depend on this
     * might rely on squares and players saved in the weapon by this effect.
     * </p>
     *
     * @param view The view of the player who wants to use this weapon, not null
     * @return Whether the effect completed successfully or not
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    @Override
    public boolean use(PersistentView view) throws InterruptedException {

        /* Ask to pay the cost */
        boolean hasPaid = payAdditionalCost(view);

        if (!hasPaid) {
            return false; /* The player could not pay the cost */
        }

        for (AbstractTarget target : targets) {
            /* Execute the target */
            boolean targetCompleted = target.execute(view);

            /* If a mandatory target has not been shot, terminate */
            if (!targetCompleted && !target.isOptional()) {
                return false;
            }
        }

        /* If we get here, all mandatory targets have been executed successfully */
        return true;
    }
}

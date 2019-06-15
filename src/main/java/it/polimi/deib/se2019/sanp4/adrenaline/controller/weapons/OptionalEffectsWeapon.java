package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.EffectRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.AbstractEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.EffectDescription;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

/**
 * Controller for a weapon with one mandatory effect and optional effects.
 * <p>
 * The weapon must contain exactly one mandatory effect, also known as <i>basic effect</i>.
 * It can also contain other effects, known as <i>optional effects</i>, which can be used
 * in addition to the basic effect.
 * </p>
 * Each optional effect may depend on the execution of other effects:
 * <ul>
 * <li>If it depends on no effect, it can be executed before or after the basic effect</li>
 * <li>If it depends on other effects,it can only be executed if those effects have completed successfully</li>
 * <li>The mandatory effect must not depend on other effects</li>
 * </ul>
 */
public class OptionalEffectsWeapon extends AbstractWeapon {

    private static final String MESSAGE_SELECT_EFFECT = "Select the effect";

    private static final String MESSAGE_BASE_NOT_COMPLETED = "You could not complete the base effect. " +
            "The weapon will terminate";

    private Set<String> completedEffects;

    /**
     * Creates a new weapon controller, with no effects.
     *
     * @param weaponCard The weapon card associated to this weapon, not null
     * @param match      The match which has to be controlled, not null
     * @param factory    The factory needed to create other controllers, not null
     */
    public OptionalEffectsWeapon(WeaponCard weaponCard, Match match, ControllerFactory factory) {
        super(weaponCard, match, factory);
        completedEffects = new HashSet<>();
    }

    /**
     * Adds given effect to this weapon.
     * If the effect is mandatory and there is already a mandatory effect,
     * this throws {@link IllegalArgumentException}, since there can't be multiple mandatory effects.
     * If the effect is mandatory, it must not depend on other effects, if this is not true
     * an {@link IllegalArgumentException} is thrown.
     * If an effect with the same id is already present, the given effect is not added.
     *
     *
     * @param effect The effect to be added, not null
     * @throws IllegalArgumentException if the effect is mandatory and it depends on other effects
     * and if the effect is mandatory and there is already a mandatory effect
     */
    @Override
    public void addEffect(AbstractEffect effect) {
        if (!effect.isOptional()) { /* It's mandatory */
            /* Check dependencies */
            if (!effect.getDependsOnEffects().isEmpty()) {
                throw new IllegalArgumentException("The basic effect cannot depend on other effects");
            }

            /* Check if there is another basic effect */
            if (!effects.values().stream().allMatch(AbstractEffect::isOptional)) {
                throw new IllegalArgumentException("There is already a basic effect");
            }
        }
        /* All fine, add it */
        super.addEffect(effect);
    }

    private EffectDescription findEffectDescription(String id) {
        return weaponCard.getEffects().stream()
                .filter(ed -> ed.getId().equals(id))
                .findAny().orElse(null); /* Null should never be returned happens */
    }

    /**
     * Handles the selection of the effect to be used.
     * <ul>
     *     <li>If the base effect is among the choices, a selection will be made for sure</li>
     *     <li>If the base effect is the only choice, it is chose automatically</li>
     *     <li>If there are more choices or the base effect is not among the choices, the user is
     *     required to select one (or no) effect</li>
     * </ul>
     *
     * @param view    The view of the player using the weapon, not null
     * @param choices The available choices, not null and not empty
     * @return The selected choice, {@code null} if no selection
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    private AbstractEffect handleEffectSelection(PersistentView view, Set<AbstractEffect> choices)
            throws InterruptedException {
        /* Determine if the choice is mandatory or not */
        boolean optional = choices.stream().allMatch(AbstractEffect::isOptional);

        if (choices.size() > 1 || optional) {
            return askToSelectEffect(view, choices, optional);
        } else {
            /* Automatically select the base effect */
            return choices.iterator().next();
        }
    }

    /**
     * Asks the user to select one or no effect to use between the ones given
     *
     * @param view    The view of the player using the weapon, not null
     * @param choices The available choices, not null
     * @param optional Whether the user can select to use no effect or not
     * @return The selected choice, {@code null} if no selection
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    private AbstractEffect askToSelectEffect(PersistentView view, Set<AbstractEffect> choices, boolean optional)
            throws InterruptedException {
        /* Determine the effect descriptions */
        List<EffectDescription> effectDescriptions = choices.stream()
                .map(effect -> findEffectDescription(effect.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        /* Send the request */
        EffectRequest req = new EffectRequest(MESSAGE_SELECT_EFFECT, effectDescriptions, optional);
        EffectDescription selected = view.sendChoiceRequest(req).get();

        /* Get the corresponding effect controller */
        return selected != null ? effects.get(selected.getId()) : null;
    }

    /**
     * Makes the user with given view use this weapon in the current state of the match.
     * <p>
     *     The player can first execute effects that don't depend on the basic effect, if any.
     *     Then the basic effect executes automatically. If that fails, the weapon terminates.
     *     If it completes successfully, the player can execute the remaining effects based on their dependencies.
     *     The weapon terminates when there are no more effects to execute or the player chooses to execute no
     *     optional effect when asked.
     * </p>
     *
     * @param view The view of the player who wants to use this weapon, not null
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    @Override
    public void use(PersistentView view) throws InterruptedException {
        Set<AbstractEffect> usableEffects;
        AbstractEffect selectedEffect = null;

        do {
            /* Determine effects which can be used */
            usableEffects = effects.values().stream()
                    .filter(effect -> effect.canBeUsed(completedEffects))
                    .collect(Collectors.toSet());

            /* Ask user to select or auto-select if base */
            if (!usableEffects.isEmpty()) {
                selectedEffect = handleEffectSelection(view, usableEffects);
            }

            if (selectedEffect != null) {
                /* Execute it */
                boolean completed = selectedEffect.use(view);

                /* Check whether to add it to the collection of successfully completed effects */
                if (completed) {
                    completedEffects.add(selectedEffect.getId());
                } else if(!selectedEffect.isOptional()) {
                    /* If the base effect did not complete successfully, the weapon terminates */
                    view.showMessage(MESSAGE_BASE_NOT_COMPLETED, MessageType.WARNING);
                    break;
                }
            }

        } while (!usableEffects.isEmpty() && selectedEffect != null);
    }
}

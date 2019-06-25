package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.EffectRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.AbstractEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.EffectDescription;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.util.List;
import java.util.concurrent.CancellationException;

/**
 * Controller for a weapon where the player can use only one between alternative effects (a.k.a. modes)
 * All the effects are treated as mandatory, the player can't choose to execute no effect.
 * Also, the dependencies of effects are ignored, since only one can be executed.
 */
public class AlternativeModesWeapon extends AbstractWeapon {

    private static final String MESSAGE_SELECT_MODE = "Select the mode of the weapon";

    private static final String MESSAGE_MODE_NOT_COMPLETED = "You could not complete the selected mode";

    /**
     * Creates a new weapon controller, with no effects.
     *
     * @param weaponCard The weapon card associated to this weapon, not null
     * @param match      The match which has to be controlled, not null
     * @param factory    The factory needed to create other controllers, not null
     */
    public AlternativeModesWeapon(WeaponCard weaponCard, Match match, ControllerFactory factory) {
        super(weaponCard, match, factory);
    }

    /**
     * Asks the user to select a mode to use between the ones available
     *
     * @param view The view of the player using the weapon, not null
     * @return The selected choice
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    private AbstractEffect askToSelectMode(PersistentView view) throws InterruptedException {
        /* Determine the effect descriptions */
        List<EffectDescription> choices = weaponCard.getEffects();

        /* Send the request */
        EffectRequest req = new EffectRequest(MESSAGE_SELECT_MODE, choices, false);
        EffectDescription selected = view.sendChoiceRequest(req).get();

        /* Get the corresponding effect controller */
        return effects.get(selected.getId());
    }

    /**
     * Makes the user with given view use this weapon in the current state of the match.
     * If there is more than one mode available, the user is asked to select which to use.
     *
     * @param view The view of the player who wants to use this weapon, not null
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    @Override
    public void use(PersistentView view) throws InterruptedException {
        if (effects.isEmpty()) return; /* The weapon has no effects */

        AbstractEffect selectedMode;
        if (effects.size() == 1) {
            /* There is only one mode => select it automatically */
            selectedMode = effects.values().iterator().next();
        } else {
            /* Ask the user to select the effect he desires */
            selectedMode = askToSelectMode(view);
        }

        /* Use it */
        boolean completed = selectedMode.use(view);

        if (!completed) {
            /* Notify the user */
            view.showMessage(MESSAGE_MODE_NOT_COMPLETED, MessageType.WARNING);
        }
    }
}

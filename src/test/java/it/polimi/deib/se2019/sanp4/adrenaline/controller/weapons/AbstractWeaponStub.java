package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;

import java.util.concurrent.CancellationException;

/**
 * Stub implementation of the {@link AbstractWeapon} class used for testing purposes.
 * All the default methods are left untouched, while the {@link #use(PersistentView)} method does nothing.
 */
public class AbstractWeaponStub extends AbstractWeapon {

    public AbstractWeaponStub(WeaponCard weaponCard, Match match, ControllerFactory factory) {
        super(weaponCard, match, factory);
    }

    /**
     * Makes the user with given view use this weapon in the current state of the match
     *
     * @param view The view of the player who wants to use this weapon, not null
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    @Override
    public void use(PersistentView view) throws InterruptedException {
        /* Do nothing here */
    }
}

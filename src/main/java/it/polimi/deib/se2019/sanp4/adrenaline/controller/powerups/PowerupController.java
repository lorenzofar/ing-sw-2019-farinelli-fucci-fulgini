package it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;

import java.util.concurrent.CancellationException;

/**
 * Interface for the controller of a specific powerup effect.
 * <p>
 * It only controls the effect, not the logic for selecting the powerup card and/or discarding it
 * after usage.
 * </p>
 * @author Alessandro Fulgini
 */
public interface PowerupController {

    /**
     * Makes the player associated to given view use this powerup.
     * <p>
     * At first the method checks if this powerup can be used (e.g. conditions on damage or proximity to
     * other players), it does not check if the player has a powerup of this type.
     * </p>
     * <p>
     * If the requirements to use the powerup are not met, the user will get a message notification
     * and the method returns {@code false}.
     * </p>
     * <p>
     * The return value indicates whether the player has been able to use the powerup (completely or partially)
     * and thus if the powerup card has to be discarded or not.
     * </p>
     *
     * @param view The view of the player who uses the powerup, not null
     * @return {@code true} if the powerup has been used, {@code false} if it hasn't been used
     * @throws CancellationException If a request to the user gets cancelled
     * @throws InterruptedException If the thread gets interrupted
     */
    boolean use(PersistentView view) throws InterruptedException;
}

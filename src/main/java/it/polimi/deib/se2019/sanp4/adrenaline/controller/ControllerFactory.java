package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.action.GrabActionController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.action.MoveActionController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.action.ReloadActionController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.action.ShootActionController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.match.MatchController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.match.SpawnController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.match.TurnController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups.PowerupController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.AbstractEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets.AbstractTarget;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurn;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Abstract factory used by controllers to create the other controllers they need.
 * The factory is bound to a specific {@link Match} and map of {@link PersistentView}s,
 * which are injected as dependencies as needed
 */
public interface ControllerFactory {

    /**
     * Creates the controller for the match of this factory
     *
     * @return the controller for the match of this factory
     */
    MatchController createMatchController();

    /**
     * Creates a new controller for the current turn of the match.
     *
     * @param turn The turn to be controlled, not null
     * @return The controller for the current turn
     * @throws NullPointerException If the turn is null
     */
    TurnController createTurnController(PlayerTurn turn);

    /**
     * Creates the spawn controller associated to the match of this factory
     *
     * @return the spawn controller associated to the match of this factory
     */
    SpawnController createSpawnController();

    /**
     * Returns the score manager for this match
     *
     * @return the score manager for this match
     */
    ScoreManager createScoreManager();

    /**
     * Returns the payment handler for this match
     *
     * @return the payment handler for this match
     */
    PaymentHandler createPaymentHandler();

    /**
     * Creates the controller for the Move basic action
     *
     * @return the controller for the Move basic action
     */
    MoveActionController createMoveActionController();

    /**
     * Creates the controller for a single Grab basic action
     *
     * @param view The view of the player who wants to perform the action, not null
     * @return The controller for the Grab basic action
     */
    GrabActionController createGrabActionController(PersistentView view);

    /**
     * Creates the controller for the Reload basic action
     *
     * @return The controller for the Reload basic action
     */
    ReloadActionController createReloadActionController();

    /**
     * Creates the controller for the Shoot basic action
     *
     * @return The controller for the Reload basic action
     */
    ShootActionController createShootActionController();

    /**
     * Creates the controller for a specific powerup effect
     *
     * @param type The type of the powerup effect, not null
     * @return The controller for the powerup effect
     */
    PowerupController createPowerupController(PowerupEnum type);

    /**
     * Creates the controller for a specific weapon
     * <p>
     * The controller will be set up with all the effects and their targets,
     * so it's ready to use.
     * </p>
     *
     * @param weaponCard The card of the weapon to be controlled, not null
     * @return The controller for the weapon
     * @throws JSONException if there is a problem with the JSON configuration
     */
    AbstractWeapon createWeaponController(WeaponCard weaponCard);

    /**
     * Creates the controller for a specific weapon effect
     * <p>
     * The controller will be set up with all the needed targets,
     * so it's ready to use.
     * </p>
     * <p>
     * The required configuration fields are {@code id} and {@code type}, the other ones
     * will receive default values if not specified in the JSON.
     * </p>
     * <p>
     * It's advisable to validate the JSON before calling this method.
     * </p>
     *
     * @param weapon The weapon this effect belongs to, not null
     * @param config A JSON tree with the configuration of the effect, not null
     * @return The effect controller
     * @throws JSONException if the JSON does not conform to the specific
     */
    AbstractEffect createEffectController(AbstractWeapon weapon, JSONObject config);

    /**
     * Creates the controller for a specific weapon target
     * <p>
     * The required configuration fields are {@code id} and {@code type}, the other ones
     * will receive default values if not specified in the JSON.
     * </p>
     * <p>
     * It's advisable to validate the JSON before calling this method.
     * </p>
     *
     * @param weapon The weapon this target belongs to, not null
     * @param config A JSON tree with the configuration of the target, not null
     * @return The target controller
     * @throws JSONException if the JSON does not conform to the specific
     */
    AbstractTarget createTargetController(AbstractWeapon weapon, JSONObject config);
}

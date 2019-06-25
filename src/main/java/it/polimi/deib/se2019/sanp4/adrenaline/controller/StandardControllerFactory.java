package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.action.GrabActionController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.action.MoveActionController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.action.ReloadActionController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.action.ShootActionController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.match.MatchController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.match.SpawnController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.match.TurnController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups.*;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AlternativeModesWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.OptionalEffectsWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.ShootingDirectionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.AbstractEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.MovementEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.TargetingEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurn;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Default implementation of controller factory
 */
public class StandardControllerFactory implements ControllerFactory {

    /**
     * The match associated to this factory
     */
    private final Match match;

    /**
     * The views of the players playing in the match
     */
    private final Map<String, PersistentView> views;

    /* Match-wide controllers */
    private final SpawnController spawnController;

    private final ScoreManager scoreManager;

    private final PaymentHandler paymentHandler;

    private final MoveActionController moveActionController;

    private final ReloadActionController reloadActionController;

    private final ShootActionController shootActionController;

    private final Map<PowerupEnum, PowerupController> powerupControllers;

    /**
     * Creates a factory associated to the given match and views of the players,
     * which will be injected as dependencies where needed.
     *
     * @param match the match to be controlled, not null
     * @param views the persistent views of the players, not null
     */
    public StandardControllerFactory(Match match, Map<String, PersistentView> views) {
        if (match == null || views == null) {
            throw new NullPointerException("Match and views cannot be null");
        }
        this.match = match;
        this.views = views;

        /* Create match-wide controllers */
        spawnController = new SpawnController(match);
        scoreManager = new StandardScoreManager();
        paymentHandler = new PaymentHandler(match);
        moveActionController = new MoveActionController(match);
        reloadActionController = new ReloadActionController(match, this);
        shootActionController = new ShootActionController(match, views, this);

        /* Create powerup controllers */
        powerupControllers = new EnumMap<>(PowerupEnum.class);
        powerupControllers.put(PowerupEnum.TARGETING_SCOPE, new TargetingScopeController(match, this));
        powerupControllers.put(PowerupEnum.NEWTON, new NewtonController(match));
        powerupControllers.put(PowerupEnum.TAGBACK, new TagbackController(match));
        powerupControllers.put(PowerupEnum.TELEPORTER, new TeleporterController(match));
    }

    /**
     * Creates the controller for the match of this factory
     *
     * @return the controller for the match of this factory
     */
    @Override
    public MatchController createMatchController() {
        return new MatchController(match, views, this);
    }

    /**
     * Creates a new controller for the turn of the match.
     *
     * @param turn The turn to be controlled, not null
     * @throws NullPointerException If the turn is null
     */
    @Override
    public TurnController createTurnController(PlayerTurn turn) {
        return new TurnController(turn, match, views, this);
    }

    /**
     * Creates the spawn controller associated to the match of this factory
     *
     * @return the spawn controller associated to the match of this factory
     */
    @Override
    public SpawnController createSpawnController() {
        return spawnController;
    }

    /**
     * Returns the score manager for this match
     *
     * @return the score manager for this match
     */
    @Override
    public ScoreManager createScoreManager() {
        return scoreManager;
    }

    /**
     * Returns the payment handler for this match
     *
     * @return the payment handler for this match
     */
    @Override
    public PaymentHandler createPaymentHandler() {
        return paymentHandler;
    }

    /* =================== ACTIONS ===================== */

    /**
     * Creates the controller for the Move basic action
     *
     * @return the controller for the Move basic action
     */
    @Override
    public MoveActionController createMoveActionController() {
        return moveActionController;
    }

    /**
     * Creates the controller for a single Grab basic action
     *
     * @param view The view of the player who wants to perform the action, not null
     * @return The controller for the Grab basic action
     */
    @Override
    public GrabActionController createGrabActionController(PersistentView view) {
        return new GrabActionController(match, view, this);
    }

    /**
     * Creates the controller for the Reload basic action
     *
     * @return The controller for the Reload basic action
     */
    @Override
    public ReloadActionController createReloadActionController() {
        return reloadActionController;
    }

    /**
     * Creates the controller for the Shoot basic action
     *
     * @return The controller for the Reload basic action
     */
    @Override
    public ShootActionController createShootActionController() {
        return shootActionController;
    }

    /* =================== POWERUPS ==================== */

    /**
     * Creates the controller for a specific powerup effect
     *
     * @param type The type of the powerup effect, not null
     * @return The controller for the powerup effect
     */
    @Override
    public PowerupController createPowerupController(PowerupEnum type) {
        return powerupControllers.get(type);
    }

    /* =================== WEAPONS ===================== */

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
    @Override
    public AbstractWeapon createWeaponController(WeaponCard weaponCard) {
        AbstractWeapon weapon;

        /* Get the JSON config and the type */
        JSONObject config = WeaponCreator.getWeaponConfiguration(weaponCard.getId());
        String type = config.getString("type");

        switch (type) {
            case "OPTIONAL_EFFECTS":
                weapon = new OptionalEffectsWeapon(weaponCard, match, this);
                break;
            case "ALTERNATIVE_MODES":
                weapon = new AlternativeModesWeapon(weaponCard, match, this);
                break;
            default:
                throw new JSONException(String.format("Weapon type %s does not exist", type));
        }

        /* Shooting direction */
        if (config.has("direction")) {
            weapon.setShootingDirection(config.getEnum(ShootingDirectionEnum.class, "direction"));
        }

        /* Effects */
        JSONArray array = config.getJSONArray("effects");
        for (int i = 0; i < array.length(); i++) {
            /* Create each effect */
            JSONObject effectConfig = array.getJSONObject(i);
            AbstractEffect effect = createEffectController(weapon, effectConfig);

            /* Add it to the weapon */
            weapon.addEffect(effect);
        }

        return weapon;
    }

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
    @Override
    public AbstractEffect createEffectController(AbstractWeapon weapon, JSONObject config) {
        /* Read the mandatory fields */
        String id = config.getString("id");
        String type = config.getString("type");

        /* Build the corresponding effect */
        switch (type) {
            case "MODE":
            case "BASIC":
                TargetingEffect mandatoryEffect = new TargetingEffect(id, match, this);
                setupTargetingEffect(weapon, mandatoryEffect, config); /* Add cost and targets */
                mandatoryEffect.setOptional(false); /* Always mandatory */
                /* Does not depend on other effects */
                return mandatoryEffect;
            case "OPTIONAL":
                TargetingEffect optionalEffect = new TargetingEffect(id, match, this);
                setupTargetingEffect(weapon, optionalEffect, config); /* Add cost and targets */
                optionalEffect.setOptional(true);
                /* May depend on other effects */
                setupDependsOnEffects(optionalEffect, config);
                return optionalEffect;
            case "MOVEMENT":
                MovementEffect movementEffect = new MovementEffect(id, match, this);
                setupMovementEffect(movementEffect, config);
                return movementEffect;
            default:
                throw new JSONException(String.format("Effect type %s does not exist", type));
        }
    }

    /**
     * Sets the proper fields for an abstract effect, reading them from config
     *
     * @param effect The effect to be configured, not null
     * @param config The JSON configuration, not null
     * @throws JSONException if the JSON does not conform to the specific
     */
    private void setupAbstractEffect(AbstractEffect effect, JSONObject config) {
        /* Cost */
        if (config.has("cost")) {
            List<AmmoCubeCost> cost = JSONUtils.arrayToEnumList(AmmoCubeCost.class, config.getJSONArray("cost"));
            effect.setCost(cost);
        }
    }

    /**
     * Sets the proper fields for a targeting effect, reading them from config
     * <p>
     * Also includes the fields for its super-classes and adding the target controllers
     * </p>
     *
     * @param weapon The weapon this effect belongs to, not null
     * @param effect The effect to be configured, not null
     * @param config The JSON configuration, not null
     * @throws JSONException if the JSON does not conform to the specific
     */
    private void setupTargetingEffect(AbstractWeapon weapon, TargetingEffect effect, JSONObject config) {
        /* Super-class */
        setupAbstractEffect(effect, config);

        /* Read the array of targets */
        JSONArray array = config.getJSONArray("targets");

        for (int i = 0; i < array.length(); i++) {
            /* Configure each target */
            JSONObject targetConfig = array.getJSONObject(i);
            AbstractTarget target = createTargetController(weapon, targetConfig);

            /* Add it to the effect */
            effect.appendTarget(target);
        }
    }

    /**
     * Sets the proper fields for a movement effect, reading them from config
     * <p>
     * Also includes the fields for its super-classes
     * </p>
     *
     * @param effect The effect to be configured, not null
     * @param config The JSON configuration, not null
     * @throws JSONException if the JSON does not conform to the specific
     */
    private void setupMovementEffect(MovementEffect effect, JSONObject config) {
        /* Super-class */
        setupAbstractEffect(effect, config);

        /* Always optional */
        effect.setOptional(true);

        /* Number of steps */
        effect.setMaxMoves(config.optInt("playerMoves", 0));

        /* Depends on effects */
        setupDependsOnEffects(effect, config);
    }

    /**
     * Sets the {@code dependsOnEffects} field in an effect, reading it from config
     *
     * @param effect The effect to be configured, not null
     * @param config The JSON configuration, not null
     * @throws JSONException if the JSON does not conform to the specific
     */
    private void setupDependsOnEffects(AbstractEffect effect, JSONObject config) {
        if (config.has("dependsOnEffects")) {
            Set<String> set = JSONUtils.arrayToStringSet(config.getJSONArray("dependsOnEffects"));
            effect.setDependsOnEffects(set);
        }
    }

    /**
     * Creates the controller for a specific weapon target
     * <p>
     * The required configuration fields are {@code id} and {@code targetMode}, the other ones
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
    @Override
    public AbstractTarget createTargetController(AbstractWeapon weapon, JSONObject config) {
        /* Read the mandatory fields */
        String id = config.getString("id");
        String targetMode = config.getString("targetMode");

        /* Build the corresponding target */
        switch (targetMode) {
            case "PLAYER":
                PlayerTarget playerTarget = new PlayerTarget(id, weapon, match, this);
                setupPlayerTarget(playerTarget, config);
                return playerTarget;
            case "SQUARE":
                SquareTarget squareTarget = new SquareTarget(id, weapon, match, this);
                setupSingleSquareTarget(squareTarget, config);
                return squareTarget;
            case "ROOM":
                RoomTarget roomTarget = new RoomTarget(id, weapon, match, this);
                setupAbstractTarget(roomTarget, config);
                return roomTarget;
            case "RADIUS":
                RadiusTarget radiusTarget = new RadiusTarget(id, weapon, match, this);
                setupAbstractTarget(radiusTarget, config);
                return radiusTarget;
            case "VORTEX":
                VortexTarget vortexTarget = new VortexTarget(id, weapon, match, this);
                setupAbstractTarget(vortexTarget, config);
                return vortexTarget;
            default:
                throw new JSONException(String.format("Target type %s does not exist", targetMode));
        }
    }

    /**
     * Sets the proper fields for an abstract target, reading them from config
     *
     * @param target The target to be configured, not null
     * @param config The JSON configuration, not null
     * @throws JSONException if the JSON does not conform to the specific
     */
    private void setupAbstractTarget(AbstractTarget target, JSONObject config) {
        Set<String> set;

        /* Optional */
        target.setOptional(config.optBoolean("optional", false));

        /* Damage and marks */
        target.setDamage(config.optInt("damage", 0));
        target.setMarks(config.optInt("marks", 0));

        /* Visibility */
        if (config.has("visibility")) {
            target.setVisibility(config.getEnum(VisibilityEnum.class, "visibility"));
        }

        /* Minimum and maximum distance */
        if (config.has("minDist")) {
            target.setMinDist(config.optInt("minDist"));
        }
        if (config.has("maxDist")) {
            target.setMaxDist(config.optInt("maxDist"));
        }

        /* Excluded players and squares */
        if (config.has("excludePlayers")) {
            set = JSONUtils.arrayToStringSet(config.getJSONArray("excludePlayers"));
            target.setExcludePlayers(set);
        }
        if (config.has("excludeSquares")) {
            set = JSONUtils.arrayToStringSet(config.getJSONArray("excludeSquares"));
            target.setExcludeSquares(set);
        }
    }

    /**
     * Sets the proper fields for a single square target, reading them from config
     * <p>
     * Also includes the fields for its super-classes
     * </p>
     *
     * @param target The target to be configured, not null
     * @param config The JSON configuration, not null
     * @throws JSONException if the JSON does not conform to the specific
     */
    private void setupSingleSquareTarget(SingleSquareTarget target, JSONObject config) {
        Set<String> set;

        /* Set up super-class */
        setupAbstractTarget(target, config);

        /* Choose between targets */
        if (config.has("chooseBetweenTargets")) {
            set = JSONUtils.arrayToStringSet(config.getJSONArray("chooseBetweenTargets"));
            target.setChooseBetweenTargets(set);
        }

        /* Visible from player */
        if (config.has("visibleFromPlayer")) {
            target.setVisibleFromPlayer(config.getString("visibleFromPlayer"));
        }

        /* Move shooter here */
        target.setMoveShooterHere(config.optBoolean("moveShooterHere", false));

        /* Square reference */
        if (config.has("squareRef")) {
            target.setSquareRef(config.getString("squareRef"));
        }
    }

    /**
     * Sets the proper fields for a player target, reading them from config
     * <p>
     * Also includes the fields for its super-classes
     * </p>
     *
     * @param target The target to be configured, not null
     * @param config The JSON configuration, not null
     * @throws JSONException if the JSON does not conform to the specific
     */
    private void setupPlayerTarget(PlayerTarget target, JSONObject config) {
        /* Set up super-class */
        setupSingleSquareTarget(target, config);

        /* Move target before and after */
        target.setMoveTargetBefore(config.optInt("moveTargetBefore", 0));
        target.setMoveTargetAfter(config.optInt("moveTargetAfter", 0));
    }

    /* =================== GETTERS ===================== */

    Match getMatch() {
        return match;
    }

    Map<String, PersistentView> getViews() {
        return views;
    }
}

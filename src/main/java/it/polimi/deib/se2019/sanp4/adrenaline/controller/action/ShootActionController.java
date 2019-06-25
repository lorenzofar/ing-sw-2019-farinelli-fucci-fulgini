package it.polimi.deib.se2019.sanp4.adrenaline.controller.action;

import it.polimi.deib.se2019.sanp4.adrenaline.common.AdrenalineProperties;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PowerupCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.WeaponCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups.PowerupController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurn;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Responsible for the basic action "Shoot"
 * <p>
 * The instance can be reused for multiple actions.
 * </p>
 */
public class ShootActionController {

    private static final String MESSAGE_SELECT_WEAPON = "Select the weapon";

    private static final String WARNING_NO_WEAPONS = "You don't have any loaded weapon";

    private static final String MESSAGE_OFFER_TAGBACK = "You received damage from %s, use one of these powerups" +
            " to give him a mark";

    private final int revengeTimeout;

    private final Match match;

    private final ControllerFactory factory;

    private final Map<String, PersistentView> views;

    /**
     * Creates a new controller for the basic action "Shoot", associated to the given match
     *
     * @param match   The match to be controlled, not null
     * @param views   The views of the players on the match with username as key, not null
     * @param factory The controller factory for this match, not null
     */
    public ShootActionController(Match match, Map<String, PersistentView> views, ControllerFactory factory) {
        this.match = match;
        this.views = views;
        this.factory = factory;

        revengeTimeout = Integer.parseInt((String) AdrenalineProperties.getProperties()
                .getOrDefault("adrenaline.timeout.revenge", "30"));
    }

    /**
     * Offers a chance to use the {@link PowerupEnum#TAGBACK} powerup to a given player
     *
     * @param victim  The player who received the damage, not null
     * @throws CancellationException If a request to the user gets cancelled
     * @throws InterruptedException  If the thread gets interrupted
     */
    private void handleRevenge(Player victim) throws InterruptedException {
        /* Check if the damaged player has TAGBACK powerups to revenge */
        List<PowerupCard> powerups = victim.getPowerups().stream()
                .filter(powerupCard -> powerupCard.getType() == PowerupEnum.TAGBACK)
                .collect(Collectors.toList());

        if (powerups.isEmpty()) {
            return; /* Can't revenge */
        }

        /* Ask the user to choose one or no powerup */
        PersistentView damagedView = views.get(victim.getName());
        damagedView.startTimer(() -> null, revengeTimeout, TimeUnit.SECONDS);
        PowerupCardRequest req = new PowerupCardRequest(MESSAGE_OFFER_TAGBACK, powerups, true);
        PowerupCard selected = damagedView.sendChoiceRequest(req).get();

        if (selected == null) {
            return; /* He chose not to use a powerup */
        }

        /* Create the controller for that powerup and use it */
        PowerupController powerupController = factory.createPowerupController(PowerupEnum.TAGBACK);
        powerupController.use(damagedView); /* Execution goes fine by default */

        /* Discard the powerup card */
        victim.removePowerup(selected);
        match.getPowerupStack().discard(selected);

        /* Stop the timer */
        damagedView.stopTimer();
    }

    /**
     * Starts executing the shoot action for the player associated to the given view.
     * <p>
     * If the player has loaded weapons, he'll be asked to choose one of them to shoot.
     * If he doesn't, he'll be notified and the action terminates.
     * </p>
     * <p>
     * After shooting, the damaged  players are saved into the turn, then those among them
     * who have TAGBACK grenade will be asked if they want to use it for revenge.
     * </p>
     *
     * @param view The view of the player who executes the action, not null
     * @throws CancellationException If a request to the user gets cancelled
     * @throws InterruptedException  If the thread gets interrupted
     */
    public void execute(PersistentView view) throws InterruptedException {
        Player shooter = match.getPlayerByName(view.getUsername());

        /* Determine the weapons that the player can use */
        List<WeaponCard> weapons = shooter.getWeapons().stream()
                .filter(WeaponCard::isUsable)
                .collect(Collectors.toList());

        if (weapons.isEmpty()) {
            /* Notify the user and terminate */
            view.showMessage(WARNING_NO_WEAPONS, MessageType.WARNING);
            return;
        }

        /* Ask the user to select a weapon */
        WeaponCardRequest req = new WeaponCardRequest(MESSAGE_SELECT_WEAPON, weapons, false);
        WeaponCard selectedWeapon = view.sendChoiceRequest(req).get();

        /* Create the controller for that weapon */
        AbstractWeapon weaponController = factory.createWeaponController(selectedWeapon);

        try {
            /* Let the controller handle the shooting */
            weaponController.use(view);
        } finally {
            /* Flush damaged players to the turn */
            Set<Player> damagedPlayers = weaponController.getDamagedPlayers();
            PlayerTurn currentTurn = match.getCurrentTurn();
            damagedPlayers.forEach(currentTurn::addDamagedPlayer);

            /* Unload the weapon */
            selectedWeapon.unload();
        }

        /* Damaged players get a chance to revenge with TAGBACK */
        Set<Player> damagedPlayers = weaponController.getDamagedPlayers();
        for (Player victim : damagedPlayers) {
            try {
                handleRevenge(victim);
            } catch (CancellationException e) {
                /* We just go on with the next player: he didn't use the powerup */
            }
        }
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PlayerRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PaymentHandler;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurn;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

/**
 * Controller for the powerup effect {@link PowerupEnum#TARGETING_SCOPE}.
 * <p>
 * A player may ues this during its turn when there are <i>damaged players</i>.
 * He will pay an ammo of any color and deal an additional damage to one of those players, which he's asked to select.
 * </p>
 */
public class TargetingScopeController implements PowerupController {

    private static final Map<AmmoCubeCost, Integer> cost = Collections.singletonMap(AmmoCubeCost.ANY, 1);

    private static final int DAMAGE = 1;

    private static final String MESSAGE_SELECT_PLAYER = "Select the player who will get the additional damage";

    private static final String MESSAGE_CANT_USE = "You can't use this powerup: you haven't damaged any players" +
            "during this turn";

    private static final String MESSAGE_CANT_PAY = "You can't pay to use this powerup";

    private final Match match;

    private final PaymentHandler paymentHandler;

    /**
     * Creates a new controller for the powerup effect {@link PowerupEnum#TARGETING_SCOPE}
     *
     * @param match   The match to be controlled, not null
     * @param factory The factory to create other controllers, not null
     */
    public TargetingScopeController(Match match, ControllerFactory factory) {
        this.match = match;
        this.paymentHandler = factory.createPaymentHandler();
    }

    /**
     * Handles the selection of the player who will receive additional damage.
     * <ul>
     * <li>If there is only one choice, it is selected automatically</li>
     * <li>If there are multiple choices, the user will be asked to select one</li>
     * </ul>
     *
     * @param view    The view of the player using the powerup, not null
     * @param choices The collection of possible choices, not null and not empty
     * @return The selection, not null
     * @throws CancellationException If a request to the user gets cancelled
     * @throws InterruptedException  If the thread gets interrupted
     */
    private Player handleVictimSelection(PersistentView view, Set<Player> choices) throws InterruptedException {
        if (choices.size() == 1) {
            /* Auto-select */
            return choices.iterator().next();
        } else {
            /* Ask user */
            return askToSelectPlayer(view, choices);
        }
    }

    /**
     * Asks the user to select exactly one among the given players
     *
     * @param view    The view of the player using the powerup, not null
     * @param choices The collection of possible choices, not null
     * @return The selection, not null
     * @throws CancellationException If a request to the user gets cancelled
     * @throws InterruptedException  If the thread gets interrupted
     */
    private Player askToSelectPlayer(PersistentView view, Set<Player> choices) throws InterruptedException {
        List<String> usernames = choices.stream().map(Player::getName).collect(Collectors.toList());

        /* Generate and send the request */
        PlayerRequest req = new PlayerRequest(MESSAGE_SELECT_PLAYER, usernames, false);
        String selected = view.sendChoiceRequest(req).get();

        /* Map back to player */
        return match.getPlayerByName(selected);
    }

    /**
     * Makes the player associated to given view use this powerup.
     * <p>
     * The method must be called when a turn is active and the player using the powerup must be
     * the owner of the turn.
     * </p>
     * <p>
     * There may be any number of damaged players.
     * If there are no damaged players, the player will be notified that he can't use the powerup,
     * the method will return {@code false}.
     * If there is only one damaged player, he will be auto-selected.
     * If there are multiple damaged players, then the user is asked to select one.
     * </p>
     * <p>
     * Then the user will pay an ammo of any color. He will be notified if he can't pay.
     * </p>
     * <p>
     * If the payment went fine, an additional damage is applied to the selected player
     * and the method returns {@code true}, else no damage is applied and the method returns {@code false}.
     * </p>
     *
     * @param view The view of the player who uses the powerup, not null
     * @return {@code true} if the powerup has been used, {@code false} if it hasn't been used
     * @throws CancellationException If a request to the user gets cancelled
     * @throws InterruptedException  If the thread gets interrupted
     */
    @Override
    public boolean use(PersistentView view) throws InterruptedException {
        PlayerTurn currentTurn = match.getCurrentTurn();
        Player currentPlayer = currentTurn.getTurnOwner();

        /* First determine if the powerup can be used */
        Set<Player> damagedPlayers = new HashSet<>(currentTurn.getDamagedPlayers());
        damagedPlayers.remove(currentPlayer); /* Robust in case rules change */

        if (damagedPlayers.isEmpty()) {
            /* Notify the player and return */
            view.showMessage(MESSAGE_CANT_USE, MessageType.WARNING);
            return false;
        }

        /* The powerup can be used: ask to select victim */
        Player victim = handleVictimSelection(view, damagedPlayers);

        /* Then ask to pay the cost */
        boolean hasPaid = paymentHandler.payAmmoCost(view, cost, true);

        if (!hasPaid) {
            view.showMessage(MESSAGE_CANT_PAY, MessageType.WARNING);
            return false;
        }

        /* The player has paid: perform the damage */
        victim.getPlayerBoard().addDamage(currentPlayer, DAMAGE);
        return true;
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.NotEnoughAmmoException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PowerupCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

/**
 * Offers a service to other classes of the controller to handle the payment of a certain cost
 * expressed in {@link AmmoCubeCost} from a given player.
 * If the user has enough ammo cubes to pay, the payment is automatic.
 * If the user has powerups, the handler will only ask the user to select a powerup
 * if he has more powerups than the ones needed to cover the cost, otherwise the selection is made automatically.
 * @author Alessandro Fulgini
 */
public class PaymentHandler {

    private Match match;

    private static final String MESSAGE_COVER_WITH_POWERUP = "Choose a powerup to pay the cost";

    /**
     * Creates a new payment handler associated to given match instance
     * @param match The match instance, not null
     */
    public PaymentHandler(Match match) {
        this.match = match;
    }

    /**
     * Converts a powerup card that the player has in his hands to an ammo cube, which will
     * be given to the player, while the card is discarded in the deck
     * @param player The player who holds the powerup card, not null
     * @param card The powerup card that has to be converted, not null
     * @throws IllegalArgumentException If the powerup does not belong to the player
     */
    public void convertPowerupToAmmoCube(Player player, PowerupCard card) {
        player.removePowerup(card); /* Remove from the player */
        player.addAmmo(card.getCubeColor()); /* Exchange for ammo */
        match.getPowerupStack().discard(card); /* Discard the powerup */
    }

    /**
     * Ask the player to pay an ammo cost with his resources.
     * The payment method is implemented as follows:
     * <ol>
     *     <li>If the player has enough ammo, the payment is automatic</li>
     *     <li>If the player does not have enough ammo, the algorithm tries to use powerups</li>
     *     <li>If there is only a powerup (or identical powerups) to pay a specific color, the payment
     *     is automatic, if there are different powerups the user will be prompted</li>
     * </ol>
     * <p>
     *     If the payment is successful, this method returns {@code true}, if it failed for lack of resources it
     *     returns {@code false} and the user's resources (ammo cubes and powerups) are left untouched.
     * </p>
     * <p>
     *     If the flag {@code removeAmmoFromPlayer} is set this method will also remove the ammo and the powerups used
     *     to pay from the player's resources.
     *     If it is not set, then it will only convert the powerups used to cover the cost to {@link AmmoCube}
     * </p>
     * @param view The view of the player who has to pay the cost, not null
     * @param cost A map containing the number of cubes to pay for each color, not null and with no negative values
     * @param removeAmmoFromPlayer If {@code true} the cubes and powerups used to pay will be removed from the player
     * @return Whether the user has been able to pay the cost or not
     * @throws CancellationException If a request to the user gets cancelled, the user's resources remain untouched
     * @throws InterruptedException If the thread gets interrupted while waiting
     */
    public boolean payAmmoCost(PersistentView view, Map<AmmoCubeCost, Integer> cost, boolean removeAmmoFromPlayer)
            throws InterruptedException {

        Player player = match.getPlayerByName(view.getUsername());
        boolean hasEnoughResources;

        /* Determine the cost which remains to pay using ammo */
        Map<AmmoCubeCost, Integer> remainingCost = AmmoCubeCost.calculateRemainingCost(cost, player.getAmmo());

        /* If this is not enough, check if the cost can be payed by using powerups */
        hasEnoughResources = remainingCost.values().stream().mapToInt(i -> i == null ? 0 : i).sum() == 0;
        if (!hasEnoughResources) {
            /* Create a map of cube counts from powerups */
            Map<AmmoCube, Integer> powerupCubes = player.getPowerups().stream()
                    .map(PowerupCard::getCubeColor)
                    .collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));
            hasEnoughResources = AmmoCubeCost.canPayAmmoCost(remainingCost, powerupCubes);

            if (hasEnoughResources) {
                /* Proceed with payment of the remaining cost with powerups */
                coverCostWithPowerups(view, remainingCost);
            }
        }

        /* If asked to do so, remove the ammo */
        if (hasEnoughResources && removeAmmoFromPlayer) {
            /* Convert the map to a list */
            List<AmmoCubeCost> costList = new LinkedList<>();
            cost.forEach((color, count) -> costList.addAll(Collections.nCopies(count, color)));

            /* Pay */
            try {
                player.payAmmo(costList);
            } catch (NotEnoughAmmoException e) {
                /* This does not happen, since we completely fulfilled the cost */
                throw new IllegalStateException("The user could not pay the cost");
            }
        }

        return hasEnoughResources;
    }

    /* ======= PRIVATE METHODS ========== */

    /**
     * Covers a cost to pay by converting powerups to ammo cubes.
     * Assumes that the player can pay the entire cost with his powerups.
     * Does not remove the converted ammo cubes from the player, while the converted powerups
     * are discarded in the powerup stack.
     * The convention to select powerups is the following:
     * The user is prompted only if he has more powerups than the ones needed to cover the cost,
     * otherwise the selection is made automatically
     * @param view The view of the player who has to cover the cost, not null
     * @param cost A map containing the number of cubes to pay for each color, not null and with no negative values
     * @throws CancellationException If a request to the user gets cancelled, the user's resources remain untouched
     * @throws InterruptedException If the thread gets interrupted while waiting
     */
    private void coverCostWithPowerups(PersistentView view, Map<AmmoCubeCost, Integer> cost) throws InterruptedException {
        Player player = match.getPlayerByName(view.getUsername());

        /* Cover the cost for each AmmoCubeCost, the ANY type will be covered last */
        for (Map.Entry<AmmoCubeCost, Integer> entry : cost.entrySet()) {
            int costCount = entry.getValue();
            if (costCount == 0) continue; /* Skip if not to pay */
            AmmoCubeCost costColor = entry.getKey();

            /* Get the powerups to cover the cost */
            List<PowerupCard> selectablePowerups = player.getPowerups().stream()
                    /* Filter only powerups that can pay for the given color */
                    .filter(pu -> costColor.canPayFor(pu.getCubeColor())).collect(Collectors.toList());

            if (selectablePowerups.size() > costCount) {
                while (costCount > 0) {
                    /* Ask the user to select the powerups he wants to convert */
                    PowerupCard selected = askToCoverCostWithPowerup(view, selectablePowerups);
                    /* Convert the selected powerup to ammo */
                    convertPowerupToAmmoCube(player, selected);
                    /* Remove it from the selectable powerups and decrease the cost still to be paid */
                    selectablePowerups.remove(selected);
                    costCount--;
                }
            } else {
                /* Convert all the powerups automatically */
                selectablePowerups.forEach(powerup -> convertPowerupToAmmoCube(player, powerup));
            }
        }
        /* The cost has been covered */
    }

    /**
     * Asks the user to select one of the provided powerups to cover a cost.
     * Does not remove the powerup from player's hands and does not add the ammo cube
     * @param view The view of the player, not null
     * @param choices A list with the possible choices
     * @return The user's choice
     * @throws CancellationException If the request to the user gets cancelled, the user's resources remain untouched
     * @throws InterruptedException If the thread gets interrupted while waiting
     */
    private PowerupCard askToCoverCostWithPowerup(PersistentView view, List<PowerupCard> choices) throws InterruptedException {
        PowerupCardRequest req = new PowerupCardRequest(MESSAGE_COVER_WITH_POWERUP, choices, false);
        return view.sendChoiceRequest(req).get();
    }
}

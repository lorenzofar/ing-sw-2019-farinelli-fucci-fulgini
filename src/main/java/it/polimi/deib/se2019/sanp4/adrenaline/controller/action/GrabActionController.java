package it.polimi.deib.se2019.sanp4.adrenaline.controller.action;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PowerupCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.WeaponCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.DrawnPowerupUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PaymentHandler;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.AmmoSquare;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.SpawnSquare;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.SquareVisitor;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.PickupState;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

/**
 * Responsible for handling the basic action "Grab".
 * The instance can be used for a single grab action, due to the fact that we use a visitor pattern.
 */
public class GrabActionController implements SquareVisitor {

    private static final String MESSAGE_EMPTY_AMMOSQUARE = "Your current square does not contain an ammo card!";

    private static final String MESSAGE_DISCARD_POWERUP = "You have reached the maximum number of powerups, " +
            "please choose one to discard";

    private static final String MESSAGE_EMPTY_SPAWNSQUARE = "Your current square does not contain any weapons!";

    private static final String MESSAGE_PICKUP_WEAPON = "Choose a weapon to pick up";

    private static final String MESSAGE_DISCARD_WEAPON = "You have reached the maximum number of weapons, " +
            "please choose one to discard";

    private static final String MESSAGE_CANNOT_PAY = "You don't have enough items to pay for this weapon";

    private final Match match;

    private final PersistentView view;

    private final Player player;

    private final PaymentHandler paymentHandler;

    /**
     * Creates a new instance of GrabActionController that can be used to execute a single
     * "grab" action performed by given player
     *
     * @param match   The match where the player resides, not null
     * @param view    The view of the player who is executing the action, not null
     * @param factory The controller factory to create other controllers
     */
    public GrabActionController(Match match, PersistentView view, ControllerFactory factory) {
        this.match = match;
        this.view = view;
        this.player = match.getPlayerByName(view.getUsername());
        this.paymentHandler = factory.createPaymentHandler();
    }


    /**
     * Executes the Grab action on the the square the player is in.
     * See the methods {@link #visit(AmmoSquare)} and {@link #visit(SpawnSquare)}
     * for the details of the performed actions depending on the square type
     *
     * @throws IllegalStateException If the player is not spawned
     * @throws CancellationException If a request to the user gets cancelled
     */
    public void execute() {
        /* Determine the square this player is in */
        Square square = player.getCurrentSquare();
        if (square == null) {
            throw new IllegalStateException("The player is not spawned");
        }

        /* Visit the square and do the right action based on the square type */
        square.accept(this);
    }

    /**
     * Executes the Grab action on an {@link AmmoSquare}.
     * If an {@link AmmoCard} is available on the board, the user will get the associated ammo and the powerup.
     * <p>
     * In case the user has reached the maximum number of powerups, he will be prompted to choose a powerup to
     * discard (among the ones he has and the one he just picked).
     * In this request, the drawn powerup will always be the first choice.
     * </p>
     * <p>
     * If there is no ammo card in the square the user will get a message notification
     * (via {@link RemoteView#showMessage(String, MessageType)}) and the action ends.
     * </p>
     *
     * @param square The square where the player is
     * @throws CancellationException If a request to the user gets cancelled
     */
    @Override
    public void visit(AmmoSquare square) {
        /* Grab the ammo card, if any */
        AmmoCard ammoCard = square.getAmmoCard();

        if (ammoCard == null) {
            /* The player is stupid, but we don't blame him */
            view.showMessage(MESSAGE_EMPTY_AMMOSQUARE, MessageType.WARNING);
            return;
        }

        /* Put the card back into the stack */
        match.getAmmoStack().discard(square.grabAmmo());

        /* If there is an ammo card, we start by adding the ammo */
        player.addAmmo(ammoCard.getCubes());

        /* Then we handle the powerup, if any */
        if (ammoCard.isHoldingPowerup()) {
            /* Draw a powerup from the deck */
            PowerupCard drawnPowerup = match.getPowerupStack().draw();

            /* Notify the player of the new card */
            DrawnPowerupUpdate update = new DrawnPowerupUpdate(view.getUsername(), drawnPowerup);
            view.updateSync(update);

            /* Try to add the card to the player's powerups */
            try {
                player.addPowerup(drawnPowerup);
            } catch (FullCapacityException e) {
                /* The player has reached the maximum number of powerups, so we ask him to discard one */
                askToDiscardPowerup(drawnPowerup);
            }
        }
    }

    /**
     * Asks the player to discard a powerup among the ones he has and the
     * given one.
     * The powerup he chose to discard is then put back in the powerup stack
     * and if he chose to keep the new powerup, this is added in his hands.
     * In case the user request is cancelled, the drawn powerup is discarded
     *
     * @param drawnPowerup The powerup that has been drawn to the stack
     * @throws CancellationException If a request to the user gets cancelled
     */
    private void askToDiscardPowerup(PowerupCard drawnPowerup) {
        List<PowerupCard> choices = new ArrayList<>();
        choices.add(drawnPowerup); /* New powerup */
        choices.addAll(player.getPowerups()); /* Powerups already in player's hands */

        /* Prepare the request and send it */
        PowerupCard selectedCard = drawnPowerup; /* Discard the drawn card by default */
        PowerupCardRequest req = new PowerupCardRequest(MESSAGE_DISCARD_POWERUP, choices, false);
        try {
            selectedCard = view.sendChoiceRequest(req).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (selectedCard != drawnPowerup) { /* Discard powerup in player's hands */
                player.removePowerup(selectedCard);
                try {
                    player.addPowerup(drawnPowerup);
                } catch (FullCapacityException e) {
                    /* Can't happen since we just removed a card */
                }
            }
            /* Finally discard the card he selected */
            match.getPowerupStack().discard(selectedCard);
        }
    }

    /**
     * Executes the Grab action on {@link SpawnSquare}.
     * The action takes place as follows:
     * <ol>
     * <li>If there are no weapons on the square the user gets notified and the action ends</li>
     * <li>The user is asked to select a weapon and to pay for its cost, if he can't pay he gets notified
     * and asked to select a weapon from the remaining ones. If the player cannot pay for any of the weapons
     * in the square, the action ends</li>
     * <li>If the player has selected a weapon and can pay for it (has already converted the necessary powerups),
     * then the methods checks if he has reached the maximum number of weapons in his hand and asks him to
     * discard one. The discarded card is unloaded and put into the square</li>
     * <li>If the player has been able to pick a weapon, pay for its cost and made space for another weapon,
     * then the new weapon gets loaded and added to his cards</li>
     * </ol>
     * A {@link CancellationException} may occur in three cases:
     * <ul>
     * <li>When the user is asked to select a card to grab, in this case the action ends and no card
     * is grabbed</li>
     * <li>When the user is paying, also here the action ends and no cards is grabbed</li>
     * <li>When the user is selecting a card to discard, in this case no card is discarded and the card
     * the user chose to pick is left in the square.
     * The player does not lose any ammo or weapons, but any powerups that have been chosen to pay
     * will have been converted to ammo cubes</li>
     * </ul>
     *
     * @param square The square to be visited
     * @throws CancellationException If a request to the user gets cancelled
     */
    @Override
    public void visit(SpawnSquare square) {
        try {
            /* Get the list of weapons in this square */
            List<WeaponCard> selectableWeapons = new LinkedList<>(square.getWeaponCards());

            if (selectableWeapons.isEmpty()) {
                /* The player cannot grab a weapon */
                view.showMessage(MESSAGE_EMPTY_SPAWNSQUARE, MessageType.WARNING);
                return;
            }

            WeaponCard selectedWeapon = null;
            while (selectedWeapon == null && !selectableWeapons.isEmpty()) {
                /* Ask the user to choose a weapon */
                selectedWeapon = askToPickUpWeapon(selectableWeapons);

                if (selectedWeapon == null) return; /* The user chose not to grab a weapon */

                /* If there is a cost to pay the user must produce the ammo to pay it */
                boolean canPay = askToPayLoadCost(selectedWeapon);

                if (!canPay) {
                    /* The player was unable to pay the cost for this, so it gets removed from the list of choices */
                    selectableWeapons.remove(selectedWeapon);
                    selectedWeapon = null;

                    /* Notify the player */
                    view.showMessage(MESSAGE_CANNOT_PAY, MessageType.INFO);
                }
            }

            if (selectedWeapon != null) {
                /* The player has selected a weapon and is able to load it */

                /* Check if he has to discard first */
                WeaponCard discarded = null;
                if (player.getWeapons().size() == Player.MAX_WEAPONS) {
                    /* The player should discard a weapon first */
                    discarded = askToDiscardWeapon(); /* <- This might throw */
                }

                /* Grab the weapon, load it and give it to the player */
                square.grabWeaponCard(selectedWeapon.getId());
                selectedWeapon.reload(player);
                player.addWeapon(selectedWeapon);

                /* If there is a card that has to be discarded, put it in the square */
                if (discarded != null) {
                    square.insertWeaponCard(discarded);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (FullCapacityException e) {
            /* Does not happen */
        }
    }

    /**
     * Asks the user to choose one or no card to pick up among the given choices.
     *
     * @param choices The cards from which the user can choose
     * @return The card that the user chose, or null if he did not choose, not null
     * @throws CancellationException If the request to the user gets cancelled
     * @throws InterruptedException  If the thread is interrupted while waiting for user's response
     */
    private WeaponCard askToPickUpWeapon(List<WeaponCard> choices) throws InterruptedException {
        WeaponCardRequest req = new WeaponCardRequest(MESSAGE_PICKUP_WEAPON, choices, true);
        return view.sendChoiceRequest(req).get();
    }

    /**
     * Asks the player to discard a weapon among the ones he has.
     * The selected weapon is removed from the player's hands.
     *
     * @return The card the user chose to discard
     * @throws CancellationException If the request to the user gets cancelled, in this case no weapon is discarded
     * @throws InterruptedException  If the thread is interrupted while waiting for user's response
     */
    private WeaponCard askToDiscardWeapon() throws InterruptedException {
        /* Prepare the request and send it */
        List<WeaponCard> choices = player.getWeapons();
        WeaponCardRequest req = new WeaponCardRequest(MESSAGE_DISCARD_WEAPON, choices, false);
        WeaponCard selected = view.sendChoiceRequest(req).get();

        player.removeWeapon(selected); /* Remove from player */
        return selected;
    }

    /**
     * Determines the cost of the weapon and asks the user to load it if necessary.
     * The weapon does not actually get loaded and no ammo is taken from the user.
     *
     * @param weapon The weapon that has to be reloaded, not null and in the {@link PickupState}
     * @return {@code true} if the user has been able to pay the cost, {@code false} if he hasn't
     * @throws CancellationException If a request to the user gets cancelled
     * @throws InterruptedException  If the thread gets interrupted while waiting for user's response
     */
    private boolean askToPayLoadCost(WeaponCard weapon) throws InterruptedException {
        /* Determine the load cost */
        List<AmmoCubeCost> costList = weapon.getCost();

        boolean canPay;
        if (costList.size() > 1) {
            /* Then the user must pay an additional cost: the total cost except the first cube */
            Map<AmmoCubeCost, Integer> costMap = AmmoCubeCost.mapFromCollection(costList.subList(1, costList.size()));

            /* Ask to pay but do not remove the ammo cubes from the player */
            canPay = paymentHandler.payAmmoCost(view, costMap, false);
        } else {
            canPay = true; /* The user can certainly pay zero cost */
        }

        return canPay;
    }
}

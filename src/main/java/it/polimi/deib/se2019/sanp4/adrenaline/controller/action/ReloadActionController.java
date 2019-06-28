package it.polimi.deib.se2019.sanp4.adrenaline.controller.action;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.WeaponCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PaymentHandler;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.UnloadedState;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

/**
 * Responsible for handling the basic action "Reload".
 * The instance does not depend on the specific player who is performing the action.
 * The actions gives the ability to the user to reload as many weapons as he wants
 */
public class ReloadActionController {

    private static final String MESSAGE_SELECT_WEAPON_TO_RELOAD = "Select the weapon you want to reload";

    private static final String MESSAGE_CANT_PAY_RELOAD_COST = "You don't have enough items to reload the weapon";

    private final Match match;

    private final PaymentHandler paymentHandler;

    /**
     * Creates a new controller for the basic action "Reload".
     * This instance can be used match-wide to control multiple iterations of the "Reload" action.
     * @param match The match that has to be controlled, not null
     * @param controllerFactory The controller factory to create needed controllers, not null
     */
    public ReloadActionController(Match match, ControllerFactory controllerFactory) {
        this.match = match;
        this.paymentHandler = controllerFactory.createPaymentHandler();
    }

    /**
     * Executes the basic action "Reload" on the player associated to given view.
     * The user is asked to select a weapon among the unloaded ones he has.
     * Then he's asked to pay the cost for that weapon.
     * If he doesn't have enough items to reload the selected weapon, he'll get a notification message.
     * In either case (the selected weapon has been reloaded or not), the user will be asked to select
     * another weapon to reload, until he selects no weapon or he has no more unloaded weapons.
     * @param view The view associated to the player who performs the action, not null
*      @throws CancellationException If the request to the player gets cancelled
     * @throws InterruptedException If the thread is interrupted while performing the request
     */
    public void execute(PersistentView view) throws InterruptedException {
        Player player = match.getPlayerByName(view.getUsername());

        /* Determine the weapons which can be reloaded */
        List<WeaponCard> selectable = player.getWeapons().stream()
                .filter(w -> !w.isUsable())
                .collect(Collectors.toList());

        while (!selectable.isEmpty()) {

            WeaponCard selectedWeapon = askToSelectWeapon(view, selectable);

            if (selectedWeapon != null) {
                /* Get the real weapon from the player */
                selectedWeapon = player.getWeapons().get(player.getWeapons().indexOf(selectedWeapon));

                /* Ask the player to pay the cost and reload the weapon */
                askToReloadWeapon(view, selectedWeapon);

                /* Remove the weapon from the selection */
                selectable.remove(selectedWeapon);
            } else {
                break; /* The user chose not to reload */
            }
        }
    }

    /**
     * Asks the user to select one or none of the provided weapons to be reloaded
     * @param view The view of the player whom to send the request, noy null
     * @param choices The weapons from which to choose, not null
     * @return The selected weapon or {@code null} if the player made no selection
     * @throws CancellationException If the request to the player gets cancelled
     * @throws InterruptedException If the thread is interrupted while performing the request
     */
    private WeaponCard askToSelectWeapon(PersistentView view, List<WeaponCard> choices) throws InterruptedException {
        WeaponCardRequest req = new WeaponCardRequest(MESSAGE_SELECT_WEAPON_TO_RELOAD, choices, true);
        return view.sendChoiceRequest(req).get();
    }

    /**
     * Makes the user pay the reload cost of the given weapon.
     * If the player can pay the cost, the weapon is actually reloaded,
     * if he can't he will get a notification message.
     * @param view The view of the player who wants to reload the weapon
     * @param weapon The weapon that has to be reloaded, not null and in {@link UnloadedState}
     * @throws CancellationException If a request to the player gets cancelled
     * @throws InterruptedException If the thread is interrupted while performing the request
     */
    private void askToReloadWeapon(PersistentView view, WeaponCard weapon) throws InterruptedException {
        Player player = match.getPlayerByName(view.getUsername());

        /* Get the reload cost of the weapon and convert it to a map */
        Map<AmmoCubeCost, Integer> costMap = AmmoCubeCost.mapFromCollection(weapon.getCost());

        /* Ask the player to provide the ammo to reload the weapon */
        boolean canPay = paymentHandler.payAmmoCost(view, costMap, false);

        if (canPay) {
            /* Then reload the weapon */
            weapon.reload(player);
        } else {
            /* Notify the player that he can't pay the cost */
            view.showMessage(MESSAGE_CANT_PAY_RELOAD_COST, MessageType.WARNING);
        }
    }
}

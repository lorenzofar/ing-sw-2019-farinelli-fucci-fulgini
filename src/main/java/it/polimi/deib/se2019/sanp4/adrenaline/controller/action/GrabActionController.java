package it.polimi.deib.se2019.sanp4.adrenaline.controller.action;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PowerupCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.DrawnPowerupUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.AmmoSquare;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.SpawnSquare;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.SquareVisitor;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

/**
 * Responsible for handling the basic action "Grab".
 * The instance can be used for a single grab action, due to the fact that we use a visitor pattern.
 */
public class GrabActionController implements SquareVisitor {

    private static final String MESSAGE_EMPTY_AMMOSQUARE = "Your current square does not contain an ammo card!";

    private static final String MESSAGE_DISCARD_POWERUP = "You have reached the maximum number of powerups, " +
            "please choose one to discard";

    private final Match match;

    private final PersistentView view;

    private final Player player;

    private ControllerFactory factory;


    /**
     * Creates a new instance of GrabActionController that can be used to execute a single
     * "grab" action performed by given player
     *
     * @param match The match where the player resides, not null
     * @param view  The view of the player who is executing the action, not null
     * @param factory The controller factory to create other controllers
     */
    public GrabActionController(Match match, PersistentView view, ControllerFactory factory) {
        this.match = match;
        this.view = view;
        this.player = match.getPlayerByName(view.getUsername());
        this.factory = factory;
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
     * Executes the Grab action on {@link SpawnSquare}
     *
     * @param square The square to be visited
     */
    @Override
    public void visit(SpawnSquare square) {

    }
}

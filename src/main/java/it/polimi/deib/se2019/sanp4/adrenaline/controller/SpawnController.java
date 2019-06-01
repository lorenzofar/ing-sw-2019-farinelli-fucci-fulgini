package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.AdrenalineProperties;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PowerupCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.DrawnPowerupUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.CardStack;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides static methods to handle initial spawn and respawn after death.
 */
public class SpawnController {

    private static final String CHOOSE_SPAWN_MESSAGE = "Choose a powerup as your spawn point";

    private Match match;

    /**
     * Creates a new spawn controller for given match
     * @param match The match to be controlled, not null
     */
    public SpawnController(Match match) {
        this.match = match;
    }

    /**
     * Handles the initial spawn of the player.
     * Doesn't check that the player needs to spawn
     * If the player doesn't respond in time, no spawn location is chosen and the player won't spawn at all.
     * @param view the view of the player who has to spawn
     * @throws CancellationException if the request to the player gets cancelled
     * @throws InterruptedException if the thread gets interrupted
     */
    public void initialSpawn(PersistentView view) throws InterruptedException {
        Player player = match.getPlayerByName(view.getUsername());
        /* Draw two cards from the powerup stack */
        CardStack<PowerupCard> stack = match.getPowerupStack();
        List<PowerupCard> choices = Stream.generate(stack::draw).limit(2).collect(Collectors.toList());

        /* Send the request (the timer is already started) */
        try {
            /* Set up the request */
            PowerupCardRequest req = new PowerupCardRequest(CHOOSE_SPAWN_MESSAGE, choices, false);

            /* Send it to the player */
            PowerupCard selectedCard = view.sendChoiceRequest(req).get();

            /* Move the player to spawn location */
            moveToPowerupColorAndDiscard(player, selectedCard, match);

            /* Insert the other card in the player's hands */
            choices.remove(selectedCard);
            player.addPowerup(choices.get(0));
        } catch (CancellationException e) {
            /* Put the cards back in the stack */
            choices.forEach(stack::discard);
            throw e; /* Re-throw to end the turn */
        } catch (FullCapacityException e) {
            /* It can't happen */
        }
    }

    /**
     * Handles the respawn of a player when he is dead.
     * Doesn't check that the player is dead
     * If the player doesn't respond or if hes's suspended,
     * the user is automatically spawned on the location corresponding to the card he drew.
     * This function also guarantees that the drawn card will be the first in the request
     * @param view the view of the player who needs to be respawned
     * @throws InterruptedException if the thread gets interrupted
     */
    public void respawn(PersistentView view) throws InterruptedException {
        int timeout = Integer.parseInt((String) AdrenalineProperties.getProperties()
                .getOrDefault("adrenaline.timeout.spawn", "30"));
        Player player = match.getPlayerByName(view.getUsername());

        /* Get the stack */
        CardStack<PowerupCard> stack = match.getPowerupStack();

        /* Draw a card from the stack */
        PowerupCard drawnCard = stack.draw();

        /* Notify the player of the new card */
        DrawnPowerupUpdate update = new DrawnPowerupUpdate(view.getUsername(), drawnCard);
        view.updateSync(update);

        try {
            /* If the player can't play, trigger the auto-spawn */
            if (!player.getState().canPlay()) throw new CancellationException();
            /* If not, go on with asking */

            /* Also add his powerups to the possible choices */
            List<PowerupCard> choices = new ArrayList<>();
            choices.add(drawnCard);
            choices.addAll(player.getPowerups());

            /* Prepare the request */
            PowerupCardRequest req = new PowerupCardRequest(CHOOSE_SPAWN_MESSAGE, choices, false);

            /* Send it */
            view.startTimer(() -> null, timeout, TimeUnit.SECONDS);
            PowerupCard selectedCard = view.sendChoiceRequest(req).get();
            view.stopTimer();

            /* Move the player to selected spawn point and discard the selected card */
            moveToPowerupColorAndDiscard(player, selectedCard, match);

            if (player.getPowerups().contains(selectedCard)) {
                // The player used a powerup from his hands: remove it and add the one he drew
                player.removePowerup(selectedCard);
                player.addPowerup(drawnCard);
            }
        } catch (CancellationException e) {
            /* Automatically spawn the player to the card he drew */
            moveToPowerupColorAndDiscard(player, drawnCard, match);
        } catch (FullCapacityException e) {
            /* This doesn't happen */
        }
    }

    private static void moveToPowerupColorAndDiscard(Player player, PowerupCard powerupCard, Match match) {
        Board board = match.getBoard();

        /* Move */
        Square end = board.getSpawnPoints().get(powerupCard.getCubeColor());
        board.movePlayer(player, end);

        /* Discard */
        match.getPowerupStack().discard(powerupCard);
    }
}

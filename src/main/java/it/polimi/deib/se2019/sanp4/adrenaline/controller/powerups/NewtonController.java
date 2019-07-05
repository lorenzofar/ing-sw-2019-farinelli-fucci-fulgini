package it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PlayerRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CardinalDirection;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum.ANY;

/**
 * Controller for the powerup effect {@link PowerupEnum#NEWTON}.
 * <p>
 * A player may ues this during its turn.
 * He will select another player and move it for 1 or 2 steps in one direction.
 * </p>
 * @author Alessandro Fulgini
 */
public class NewtonController implements PowerupController {

    private final Match match;

    private final Board board;

    private static final String MESSAGE_CANT_USE = "You can't use this powerup: there are no players to move";

    private static final String MESSAGE_SELECT_PLAYER = "Select the player to move";

    private static final String MESSAGE_SELECT_DESTINATION = "Select the destination square";

    private static final int MAX_MOVES = 2;

    /**
     * Creates a new controller for the powerup effect {@link PowerupEnum#NEWTON}
     *
     * @param match The match to be controlled, not null
     */
    public NewtonController(Match match) {
        this.match = match;
        this.board = match.getBoard();
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
     * Asks the user to select exactly one among the given squares
     *
     * @param view    The view of the player using the powerup, not null
     * @param choices The collection of possible choices, not null
     * @return The selection, not null
     * @throws CancellationException If a request to the user gets cancelled
     * @throws InterruptedException  If the thread gets interrupted
     */
    private Square askToSelectSquare(PersistentView view, Set<Square> choices) throws InterruptedException {
        /* Map to location */
        List<CoordPair> coordPairs = choices.stream()
                .map(Square::getLocation)
                .collect(Collectors.toList());

        /* Prepare and send the request */
        SquareRequest req = new SquareRequest(MESSAGE_SELECT_DESTINATION, coordPairs, false);
        CoordPair selected = view.sendChoiceRequest(req).get();

        /* Map back to square */
        return board.getSquare(selected);
    }

    /**
     * Given the "victim" of the powerup, determines the squares in cardinal directions that are
     * at max. consented distance from him.
     *
     * @param victim The victim of the powerup, not null
     * @return The set of squares he can be moved to
     */
    private Set<Square> determineSelectableSquares(Player victim) {
        Square start = victim.getCurrentSquare();

        /* Query squares in all directions */
        Set<Square> selectable = new HashSet<>();
        for (CardinalDirection direction : CardinalDirection.values()) {
            selectable.addAll(board.querySquares(start, ANY, direction, null, MAX_MOVES));
        }

        return selectable;
    }

    /**
     * Makes the player associated to given view use this powerup.
     * <p>
     * At first the method determines if there are any players which can be moved.
     * If there are none (e.g. the current player is the only one spawned), then the player is notified
     * and the method returns {@code false}
     * </p>
     * <p>
     * If there are players which can be moved, then the user is asked to select one of them and then to select
     * a square where to move him. The method will return {@code true}
     * </p>
     *
     * @param view The view of the player who uses the powerup, not null
     * @return {@code true} if the powerup has been used, {@code false} if it hasn't been used
     * @throws CancellationException If a request to the user gets cancelled
     * @throws InterruptedException  If the thread gets interrupted
     */
    @Override
    public boolean use(PersistentView view) throws InterruptedException {
        Player currentPlayer = match.getPlayerByName(view.getUsername());

        /* Determine other players that are spawned */
        Set<Player> selectablePlayers = match.getPlayers().stream()
                .filter(p -> !p.equals(currentPlayer))
                .filter(p -> p.getCurrentSquare() != null)
                .collect(Collectors.toSet());

        /* Check if there is at least one */
        if (selectablePlayers.isEmpty()) {
            view.showMessage(MESSAGE_CANT_USE, MessageType.WARNING);
            return false;
        }

        /* Ask the user to select a player to move */
        Player victim = askToSelectPlayer(view, selectablePlayers);

        /* Then determine the possible destinations */
        Set<Square> selectableSquares = determineSelectableSquares(victim);

        /* Ask the user to select a destination */
        Square destination = askToSelectSquare(view, selectableSquares);

        /* Move the victim to the destination */
        board.movePlayer(victim, destination);

        return true;
    }
}

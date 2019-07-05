package it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

/**
 * Controller for the powerup effect {@link PowerupEnum#TELEPORTER}.
 * <p>
 * A player may ues this during its turn.
 * He will select a square anywhere where he wants to move.
 * </p>
 * @author Alessandro Fulgini
 */
public class TeleporterController implements PowerupController {

    private static final String MESSAGE_SELECT_DESTINATION = "Select your destination";

    private final Match match;

    private final Board board;

    /**
     * Creates a new controller for the powerup effect {@link PowerupEnum#TELEPORTER}
     *
     * @param match The match to be controlled, not null
     */
    public TeleporterController(Match match) {
        this.match = match;
        this.board = match.getBoard();
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
     * Makes the player associated to given view use this powerup.
     * <p>
     * The player using this powerup, which must be spawned, is asked to select a square where he wants to move.
     * He will be moved there and the method returns {@code true}.
     * </p>
     *
     * @param view The view of the player who uses the powerup, not null
     * @return {@code true}
     * @throws CancellationException If a request to the user gets cancelled
     * @throws InterruptedException  If the thread gets interrupted
     */
    @Override
    public boolean use(PersistentView view) throws InterruptedException {
        Player player = match.getPlayerByName(view.getUsername());

        /* Determine where the player can move */
        Set<Square> selectableSquares = new HashSet<>(board.getSquares());

        /* Ask him where he wants to move */
        Square destination = askToSelectSquare(view, selectableSquares);

        /* Move him there */
        board.movePlayer(player, destination);

        return true;
    }
}

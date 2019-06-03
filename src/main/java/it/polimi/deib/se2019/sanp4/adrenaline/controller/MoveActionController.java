package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

/**
 * Responsible for handling the basic action "Move".
 * The instance does not depend on the specific player who is performing the action
 */
public class MoveActionController {

    private static final String MOVE_MESSAGE = "Select the square where you want to move";

    private final Match match;

    private final Board board;

    /**
     * Creates a new controller for the basic action "Move", associated to the given match
     * @param match The match to be controlled, not null
     */
    public MoveActionController(Match match) {
        this.match = match;
        this.board = match.getBoard();
    }

    /**
     * Starts the execution of the move action on the player associated to the given view.
     * The player must be already spawned.
     * This method determines the movement possibilities of the player and asks him where he wants to move.
     * If the user responds he will be moved to that position, if the request gets cancelled the user
     * remains in his current position
     * @param view the view of the player who executes the action, not null
     * @param maxSteps The maximum number of steps that a player can perform from his current square, not negative
     * @throws NullPointerException if the view is null
     * @throws IllegalStateException if the player is not spawned
     * @throws IllegalArgumentException if the number of steps is negative
     * @throws CancellationException If the request to the user gets cancelled
     * @throws InterruptedException If the thread gets interrupted
     */
    public void execute(PersistentView view, int maxSteps) throws InterruptedException {
        /* Determine the position of the player */
        Player player = match.getPlayerByName(view.getUsername());
        Square currentSquare = player.getCurrentSquare();

        if (currentSquare == null) {
            throw new IllegalStateException("The player is not spawned");
        }

        if (maxSteps < 0) {
            throw new IllegalArgumentException("Max steps cannot be negative");
        }

        /* Determine the movement possibilities */
        List<CoordPair> choices = board
                .querySquares(currentSquare, VisibilityEnum.ANY, null, null, maxSteps)
                .stream()
                .map(Square::getLocation).collect(Collectors.toList()); /* Map to coordinates */

        /* Ask the player to choose one of the squares */
        SquareRequest req = new SquareRequest(MOVE_MESSAGE, choices, false);
        CoordPair choice = view.sendChoiceRequest(req).get();

        /* Move the player there */
        board.movePlayer(player, board.getSquare(choice));
     }
}

package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum.ANY;

/**
 * Represents an effect where the player using the weapon is able to move for a
 * maximum number of steps.
 */
public class MovementEffect extends AbstractEffect {

    private static final String MESSAGE_SELECT_SQUARE = "Select a square where you want to move";

    private int maxMoves;

    /**
     * Creates a new movement effect with given id, attached to the given match and factory.
     * The cost of the effect is set to zero and it does not depend on any effect.
     * The number of maximum moves is set to zero.
     *
     * @param id      Unique identifier of the effect in the weapon, not null
     * @param match   The match which has to be controlled, not null
     * @param factory The factory used to create needed controllers, not null
     */
    public MovementEffect(String id, Match match, ControllerFactory factory) {
        super(id, match, factory);
        maxMoves = 0;
    }

    /**
     * Returns the maximum number of steps that the player can move.
     * @return The maximum number of steps that the player can move
     */
    public int getMaxMoves() {
        return maxMoves;
    }

    /**
     * Sets the maximum number of steps that the player can move.
     * @param maxMoves The maximum number of steps that the player can move
     */
    public void setMaxMoves(int maxMoves) {
        this.maxMoves = maxMoves;
    }

    /**
     * Makes the user with given view use this effect in the current state of the match,
     * including the payment of the additional cost to use the effect.
     * This effect always executes successfully: even if the player is not able to pay
     * the cost, staying in position can be treated as a movement of zero squares.
     *
     * @param view The view of the player who wants to use this weapon, not null
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    @Override
    public boolean use(PersistentView view) throws InterruptedException {
        /* Ask to pay the cost */
        boolean hasPaid = payAdditionalCost(view);

        if (!hasPaid) {
            return true; /* Can't move if he couldn't pay */
        }

        /* Determine the squares where he can move */
        Player player = match.getPlayerByName(view.getUsername());
        Board board = match.getBoard();

        Set<Square> squares = board.querySquares(player.getCurrentSquare(),
                ANY, null, null, maxMoves);

        if (squares.size() <= 1) {
            return true; /* The player can only stay in his square */
        }

        /* The player can move to more than one square, so we ask him where he wants to move */
        SquareRequest req = new SquareRequest(MESSAGE_SELECT_SQUARE,
                squares.stream().map(Square::getLocation).collect(Collectors.toList()), false);

        CoordPair selected = view.sendChoiceRequest(req).get();

        /* Move the player to the selected square */
        board.movePlayer(player, board.getSquare(selected));

        return true;
    }
}

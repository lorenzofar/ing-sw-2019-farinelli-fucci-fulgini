package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Specifies a visibility modifier of the squares visible from a given square.
 *
 * @author Lorenzo Farinelli, Alessandro Fulgini
 */
public enum VisibilityEnum {
    /**
     * Any square can be queried, but the user cannot pass through walls
     */
    ANY("Any",
            /* Filter */
            (start, board) -> board.getSquares().stream(),
            /* Connection */
            sc -> sc.getConnectionType() != SquareConnectionType.WALL),

    /**
     * Only visible squares can be queried
     */
    VISIBLE("Visible",
            /* Filter */
            (start, board) -> board.getVisibleSquares(start).stream(),
            /* Connection */
            sc -> sc.getConnectionType() != SquareConnectionType.WALL),

    /**
     * Only non-visible squares can be queried
     */
    NOT_VISIBLE("Not visible",
            /* Filter out non visible */
            (start, board) ->
                    board.getSquares().stream()
                            .filter(square -> !board.getVisibleSquares(start).contains(square)),
            /* Connection */
            sc -> sc.getConnectionType() != SquareConnectionType.WALL),

    /**
     * Squares are queried by ignoring walls
     */
    IGNORE_WALLS("Ignore walls",
            /* Filter */
            (start, board) -> board.getSquares().stream(),
            /* Connection */
            sc -> true
    );

    private final String message;
    public final BiFunction<Square, Board, Stream<Square>> squareGenerator;
    public final Predicate<SquareConnection> connectionFilter;

    /**
     * Creates a new square visibility modifier
     *
     * @param message          a human-readable string
     * @param squareGenerator  a function which, given the board and the starting square
     *                         returns a stream with the squares visible according to that modifier
     * @param connectionFilter a predicate which, given a {@link SquareConnection} returns the
     *                         navigable squares according to the visibility modifier
     */
    VisibilityEnum(String message,
                   BiFunction<Square, Board, Stream<Square>> squareGenerator,
                   Predicate<SquareConnection> connectionFilter) {
        this.message = message;
        this.squareGenerator = squareGenerator;
        this.connectionFilter = connectionFilter;
    }

    @Override
    public String toString() {
        return message;
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

/**
 * A class representing a connection to a square.
 * It holds information about:
 * <ul>
 *     <li>The connected square</li>
 *     <li>The type of connection</li>
 * </ul>
 * */
public class SquareConnection {

    /** The coordinates of the connected square */
    private CoordPair square;

    /** Thea type of connection with the square */
    private SquareConnectionType connectionType;

    /**
     * Creates a new connection
     * @param square The square the connection points to
     * @param connectionType The type of connection to the square
     */
    SquareConnection(CoordPair square, SquareConnectionType connectionType){
        this.square = square;
        this.connectionType = connectionType;
    }

    /**
     * Retrieves the connected square
     * @return The object describing the square's coordinates
     */
    public CoordPair getSquare() {
        return square;
    }

    /**
     * Retrieves the type of connection
     * @return An enumerator describing the type of connection
     */
    public SquareConnectionType getConnectionType() {
        return connectionType;
    }
}

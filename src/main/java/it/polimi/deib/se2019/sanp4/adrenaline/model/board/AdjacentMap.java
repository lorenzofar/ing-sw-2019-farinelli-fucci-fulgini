package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A class describing the connection between squares in the board.
 * It is unique for each square and represents how it is connected to the surrounding squares.
 * It holds an internal mapping of the adjacent squares of a square for all the cardinal directions
 * and provides methods to retrieve adjacent squares.
 * If there is no adjacent square in a given direction, the value is set to null in this map.
 */
public class AdjacentMap extends EnumMap<CardinalDirection, SquareConnection> implements Serializable {

    private static final long serialVersionUID = -3090416453975696683L;

    /**
     * Creates a map where the square is isolated: all connections are set to null.
     */
    AdjacentMap(){
        super(CardinalDirection.class);
    }

    /**
     * Sets the connection in a specified direction.
     * @param direction The cardinal direction, not null
     * @param square The coordinates of the adjacent square in that direction
     * @param connectionType The connection type with the adjacent square
     */
    public void setConnection(CardinalDirection direction, CoordPair square, SquareConnectionType connectionType){
        if(direction == null){
            throw new NullPointerException("Direction cannot be null");
        }
        this.put(direction, new SquareConnection(square, connectionType));
    }

    /**
     * Returns the connection in the specified direction, null if there is no adjacent square.
     * @param direction The cardinal direction, not null
     * @return If there is an adjacent square, returns the connection; if there is no adjacent square returns null
     */
    public SquareConnection getConnection(CardinalDirection direction){
        if(direction == null){
            throw new NullPointerException("Direction cannot be null");
        }
        return this.get(direction);
    }

    /**
     * Retrieves all the adjacent squares.
     * @return A collection of objects representing the squares
     */
    public Collection<CoordPair> getSquares(){
        return this.values().stream().map(SquareConnection::getSquare).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Retrieves all the adjacent squares a player can move into
     * @return A collection of objects representing the squares
     */
    public Collection<CoordPair> getReachableSquares(){
        return this.values()
                .stream()
                .filter(connection -> connection.getConnectionType() != SquareConnectionType.WALL)
                .map(SquareConnection::getSquare)
                .collect(Collectors.toList());
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * A class describing the connection between squares in the board.
 * It is unique for each square and represents how it is connected to the surrounding squares.
 * It holds an internal mapping of the adjacent squares of a square for all the cardinal directions
 * and provides methods to retrieve adjacent squares
 */
public class AdjacentMap {

    /** The mapping between cardinal directions and connections */
    private Map<CardinalDirection, SquareConnection> map;

    /**
     * Creates a new map with the provided connections
     * @param map The map describing the connections, not null
     */
    AdjacentMap(Map<CardinalDirection, SquareConnection> map){
        if(map == null){
            throw new NullPointerException("Connections map cannot be null");
        }
        this.map = map;
    }

    /**
     * Gets the connection in the specified direction
     * @param direction The cardinal direction, not null
     * @return The object representing the connection
     */
    public SquareConnection getConnection(CardinalDirection direction){
        if(direction == null){
            throw new NullPointerException("Direction cannot be null");
        }
        return this.map.get(direction);
    }

    /**
     * Retrieves all the adjacent squares
     * @return A collection of objects representing the squares
     */
    public Collection<Square> getSquares(){
        //TODO: Implement this method
        return Collections.emptyList();
    }
}

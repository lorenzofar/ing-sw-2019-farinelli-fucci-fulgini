package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import org.junit.Test;

import static org.junit.Assert.*;
import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.CardinalDirection.*;
import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.SquareConnectionType.*;

public class AdjacentMapTest {

    @Test (expected = NullPointerException.class)
    public void getConnection_NullDirectionProvided_ShouldThrowNullPointerException() {
        AdjacentMap adjacentMap = new AdjacentMap();
        adjacentMap.getConnection(null); /* This will throw */
    }

    @Test (expected = NullPointerException.class)
    public void setConnection_NullDirectionProvided_ShouldThrowNullPointerException() {
        new AdjacentMap().setConnection(null, new CoordPair(5,5), DOOR);
    }

    @Test
    public void setConnection_ShouldReturnSameConnection() {
        AdjacentMap adjacentMap = new AdjacentMap();
        adjacentMap.setConnection(N, new CoordPair(5,5), DOOR);
        assertEquals(DOOR, adjacentMap.getConnection(N).getConnectionType());
    }

    @Test
    public void getReachableSquares_ShouldContainProvidedSquare(){
        AdjacentMap adjacentMap = new AdjacentMap();
        CoordPair coordPair = new CoordPair(5,5);
        adjacentMap.setConnection(N, coordPair, DOOR);
        assertTrue(adjacentMap.getReachableSquares().contains(coordPair));
    }

    @Test
    public void getSquares_ShouldContainProvidedSquare(){
        AdjacentMap adjacentMap = new AdjacentMap();
        CoordPair coordPair = new CoordPair(5,5);
        adjacentMap.setConnection(N, coordPair, DOOR);
        assertTrue(adjacentMap.getSquares().contains(coordPair));
    }
}
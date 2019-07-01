package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import org.junit.Test;

import java.util.Arrays;

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
        SquareConnectionType connectionType = adjacentMap.getConnection(N).getConnectionType();
        assertEquals(DOOR, connectionType);
        assertEquals(DOOR.toString(), connectionType.toString());
        assertEquals(DOOR.getHorizontalCharacterRepresentation(), connectionType.getHorizontalCharacterRepresentation());
        assertEquals(DOOR.getVerticalCharacterRepresentation(), connectionType.getVerticalCharacterRepresentation());
    }

    @Test
    public void getReachableSquares_ShouldContainProvidedSquare(){
        AdjacentMap adjacentMap = new AdjacentMap();
        CoordPair coordPair = new CoordPair(5,5);
        adjacentMap.setConnection(N, coordPair, DOOR);
        assertTrue(adjacentMap.getReachableSquares().contains(coordPair));
    }

    @Test
    public void getReachableSquares_shouldNotTravelThroughWalls() {
        AdjacentMap adjacentMap = new AdjacentMap();
        CoordPair c1 = new CoordPair(5,5);
        adjacentMap.setConnection(N, c1, DOOR);
        CoordPair c2 = new CoordPair(4,5);
        adjacentMap.setConnection(W, c2, WALL);
        CoordPair c3 = new CoordPair(4,4);
        adjacentMap.setConnection(S, c3, FLOOR);
        assertTrue(adjacentMap.getReachableSquares().containsAll(Arrays.asList(c1, c3)));
        assertFalse(adjacentMap.getReachableSquares().contains(c2));
    }

    @Test
    public void getSquares_ShouldContainProvidedSquare(){
        AdjacentMap adjacentMap = new AdjacentMap();
        CoordPair coordPair = new CoordPair(5,5);
        adjacentMap.setConnection(N, coordPair, DOOR);
        assertTrue(adjacentMap.getSquares().contains(coordPair));
    }
}
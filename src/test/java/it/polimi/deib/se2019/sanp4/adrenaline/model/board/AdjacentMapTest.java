package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class AdjacentMapTest {

    @Test (expected = NullPointerException.class)
    public void getConnection_NullDirectionProvided_ShouldThrowNullPointerException() {
        AdjacentMap adjacentMap = new AdjacentMap();
        adjacentMap.setConnection(CardinalDirection.N, new CoordPair(5,5), SquareConnectionType.DOOR);
        adjacentMap.getConnection(null); /* This will throw */
    }

    @Test (expected = NullPointerException.class)
    public void setConnection_NullDirectionProvided_ShouldThrowNullPointerException() {
        AdjacentMap adjacentMap = new AdjacentMap();
        adjacentMap.setConnection(null, new CoordPair(5,5), SquareConnectionType.DOOR);
    }

    @Test
    public void setConnection_ShouldReturnSameConnection() {
        AdjacentMap adjacentMap = new AdjacentMap();
        adjacentMap.setConnection(CardinalDirection.N, new CoordPair(5,5), SquareConnectionType.DOOR);
        assertEquals(SquareConnectionType.DOOR, adjacentMap.getConnection(CardinalDirection.N).getConnectionType());
    }

    @Test
    public void getSquares_CollectionShouldReturnBothSquares(){
        AdjacentMap adjacentMap = new AdjacentMap();
        adjacentMap.setConnection(CardinalDirection.N, new CoordPair(5,5), SquareConnectionType.DOOR);

    }

    @Ignore
    public void getConnection_SingleDirectionMap_getConnectionShouldReturnSameConnection() {
        fail();
    }

    @Ignore
    public void getSquares() {
        fail();
    }
}
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

    @Ignore
    public void getConnection_SingleDirectionMap_getConnectionShouldReturnSameConnection() {
        fail();
    }

    @Ignore
    public void getSquares() {
        fail();
    }
}
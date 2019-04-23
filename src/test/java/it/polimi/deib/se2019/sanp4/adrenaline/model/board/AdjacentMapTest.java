package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import org.junit.Test;

import java.util.Map;
import java.util.HashMap;

import static org.junit.Assert.*;

public class AdjacentMapTest {

    @Test (expected = NullPointerException.class)
    public void getConnection_NullDirectionProvided_ShouldThrowNullPointerException() {
        Map<CardinalDirection, SquareConnection> map = new HashMap<CardinalDirection, SquareConnection>();
        CoordPair coordPair = new CoordPair(5,5);
        SquareConnection squareConnection = new SquareConnection(coordPair, SquareConnectionType.DOOR);
        map.put(CardinalDirection.N, squareConnection);
        AdjacentMap adjacentMap = new AdjacentMap(map);

        adjacentMap.getConnection(null);
    }

    @Test
    public void getConnection_SingleDirectionMap_getConnectionShouldReturnSameConnection() {
        
    }

    @Test
    public void getSquares() {
    }

    @Test(expected = NullPointerException.class)
    public void createMap_emptyMapProvided_ShouldThrowNullPointerException(){
        Map<CardinalDirection, SquareConnection> map = null;
        new AdjacentMap(map);
    }

    //TODO: check this test after modifying AdjacentMap
    @Test(expected = NullPointerException.class)
    public void createMap_mapWithEmptyValuesProvided_ShouldThrowNullPointerException(){
        Map<CardinalDirection, SquareConnection> map = new HashMap<CardinalDirection, SquareConnection>();
        map.put(null, null);
        new AdjacentMap(map);
    }

    @Test
    public void createMap_mapWithOneValueProvided_ShouldNotThrowException(){
        Map<CardinalDirection, SquareConnection> map = new HashMap<CardinalDirection, SquareConnection>();
        CoordPair coordPair = new CoordPair(5,5);
        SquareConnection squareConnection = new SquareConnection(coordPair, SquareConnectionType.DOOR);
        map.put(CardinalDirection.N, squareConnection);
        AdjacentMap adjacentMap = new AdjacentMap(map);
        assertEquals(adjacentMap.getConnection(CardinalDirection.N), squareConnection);
    }
}
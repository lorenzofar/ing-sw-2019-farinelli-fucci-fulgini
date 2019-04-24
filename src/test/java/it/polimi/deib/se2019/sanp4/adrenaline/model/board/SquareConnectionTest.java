package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import org.junit.Test;

import static org.junit.Assert.*;

public class SquareConnectionTest {

    @Test(expected = NullPointerException.class)
    public void createSquareConnection_NullSquareProvided_ShouldThrowNullPointerException(){
        CoordPair coordPair = new CoordPair(5,5);
        SquareConnectionType squareConnectionType = null;
        SquareConnection squareConnection = new SquareConnection(coordPair, squareConnectionType);
    }

    @Test(expected = NullPointerException.class)
    public void createSquareConnection_NullCoordPairProvided_ShouldThrowNullPointerException(){
        CoordPair coordPair = null;
        SquareConnectionType squareConnectionType = SquareConnectionType.DOOR;
        SquareConnection squareConnection = new SquareConnection(coordPair, squareConnectionType);
    }

    @Test
    public void createSquareConnection_ShouldNotThrowException(){
        CoordPair coordPair = new CoordPair(5,5);
        SquareConnectionType squareConnectionType = SquareConnectionType.DOOR;
        SquareConnection squareConnection = new SquareConnection(coordPair, squareConnectionType);

    }

    @Test
    public void getSquare_ShouldReturnSameSquare(){
        CoordPair coordPair = new CoordPair(5,5);
        SquareConnectionType squareConnectionType = SquareConnectionType.DOOR;
        SquareConnection squareConnection = new SquareConnection(coordPair, squareConnectionType);
        assertEquals(coordPair, squareConnection.getSquare());
    }

    @Test
    public void getConnectionType_ShouldReturnSameConnectionType(){
        CoordPair coordPair = new CoordPair(5,5);
        SquareConnectionType squareConnectionType = SquareConnectionType.DOOR;
        SquareConnection squareConnection = new SquareConnection(coordPair, squareConnectionType);
        assertEquals(squareConnectionType, squareConnection.getConnectionType());
    }
}
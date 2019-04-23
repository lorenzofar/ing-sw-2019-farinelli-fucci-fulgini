package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import org.junit.Test;

import static org.junit.Assert.*;

public class CoordPairTest {

    @Test(expected = IllegalArgumentException.class)
    public void createCoordPair_NegativeXValueProvided_ShouldThrowIllegalArgumentException() {
        CoordPair coordPair = new CoordPair(-5, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCoordPair_NegativeYValueProvided_ShouldThrowIllegalArgumentException() {
        CoordPair coordPair = new CoordPair(5, -1);
    }

    @Test
    public void createCoordPair_ShouldReturnSameValues() {
        int x = 5;
        int y = 5;
        CoordPair coordPair = new CoordPair(x,y);
        assertEquals(x, coordPair.getX());
        assertEquals(y, coordPair.getY());
    }

}
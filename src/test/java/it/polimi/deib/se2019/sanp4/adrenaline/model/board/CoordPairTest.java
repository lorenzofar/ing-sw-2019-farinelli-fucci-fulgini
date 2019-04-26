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

    @Test
    public void checkEquals_selfPassed_shouldBeEqual(){
        CoordPair pair = new CoordPair(1, 2);
        assertEquals(pair, pair);
    }

    @Test
    public void checkEquals_anotherClassPassed_shouldNotBeEqual(){
        CoordPair pair = new CoordPair(1, 2);
        assertNotEquals(new Object(), pair);
    }

    @Test
    public void checkEquals_coordPairWithSameValuesPassed_shouldBeEqual(){
        int x = 5;
        int y = 5;
        CoordPair pair1 = new CoordPair(x, y);
        CoordPair pair2 = new CoordPair(x, y);
        assertEquals(pair1, pair2);
    }

    @Test
    public void checkEquals_coordPairWithDifferentValuesPassed_shouldNotBeEqual(){
        CoordPair pair1 = new CoordPair(1, 2);
        CoordPair pair2 = new CoordPair(2, 1);
        assertNotEquals(pair1, pair2);
    }

    @Test
    public void checkHashCode_compareWithDifferentCoordPair_shouldBeDifferent(){
        CoordPair pair1 = new CoordPair(1, 2);
        CoordPair pair2 = new CoordPair(2, 1);
        assertNotEquals(pair1.hashCode(), pair2.hashCode());
    }

    @Test
    public void checkHashCode_compareWithSameCoordPair_shouldBeSame(){
        CoordPair pair1 = new CoordPair(1, 2);
        CoordPair pair2 = new CoordPair(1, 2);
        assertEquals(pair1.hashCode(), pair2.hashCode());
    }

}
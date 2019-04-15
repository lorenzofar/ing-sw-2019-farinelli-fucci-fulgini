package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ScoresIteratorTest {

    static ScoresIterator validScores;
    static final int[] points = {8, 6, 4, 2, 1, 1};

    @BeforeClass
    public static void setup(){
        validScores = new ScoresIterator(points, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createIterator_emptyPointsProvided_shouldThrowIllegalArgumentException(){
        int[] invalidPoints = {};
        ScoresIterator invalidScores = new ScoresIterator(invalidPoints,  0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createIterator_illegalStartIndex_shouldThrowIllegalArgumentException(){
        int invalidStartIndex = points.length +1;
        ScoresIterator invalidScores = new ScoresIterator(points, invalidStartIndex);
    }

    @Test
    public void iterate_afterPointsLength_returnsLastElement(){
        for(int i = 0; i< points.length + 3; i++){
            int n = validScores.next();
            if(i < points.length){
                assertEquals(n, points[i]);
            }
            else {
                assertEquals(n, points[points.length - 1]);
            }
        }
    }

    @Test
    public void queryHasNext_shouldReturnTrue(){
        assertTrue(validScores.hasNext());
    }
}

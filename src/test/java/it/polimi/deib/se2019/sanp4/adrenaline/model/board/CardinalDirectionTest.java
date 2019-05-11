package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import org.junit.Test;

import static org.junit.Assert.*;

public class CardinalDirectionTest {

    @Test
    public void getOppositeDirection_ShouldSucceed(){
        CardinalDirection cardinalDirection = CardinalDirection.N.getOppositeDirection();
        assertEquals(CardinalDirection.S, cardinalDirection);
        cardinalDirection = CardinalDirection.S.getOppositeDirection();
        assertEquals(CardinalDirection.N, cardinalDirection);
        cardinalDirection = CardinalDirection.E.getOppositeDirection();
        assertEquals(CardinalDirection.W, cardinalDirection);
        cardinalDirection = CardinalDirection.W.getOppositeDirection();
        assertEquals(CardinalDirection.E, cardinalDirection);
    }

    @Test
    public void toString_ShouldReturnSameString(){
        CardinalDirection cardinalDirection = CardinalDirection.N;
        assertEquals("North", cardinalDirection.toString());
    }

}
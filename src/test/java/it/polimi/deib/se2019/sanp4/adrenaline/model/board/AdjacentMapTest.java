package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class AdjacentMapTest {

    @Test
    public void getConnection() {
    }

    @Test
    public void getSquares() {
    }

    @Test(expected = NullPointerException.class)
    public void createMap_emptyMapProvided_ShouldThrowNullPointerException(){
        Map<CardinalDirection, SquareConnection> map = null;
        new AdjacentMap(map);
    }

    //TODO: test the second "if" and the final statement


}
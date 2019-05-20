package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Map;
import java.util.HashMap;

import static org.junit.Assert.*;

public class AmmoSquareTest {

    private static AmmoSquare ammoSquare;
    private static AmmoCard ammoCard;

    @BeforeClass
    public static void classSetUp() {
        Map<AmmoCube, Integer> map = new HashMap<>();
        map.put(AmmoCube.BLUE, 3);
        ammoCard = new AmmoCard(1, map, false);
    }

    @Before
    public void setUp() throws Exception {
        /* Create a new ammosquare before each test */
        ammoSquare = new AmmoSquare(new CoordPair(5,5));
    }

    @Test (expected = IllegalStateException.class)
    public void grabAmmo_EmptyAmmoSquare_ShouldThrowIllegalStateException(){
        ammoSquare.grabAmmo();
    }

    @Test (expected = NullPointerException.class)
    public void insertAmmo_NullCardProvided_ShouldThrowNullPointerException(){
        ammoSquare.insertAmmo(null);

    }

    @Test
    public void grabAmmo_ValidCardProvided_ShouldReturnSameCard(){
        ammoSquare.insertAmmo(ammoCard);
        assertEquals(ammoCard, ammoSquare.grabAmmo());
    }

    @Test
    public void getAmmoCard_ShouldReturnSameCard(){
        ammoSquare.insertAmmo(ammoCard);
        assertEquals(ammoCard, ammoSquare.getAmmoCard());
    }

    @Test
    public void isFull_emptySquare_shouldReturnFalse() {
        assertFalse(ammoSquare.isFull());
    }

    @Test
    public void isFull_cardInserted_shouldReturnTrue() {
        ammoSquare.insertAmmo(ammoCard);
        assertTrue(ammoSquare.isFull());
    }
}
package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;

import org.junit.Test;
import java.util.Map;
import java.util.HashMap;

import static org.junit.Assert.*;

public class AmmoSquareTest {

    @Test (expected = IllegalStateException.class)
    public void grabAmmo_EmptyAmmoSquare_ShouldThrowIllegalStateException(){
        CoordPair location = new CoordPair(5,5);
        AmmoSquare ammoSquare = new AmmoSquare(location);
        ammoSquare.grabAmmo();
    }

    @Test (expected = NullPointerException.class)
    public void insertAmmo_NullCardProvided_ShouldThrowNullPointerException(){
        AmmoCard ammo = null;
        CoordPair location = new CoordPair(5,5);
        AmmoSquare ammoSquare = new AmmoSquare(location);
        ammoSquare.insertAmmo(ammo);

    }

    @Test
    public void grabAmmo_ValidCardProvided_ShouldReturnSameCard(){
        CoordPair location = new CoordPair(5,5);
        AmmoSquare ammoSquare = new AmmoSquare(location);
        Map<AmmoCube, Integer> map = new HashMap<>();
        map.put(AmmoCube.BLUE, 3);
        AmmoCard ammoCard = new AmmoCard(1, map, false);
        ammoSquare.insertAmmo(ammoCard);
        assertEquals(ammoCard, ammoSquare.grabAmmo());
    }

    @Test
    public void getAmmoCard_ShouldReturnSameCard(){
        CoordPair location = new CoordPair(5,5);
        AmmoSquare ammoSquare = new AmmoSquare(location);
        Map<AmmoCube, Integer> map = new HashMap<>();
        map.put(AmmoCube.BLUE, 3);
        AmmoCard ammoCard = new AmmoCard(1, map, false);
        ammoSquare.insertAmmo(ammoCard);
        assertEquals(ammoCard, ammoSquare.getAmmoCard());
    }
}
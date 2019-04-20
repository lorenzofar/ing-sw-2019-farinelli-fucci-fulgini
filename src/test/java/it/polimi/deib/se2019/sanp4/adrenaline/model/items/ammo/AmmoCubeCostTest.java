package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

import org.junit.Test;

import static org.junit.Assert.*;

public class AmmoCubeCostTest {

    @Test(expected = NullPointerException.class)
    public void checkCanPay_nullAmmoCubeCostProvided_shouldThrowNullPointerException(){
        AmmoCubeCost.ANY.canPayFor((AmmoCubeCost)null);
    }

    @Test(expected = NullPointerException.class)
    public void checkCanPay_nullAmmoCubeProvided_shouldThrowNullPointerException(){
        AmmoCubeCost.ANY.canPayFor((AmmoCube)null);
    }

    @Test
    public void checkCanPay_passAmmoCubeCost_cubeisANY_shouldReturnAlwaysTrue(){
        assertTrue(AmmoCubeCost.ANY.canPayFor(AmmoCubeCost.ANY));
        assertTrue(AmmoCubeCost.ANY.canPayFor(AmmoCubeCost.BLUE));
        assertTrue(AmmoCubeCost.ANY.canPayFor(AmmoCubeCost.RED));
        assertTrue(AmmoCubeCost.ANY.canPayFor(AmmoCubeCost.YELLOW));
    }

    @Test
    public void checkCanPay_passAmmoCubeCost_cubeIsNotANY_shouldReturnTrueOnlyWhenCubesHaveSameValue(){
        assertTrue(AmmoCubeCost.BLUE.canPayFor(AmmoCubeCost.BLUE));
        assertFalse(AmmoCubeCost.BLUE.canPayFor(AmmoCubeCost.RED));
        assertFalse(AmmoCubeCost.BLUE.canPayFor(AmmoCubeCost.YELLOW));
        assertFalse(AmmoCubeCost.BLUE.canPayFor(AmmoCubeCost.ANY));
        assertTrue(AmmoCubeCost.RED.canPayFor(AmmoCubeCost.RED));
        assertFalse(AmmoCubeCost.RED.canPayFor(AmmoCubeCost.BLUE));
        assertFalse(AmmoCubeCost.RED.canPayFor(AmmoCubeCost.YELLOW));
        assertFalse(AmmoCubeCost.RED.canPayFor(AmmoCubeCost.ANY));
        assertTrue(AmmoCubeCost.YELLOW.canPayFor(AmmoCubeCost.YELLOW));
        assertFalse(AmmoCubeCost.YELLOW.canPayFor(AmmoCubeCost.RED));
        assertFalse(AmmoCubeCost.YELLOW.canPayFor(AmmoCubeCost.BLUE));
        assertFalse(AmmoCubeCost.YELLOW.canPayFor(AmmoCubeCost.ANY));
    }

    @Test
    public void checkCanPay_passAmmoCube_cubeisANY_shouldReturnAlwaysTrue(){assertTrue(AmmoCubeCost.ANY.canPayFor(AmmoCube.BLUE));
        assertTrue(AmmoCubeCost.ANY.canPayFor(AmmoCube.RED));
        assertTrue(AmmoCubeCost.ANY.canPayFor(AmmoCube.YELLOW));
    }

    @Test
    public void checkCanPay_passAmmoCube_cubeIsNotANY_shouldReturnTrueOnlyWhenCubesHaveSameValue(){
        assertTrue(AmmoCubeCost.BLUE.canPayFor(AmmoCube.BLUE));
        assertFalse(AmmoCubeCost.BLUE.canPayFor(AmmoCube.RED));
        assertFalse(AmmoCubeCost.BLUE.canPayFor(AmmoCube.YELLOW));
        assertTrue(AmmoCubeCost.RED.canPayFor(AmmoCube.RED));
        assertFalse(AmmoCubeCost.RED.canPayFor(AmmoCube.BLUE));
        assertFalse(AmmoCubeCost.RED.canPayFor(AmmoCube.YELLOW));
        assertTrue(AmmoCubeCost.YELLOW.canPayFor(AmmoCube.YELLOW));
        assertFalse(AmmoCubeCost.YELLOW.canPayFor(AmmoCube.RED));
        assertFalse(AmmoCubeCost.YELLOW.canPayFor(AmmoCube.BLUE));
    }

    @Test
    public void getCorrespondingCube_cubeNotANY_shouldReturnCorrespondingValue(){
        assertEquals(AmmoCube.BLUE, AmmoCubeCost.BLUE.getCorrespondingCube());
        assertEquals(AmmoCube.YELLOW, AmmoCubeCost.YELLOW.getCorrespondingCube());
        assertEquals(AmmoCube.RED, AmmoCubeCost.RED.getCorrespondingCube());
    }

    @Test
    public void getCorrespondingCube_cubeisANY_shouldReturnNull(){
        assertNull(AmmoCubeCost.ANY.getCorrespondingCube());
    }
}

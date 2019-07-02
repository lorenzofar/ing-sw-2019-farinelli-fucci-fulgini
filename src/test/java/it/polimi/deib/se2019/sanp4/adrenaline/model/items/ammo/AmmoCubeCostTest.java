package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class AmmoCubeCostTest {

    @Test
    public void colorCodes_shouldBeNotNull() {
        AmmoCubeCost cube = AmmoCubeCost.RED;

        assertNotNull(cube.getAnsiCode());
        assertNotNull(cube.getHexCode());
        assertNotNull(cube.toString());
    }

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

    @Test
    public void calculateRemainingCost_enoughAmmo_noAny_shouldHaveNoRemainingCost() {
        /* Create the two maps */
        Map<AmmoCubeCost, Integer> initialCost = new EnumMap<>(AmmoCubeCost.class);
        initialCost.put(AmmoCubeCost.RED, 2);
        initialCost.put(AmmoCubeCost.BLUE, 3);

        Map<AmmoCube, Integer> availableAmmo = new EnumMap<>(AmmoCube.class);
        availableAmmo.put(AmmoCube.RED, 3);
        availableAmmo.put(AmmoCube.BLUE, 3);

        /* Call the method */
        Map<AmmoCubeCost, Integer> remainingCost = AmmoCubeCost.calculateRemainingCost(initialCost, availableAmmo);

        /* Check the result */
        remainingCost.forEach((k,v) -> assertEquals(0, (int) v));
    }

    @Test
    public void calculateRemainingCost_enoughAmmo_withAny_shouldHaveNoRemainingCost() {
        /* Create the two maps */
        Map<AmmoCubeCost, Integer> initialCost = new EnumMap<>(AmmoCubeCost.class);
        initialCost.put(AmmoCubeCost.RED, 1);
        initialCost.put(AmmoCubeCost.BLUE, 3);
        initialCost.put(AmmoCubeCost.ANY, 2);

        Map<AmmoCube, Integer> availableAmmo = new EnumMap<>(AmmoCube.class);
        availableAmmo.put(AmmoCube.RED, 3);
        availableAmmo.put(AmmoCube.BLUE, 3);

        /* Call the method */
        Map<AmmoCubeCost, Integer> remainingCost = AmmoCubeCost.calculateRemainingCost(initialCost, availableAmmo);

        /* Check the result */
        remainingCost.forEach((k,v) -> assertEquals(0, (int) v));
    }

    @Test
    public void calculateRemainingCost_notEnoughToPayNormal_noAny_shouldHaveRemainingCost() {
        /* Create the two maps */
        Map<AmmoCubeCost, Integer> initialCost = new EnumMap<>(AmmoCubeCost.class);
        initialCost.put(AmmoCubeCost.RED, 1);
        initialCost.put(AmmoCubeCost.BLUE, 3);
        initialCost.put(AmmoCubeCost.YELLOW, 3);

        Map<AmmoCube, Integer> availableAmmo = new EnumMap<>(AmmoCube.class);
        availableAmmo.put(AmmoCube.RED, 3);
        availableAmmo.put(AmmoCube.BLUE, 3);
        availableAmmo.put(AmmoCube.YELLOW, 1);

        /* Call the method */
        Map<AmmoCubeCost, Integer> remainingCost = AmmoCubeCost.calculateRemainingCost(initialCost, availableAmmo);

        /* Check the result */
        assertEquals(0, (int) remainingCost.get(AmmoCubeCost.RED));
        assertEquals(0, (int) remainingCost.get(AmmoCubeCost.BLUE));
        assertEquals(2, (int) remainingCost.get(AmmoCubeCost.YELLOW));
    }

    @Test
    public void calculateRemainingCost_notEnoughToPayNormal_withAny_shouldHaveRemainingCost() {
        /* Create the two maps */
        Map<AmmoCubeCost, Integer> initialCost = new EnumMap<>(AmmoCubeCost.class);
        initialCost.put(AmmoCubeCost.RED, 1);
        initialCost.put(AmmoCubeCost.BLUE, 3);
        initialCost.put(AmmoCubeCost.YELLOW, 3);
        initialCost.put(AmmoCubeCost.ANY, 1);

        Map<AmmoCube, Integer> availableAmmo = new EnumMap<>(AmmoCube.class);
        availableAmmo.put(AmmoCube.RED, 3);
        availableAmmo.put(AmmoCube.BLUE, 3);
        availableAmmo.put(AmmoCube.YELLOW, 1);

        /* Call the method */
        Map<AmmoCubeCost, Integer> remainingCost = AmmoCubeCost.calculateRemainingCost(initialCost, availableAmmo);

        /* Check the result */
        assertEquals(0, (int) remainingCost.getOrDefault(AmmoCubeCost.RED, 0));
        assertEquals(0, (int) remainingCost.getOrDefault(AmmoCubeCost.BLUE, 0));
        assertEquals(2, (int) remainingCost.getOrDefault(AmmoCubeCost.YELLOW, 0));
        assertEquals(0, (int) remainingCost.getOrDefault(AmmoCubeCost.ANY, 0));
    }

    @Test
    public void calculateRemainingCost_notEnoughToPayAny_shouldHaveRemainingCost() {
        /* Create the two maps */
        Map<AmmoCubeCost, Integer> initialCost = new EnumMap<>(AmmoCubeCost.class);
        initialCost.put(AmmoCubeCost.RED, 1);
        initialCost.put(AmmoCubeCost.ANY, 3);

        Map<AmmoCube, Integer> availableAmmo = new EnumMap<>(AmmoCube.class);
        availableAmmo.put(AmmoCube.RED, 2);
        availableAmmo.put(AmmoCube.BLUE, 1);

        /* Call the method */
        Map<AmmoCubeCost, Integer> remainingCost = AmmoCubeCost.calculateRemainingCost(initialCost, availableAmmo);

        /* Check the result */
        assertEquals(0, (int) remainingCost.getOrDefault(AmmoCubeCost.RED, 0));
        assertEquals(0, (int) remainingCost.getOrDefault(AmmoCubeCost.BLUE, 0));
        assertEquals(0, (int) remainingCost.getOrDefault(AmmoCubeCost.YELLOW, 0));
        assertEquals(1, (int) remainingCost.getOrDefault(AmmoCubeCost.ANY, 0));
    }

    @Test
    public void canPayAmmoCost_enoughAmmo_shouldReturnTrue() {
        /* Create the two maps */
        Map<AmmoCubeCost, Integer> cost = new EnumMap<>(AmmoCubeCost.class);
        cost.put(AmmoCubeCost.RED, 1);

        Map<AmmoCube, Integer> availableAmmo = new EnumMap<>(AmmoCube.class);
        availableAmmo.put(AmmoCube.RED, 2);
        availableAmmo.put(AmmoCube.BLUE, 1);

        assertTrue(AmmoCubeCost.canPayAmmoCost(cost, availableAmmo));
    }

    @Test
    public void canPayAmmoCost_notEnoughAmmo_shouldReturnFalse() {
        /* Create the two maps */
        Map<AmmoCubeCost, Integer> cost = new EnumMap<>(AmmoCubeCost.class);
        cost.put(AmmoCubeCost.RED, 5);

        Map<AmmoCube, Integer> availableAmmo = new EnumMap<>(AmmoCube.class);
        availableAmmo.put(AmmoCube.RED, 2);
        availableAmmo.put(AmmoCube.BLUE, 1);

        assertFalse(AmmoCubeCost.canPayAmmoCost(cost, availableAmmo));
    }

    @Test
    public void mapFromCollection_shouldCountCorrectly() {
        /* Create a collection */
        List<AmmoCubeCost> list = Arrays.asList(AmmoCubeCost.BLUE, AmmoCubeCost.BLUE, AmmoCubeCost.RED);

        Map<AmmoCubeCost, Integer> map = AmmoCubeCost.mapFromCollection(list);

        assertEquals(1, (int) map.getOrDefault(AmmoCubeCost.RED, 0));
        assertEquals(2, (int) map.getOrDefault(AmmoCubeCost.BLUE, 0));
        assertEquals(0, (int) map.getOrDefault(AmmoCubeCost.YELLOW, 0));
        assertEquals(0, (int) map.getOrDefault(AmmoCubeCost.ANY, 0));
    }
}

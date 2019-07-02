package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube.*;
import static org.junit.Assert.*;

public class AmmoCubeTest {

    @Test
    public void colorCodes_shouldBeNotNull() {
        AmmoCube cube = AmmoCube.RED;

        assertNotNull(cube.getAnsiCode());
        assertNotNull(cube.getHexCode());
        assertNotNull(cube.toString());
    }

    @Test
    public void mapFromCollection_shouldCountCorrectly() {
        List<AmmoCube> cubeList = Arrays.asList(BLUE, RED, BLUE, YELLOW, RED, RED);

        Map<AmmoCube, Integer> cubeCount = AmmoCube.mapFromCollection(cubeList);

        assertEquals(2, (int) cubeCount.get(BLUE));
        assertEquals(3, (int) cubeCount.get(RED));
        assertEquals(1, (int) cubeCount.get(YELLOW));
    }
}
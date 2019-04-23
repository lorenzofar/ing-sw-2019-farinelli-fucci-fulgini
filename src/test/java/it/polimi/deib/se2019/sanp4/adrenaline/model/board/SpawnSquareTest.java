package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import org.junit.Test;

import static org.junit.Assert.*;

public class SpawnSquareTest {

    @Test (expected = NullPointerException.class)
    public void instertWeaponCard_NullWeaponProvided_ShouldThrowNullPointerException() throws FullCapacityException {
        CoordPair coordPair = new CoordPair(5,5);
        SpawnSquare spawnSquare = new SpawnSquare(coordPair);
        spawnSquare.insertWeaponCard(null);
    }

    /* TODO: Add more tests */
}
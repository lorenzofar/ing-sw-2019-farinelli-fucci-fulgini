package it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.json.JSONException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PowerupCreatorTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        /* Load JSON schemas needed for validation */
        JSONUtils.loadPowerupPackSchema("/schemas/powerup_pack.schema.json");
    }

    @After
    public void tearDown() throws Exception {
        PowerupCreator.reset();
    }

    @Test
    public void loadPowerupPack_validPack_shouldSucceed() {
        /* Load powerups, should throw nothing */
        PowerupCreator.loadPowerupPack("/assets/powerup_pack_valid.json");

        /* Now check that loading has been performed correctly by requesting a full deck */
        Collection<PowerupCard> cards = PowerupCreator.createPowerupDeck();

        int count;
        /* Check that for each type there are two cards of the same color */
        for (PowerupEnum type : PowerupEnum.values()) {
            for (AmmoCube color : AmmoCube.values()) {
                assertEquals(2, cards.stream()
                        /* Filter type */
                        .filter(p -> p.getType().equals(type))
                        /* Filter color */
                        .filter(p -> p.getCubeColor().equals(color))
                        /* Count occurrences */
                        .count());
            }
        }

        /* Check that there are no additional cards */
        int expectedSize = AmmoCube.values().length * PowerupEnum.values().length * 2;
        assertEquals(expectedSize, cards.size());
    }

    @Test
    public void createPowerupDeck_notLoaded_shouldReturnEmptyDeck() {
        Collection<PowerupCard> cards = PowerupCreator.createPowerupDeck();
        assertTrue(cards.isEmpty());
    }

    @Test(expected = JSONException.class)
    public void loadPowerupPack_invalidSyntax_shouldThrow() {
        PowerupCreator.loadPowerupPack("/assets/powerup_pack_invalid_syntax.json");
    }
}
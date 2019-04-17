package it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.json.JSONException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

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
        Collection<PowerUpCard> cards = PowerupCreator.getPowerupDeck();

        /* Red cubes */
        long count = cards.stream().filter(card -> card.getId().equals("pw1"))
                .filter( card -> card.getCubeColor().equals(AmmoCube.RED)).count();
        assertEquals(1, count);

        /* Yellow cubes */
        count = cards.stream().filter(card -> card.getId().equals("pw1"))
                .filter( card -> card.getCubeColor().equals(AmmoCube.YELLOW)).count();
        assertEquals(2, count);

        /* Blue cubes */
        count = cards.stream().filter(card -> card.getId().equals("pw1"))
                .filter( card -> card.getCubeColor().equals(AmmoCube.BLUE)).count();
        assertEquals(3, count);

        /* Last check there are no additional cards */
        cards.removeIf(card -> card.getId().equals("pw1"));
        assertTrue(cards.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void loadPowerupPack_undeclaredType_shouldThrow() {
        PowerupCreator.loadPowerupPack("/assets/powerup_pack_invalid_undeclared.json");
    }

    @Test(expected = JSONException.class)
    public void loadPowerupPack_invalidSyntax_shouldThrow() {
        PowerupCreator.loadPowerupPack("/assets/powerup_pack_invalid_syntax.json");
    }
}
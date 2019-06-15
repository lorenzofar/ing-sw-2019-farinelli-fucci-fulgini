package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class WeaponCreatorTest {
    @BeforeClass
    public static void classSetUp() {
        /* Load JSON schemas needed for validation */
        JSONUtils.loadWeaponPackSchema("/schemas/weapon_pack.schema.json");
        JSONUtils.loadWeaponSchema("/schemas/weapon.schema.json");
    }

    @After
    public void tearDown() throws Exception {
        /* Bring it to its original state */
        WeaponCreator.reset();
    }

    @Test
    public void loadWeapon_validWeapon_shouldSucceed() {
        WeaponCreator.loadWeapon("/assets/test_weapons/validweapon.json");
        assertTrue(WeaponCreator.isWeaponAvailable("cyberblade"));
        /* Also test reset */
        WeaponCreator.reset();
        assertFalse(WeaponCreator.isWeaponAvailable("cyberblade"));
    }

    @Test(expected = ValidationException.class)
    public void loadWeapon_invalidWeapon_shouldFail() {
        WeaponCreator.loadWeapon("/assets/test_weapons/invalidweapon.json");
    }

    @Test
    public void loadWeaponPack_validPack_shouldSucceed() {
        WeaponCreator.loadWeaponPack("/assets/weapon_pack_valid.json");
        assertTrue(WeaponCreator.isWeaponAvailable("cyberblade"));
    }

    @Test(expected = ValidationException.class)
    public void loadWeaponPack_invalidPack_shouldFail() {
        WeaponCreator.loadWeaponPack("/assets/weapon_pack_invalid.json");
    }

    @Test
    public void createWeaponCard_validWeapon_shouldCreate() throws CardNotFoundException, IOException {
        /* Load a test cyberblade */
        WeaponCreator.loadWeapon("/assets/test_weapons/validweapon.json");

        WeaponCard weapon = WeaponCreator.createWeaponCard("cyberblade");

        /* Check that everything has been built correctly */
        assertEquals("cyberblade", weapon.getId());
        assertEquals("Cyberblade", weapon.getName());
        assertEquals(Arrays.asList(AmmoCubeCost.YELLOW, AmmoCubeCost.RED), weapon.getCost());

        List<EffectDescription> effects = weapon.getEffects();
        assertEquals(3, effects.size());

        /* First effect */
        EffectDescription effect = effects.get(0);
        assertEquals("basic", effect.getId());
        assertEquals("Basic Effect", effect.getName());
        assertEquals("This is the basic effect", effect.getDescription());
        assertEquals(Collections.EMPTY_LIST, effect.getCost());


        /* Second effect */
        effect = effects.get(1);
        assertEquals("shadowstep", effect.getId());
        assertEquals("Shadowstep", effect.getName());
        assertEquals("This a movement effect", effect.getDescription());
        assertEquals(Collections.EMPTY_LIST, effect.getCost());

        /* Third effect */
        effect = effects.get(2);
        assertEquals("slice_and_dice", effect.getId());
        assertEquals("Slice and dice", effect.getName());
        assertEquals("This is an optional effect", effect.getDescription());
        assertEquals(Collections.singletonList(AmmoCubeCost.YELLOW), effect.getCost());
    }

    @Test(expected = CardNotFoundException.class)
    public void getWeaponConfiguration_notLoaded_shouldThrow() {
        WeaponCreator.getWeaponConfiguration("cyberblade");
    }

    @Test
    public void getWeaponConfiguration_loaded_shouldReturn() {
        WeaponCreator.loadWeapon("/assets/test_weapons/validweapon.json");

        JSONObject config = WeaponCreator.getWeaponConfiguration("cyberblade");

        assertEquals("cyberblade", config.getString("id"));
    }

    @Test(expected = CardNotFoundException.class)
    public void createWeaponCard_notLoaded_shouldThrow() throws CardNotFoundException, IOException {
        WeaponCreator.createWeaponCard("cyberblade");
    }

    @Test
    public void createWeaponCardDeck_standardPack_shouldSucceed() throws IOException {
        /* Load and validate the standard pack */
        WeaponCreator.loadWeaponPack("/assets/standard_weapons.json");

        /* Create a deck from the standard pack */
        Collection<WeaponCard> cards = WeaponCreator.createWeaponCardDeck();

        /* Check the number of cards */
        assertEquals(21, cards.size());
    }
}
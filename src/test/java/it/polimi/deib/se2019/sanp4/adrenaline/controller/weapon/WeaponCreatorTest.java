package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapon;

import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.everit.json.schema.ValidationException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

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
    public void loadWeaponPack_standardPack_shouldSucceed() {
        WeaponCreator.loadWeaponPack("/assets/standard_weapons.json");
    }
}
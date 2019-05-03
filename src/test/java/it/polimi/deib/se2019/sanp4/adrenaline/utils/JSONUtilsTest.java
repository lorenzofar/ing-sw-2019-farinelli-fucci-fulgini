package it.polimi.deib.se2019.sanp4.adrenaline.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.MissingResourceException;
import java.util.Set;

public class JSONUtilsTest {
    @After
    public void tearDown() throws Exception {
        /* Always bring it back to its original state */
        JSONUtils.resetSchemas();
    }

    @Test
    public void loadJSONResource_validObject_shouldSucceed() {
        /* Should not throw exceptions and return a non-null object */
        JSONObject obj = JSONUtils.loadJSONResource("/schemas/weapon_pack.schema.json");
        assertNotNull(obj);
    }

    @Test(expected = JSONException.class)
    public void loadJSONResource_invalidObject_shouldFail() {
        JSONObject obj = JSONUtils.loadJSONResource("/schemas/invalid_json.json");
    }

    @Test(expected = MissingResourceException.class)
    public void loadJSONResource_inexistent_shouldFail() {
        JSONUtils.loadJSONResource("/i/do/not/exist");
    }

    @Test
    public void loadSchema_validSchema_shouldSucceed() {
        /* Should throw no exception */
        JSONUtils.loadSchema("/schemas/weapon_pack.schema.json");
        assertTrue(true);
    }

    @Test(expected = MissingResourceException.class)
    public void loadSchema_inexistentSchema_shouldFail() {
        JSONUtils.loadSchema("/i/do/not/exist");
    }

    @Test(expected = IllegalStateException.class)
    public void validateWeapon_schemaNotLoaded_shouldThrow() {
        JSONObject weapon = JSONUtils.loadJSONResource("/assets/test_weapons/validWeapon.json");
        /* Ask the class to validate an object for a schema that has not been loaded yet */
        JSONUtils.validateWeapon(weapon);
    }

    @Test(expected = IllegalStateException.class)
    public void validateWeaponPack_schemaNotLoaded_shouldThrow() {
        JSONObject weaponPack = JSONUtils.loadJSONResource("/assets/weapon_pack_valid.json");
        /* Ask the class to validate an object for a schema that has not been loaded yet */
        JSONUtils.validateWeaponPack(weaponPack);
    }

    @Test(expected = IllegalStateException.class)
    public void validatePowerupPack_schemaNotLoaded_shouldThrow() {
        JSONObject powerupPack = JSONUtils.loadJSONResource("/assets/powerup_pack_valid.json");
        /* Ask the class to validate an object for a schema that has not been loaded yet */
        JSONUtils.validatePowerupPack(powerupPack);
    }

    @Test(expected = IllegalStateException.class)
    public void validateAmmoCardPack_schemaNotLoaded_shouldThrow() {
        JSONObject ammoCardPack = JSONUtils.loadJSONResource("/assets/ammo_card_pack_valid.json");
        /* Ask the class to validate an object for a schema that has not been loaded yet */
        JSONUtils.validateAmmoCardPack(ammoCardPack);
    }

    @Test(expected = IllegalStateException.class)
    public void validateActionCardPack_schemaNotLoaded_shouldThrow() {
        JSONObject actionCardPack = JSONUtils.loadJSONResource("/assets/action_card_pack_valid.json");
        /* Ask the class to validate an object for a schema that has not been loaded yet */
        JSONUtils.validateActionCardPack(actionCardPack);
    }

    @Test
    public void arrayToStringSet_stringArray_shouldSucceed() {
        /* Build valid string array and convert it */
        JSONArray array = new JSONArray("[ \"string1\", \"string2\", \"string3\" ]");
        Set<String> set = JSONUtils.arrayToStringSet(array);
        /* Check items */
        assertTrue(set.contains("string1"));
        assertTrue(set.contains("string2"));
        assertTrue(set.contains("string3"));
        assertEquals(3, set.size());
    }

    @Test(expected = JSONException.class)
    public void arrayToStringSet_mixedArray_shouldThrow() {
        /* Build mixed array */
        JSONArray array = new JSONArray("[ \"string\", false, {}, 1.0 ]");
        JSONUtils.arrayToStringSet(array);
    }
}
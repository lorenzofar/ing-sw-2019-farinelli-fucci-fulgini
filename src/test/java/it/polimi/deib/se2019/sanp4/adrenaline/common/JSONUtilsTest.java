package it.polimi.deib.se2019.sanp4.adrenaline.common;

import it.polimi.deib.se2019.sanp4.adrenaline.common.JSONUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.MissingResourceException;
import java.util.Set;

public class JSONUtilsTest {

    enum TestEnum {
        ONE, TWO, THREE
    }

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
        JSONObject weapon = JSONUtils.loadJSONResource("/assets/test_weapons/validweapon.json");
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

    @Test(expected = IllegalStateException.class)
    public void validateBoardPack_schemaNotLoaded_shouldThrow() {
        JSONObject boardPack = JSONUtils.loadJSONResource("/assets/board_pack_valid.json");
        /* Ask the class to validate an object for a schema that has not been loaded yet */
        JSONUtils.validateBoardPack(boardPack);
    }

    @Test(expected = IllegalStateException.class)
    public void validateBoard_schemaNotLoaded_shouldThrow() {
        JSONObject board = JSONUtils.loadJSONResource("/assets/test_boards/board_valid.json");
        /* Ask the class to validate an object for a schema that has not been loaded yet */
        JSONUtils.validateBoard(board);
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

    @Test
    public void arrayToEnumList_validNames_shouldSucceed() {
        /* Build array of valid names */
        JSONArray array = new JSONArray("[ \"ONE\", \"TWO\", \"THREE\" ]");
        List<TestEnum> list = JSONUtils.arrayToEnumList(TestEnum.class, array);

        /* Check items */
        assertEquals(3, list.size());
        assertEquals(TestEnum.ONE, list.get(0));
        assertEquals(TestEnum.TWO, list.get(1));
        assertEquals(TestEnum.THREE, list.get(2));
    }

    @Test(expected = JSONException.class)
    public void arrayToEnumList_mixedTypes_shouldThrow() {
        /* Build array of valid names */
        JSONArray array = new JSONArray("[ \"string\", false, {}, 1.0 ]");
        JSONUtils.arrayToEnumList(TestEnum.class, array);
    }

    @Test(expected = JSONException.class)
    public void arrayToEnumList_invalidName_shouldThrow() {
        /* Build array of valid names */
        JSONArray array = new JSONArray("[ \"ONE\", \"FOUR\", \"THREE\" ]");
        JSONUtils.arrayToEnumList(TestEnum.class, array);
    }
}
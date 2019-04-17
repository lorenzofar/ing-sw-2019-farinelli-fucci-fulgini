package it.polimi.deib.se2019.sanp4.adrenaline.utils;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;

import java.io.InputStream;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.Set;

/**
 * Provides useful methods to load and validate JSON objects.
 * Please note that JSON schemas are not loaded automatically, so they should be loaded on startup.
 */
public class JSONUtils {
    private static Schema weaponPackSchema;
    private static Schema weaponSchema;
    private static Schema powerupPackSchema;
    private static Schema ammoCardPackSchema;
    /* TODO: Add more schemas */

    /** This class has only static methods and should not be instantiated */
    private JSONUtils() {}

    /**
     * Loads JSON object from given resource path.
     * @param path resource path
     * @return loaded json object
     * @throws MissingResourceException if the specified resource is missing
     * @throws org.json.JSONException if there are errors in the JSON file itself
     */
    public static JSONObject loadJSONResource(String path) {
        InputStream input = JSONUtils.class.getResourceAsStream(path);
        if (input == null) {
            throw new MissingResourceException(String.format("Missing resource \"%s\"", path), "JSON", path);
        }
        return new JSONObject(new JSONTokener(input));
    }

    /**
     * Loads given weapon pack schema.
     * @param schemaPath absolute path of the schema resource
     */
    public static void loadWeaponPackSchema(String schemaPath) {
        weaponPackSchema = loadSchema(schemaPath);
    }

    /**
     * Validates weapon pack against its schema.
     * @param pack weapon pack JSON to validate
     * @throws ValidationException if the pack is not valid
     * @throws IllegalStateException if the validation schema had not been previously loaded
     */
    public static void validateWeaponPack(JSONObject pack) {
        if (weaponPackSchema == null) throw new IllegalStateException("You must load the schema first!");
        weaponPackSchema.validate(pack);
    }

    /**
     * Loads given weapon schema.
     * @param schemaPath absolute path of the schema resource
     */
    public static void loadWeaponSchema(String schemaPath) {
        weaponSchema = loadSchema(schemaPath);
    }

    /**
     * Validates weapon against its schema.
     * @param weapon weapon JSON to validate
     * @throws ValidationException if the pack is not valid
     * @throws IllegalStateException if the validation schema had not been previously loaded
     */
    public static void validateWeapon(JSONObject weapon) {
        if (weaponSchema == null) throw new IllegalStateException("You must load the schema first!");
        weaponSchema.validate(weapon);
    }

    /**
     * Loads powerup pack schema.
     * @param schemaPath absolute path of the schema resource
     */
    public static void loadPowerupPackSchema(String schemaPath){
        powerupPackSchema = loadSchema(schemaPath);
    }

    /**
     * Validates powerup pack against its schema.
     * @param pack powerup pack JSON to validate
     * @throws ValidationException if the pack is not valid
     * @throws IllegalStateException if the validation schema had not been previously loaded
     */
    public static void validatePowerupPack(JSONObject pack){
        if (powerupPackSchema == null) throw new IllegalStateException("You must load the powerup schema first!");
        powerupPackSchema.validate(pack);
    }

    /**
     * Loads given ammo card pack schema.
     * @param schemaPath absolute path of the schema resource
     */
    public static void loadAmmoCardPackSchema(String schemaPath) {
        ammoCardPackSchema = loadSchema(schemaPath);
    }

    /**
     * Validates ammo card pack against its schema.
     * @param pack ammo card pack JSON to validate
     * @throws ValidationException if the pack is not valid
     * @throws IllegalStateException if the validation schema had not been previously loaded
     */
    public static void validateAmmoCardPack(JSONObject pack){
        if (ammoCardPackSchema == null) throw new IllegalStateException("You must load the ammo card pack schema first!");
        ammoCardPackSchema.validate(pack);
    }

    /**
     * Loads and returns a schema from given path.
     * @param schemaPath absolute file path to the schema
     * @return loaded schema
     * @throws MissingResourceException if the schema is missing
     */
    static Schema loadSchema(String schemaPath) {
        return SchemaLoader.load(loadJSONResource(schemaPath));
    }

    /**
     * Converts the given array of strings to a set of strings.
     * @param array array of strings, not null
     * @return set of strings from array
     * @throws JSONException if the array contains something other than strings
     */
    public static Set<String> arrayToStringSet(JSONArray array) {
        Set<String> set = new HashSet<>();
        for (int i=0; i < array.length(); i++) {
            set.add(array.getString(i));
        }
        return set;
    }

    /**
     * Deletes all the schemas loaded until now.
     */
    public static void resetSchemas() {
        weaponPackSchema = null;
        weaponSchema = null;
        powerupPackSchema = null;
        ammoCardPackSchema = null;
    }
}

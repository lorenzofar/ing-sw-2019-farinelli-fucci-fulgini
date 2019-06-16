package it.polimi.deib.se2019.sanp4.adrenaline.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;

import java.io.InputStream;
import java.util.*;

/**
 * Provides useful methods to load and validate JSON objects.
 * Please note that JSON schemas are not loaded automatically, so they should be loaded on startup.
 */
public class JSONUtils {

    private static Schema weaponPackSchema;
    private static Schema weaponSchema;
    private static Schema powerupPackSchema;
    private static Schema ammoCardPackSchema;
    private static Schema actionCardPackSchema;
    private static Schema boardPackSchema;
    private static Schema boardSchema;

    private static final String SCHEMA_NOT_LOADED = "You must load the schema first!";

    /** Object mapper specifically configured for network streams */
    private static final ObjectMapper networkObjectMapper = new ObjectMapper()
            .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
            .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** This class has only static methods and should not be instantiated */
    private JSONUtils() {}

    /**
     * Returns a global ObjectMapper instance from Jackson, with default configuration.
     * Every class that uses an objectmapper should retrieve it from here, since ObjectMapper creation is very slow
     * but it is also thread-safe.
     * @return a global ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Returns a global ObjectMapper instance specifically configured to read/write on network streams.
     * This instance is configured not to automatically close streams when reading/writing.
     * @return a global ObjectMapper instance
     */
    public static ObjectMapper getNetworkObjectMapper() {
        return networkObjectMapper;
    }

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
        if (weaponPackSchema == null) throw new IllegalStateException(SCHEMA_NOT_LOADED);
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
        if (weaponSchema == null) throw new IllegalStateException(SCHEMA_NOT_LOADED);
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
        if (powerupPackSchema == null) throw new IllegalStateException(SCHEMA_NOT_LOADED);
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
        if (ammoCardPackSchema == null) throw new IllegalStateException(SCHEMA_NOT_LOADED);
        ammoCardPackSchema.validate(pack);
    }

    /**
     * Loads action card pack schema.
     * @param schemaPath absolute path of the schema resource
     */
    public static void loadActionCardPackSchema(String schemaPath){
        actionCardPackSchema = loadSchema(schemaPath);
    }

    /**
     * Validates action card pack against its schema.
     * @param pack action card pack JSON to validate
     * @throws ValidationException if the pack is not valid
     * @throws IllegalStateException if the validation schema had not been previously loaded
     */
    public static void validateActionCardPack(JSONObject pack){
        if (actionCardPackSchema == null) throw new IllegalStateException(SCHEMA_NOT_LOADED);
        actionCardPackSchema.validate(pack);
    }

    /**
     * Loads board pack schema.
     * @param schemaPath absolute path of the schema resource
     */
    public static void loadBoardPackSchema(String schemaPath){
        boardPackSchema = loadSchema(schemaPath);
    }

    /**
     * Validates board pack against its schema.
     * @param pack board pack JSON to validate
     * @throws ValidationException if the pack is not valid
     * @throws IllegalStateException if the validation schema had not been previously loaded
     */
    public static void validateBoardPack(JSONObject pack){
        if (boardPackSchema == null) throw new IllegalStateException(SCHEMA_NOT_LOADED);
        boardPackSchema.validate(pack);
    }

    /**
     * Loads given board schema.
     * @param schemaPath absolute path of the schema resource
     */
    public static void loadBoardSchema(String schemaPath) {
        boardSchema = loadSchema(schemaPath);
    }

    /**
     * Validates board description against its schema.
     * @param board board JSON to validate
     * @throws ValidationException if the pack is not valid
     * @throws IllegalStateException if the validation schema had not been previously loaded
     */
    public static void validateBoard(JSONObject board) {
        if (boardSchema == null) throw new IllegalStateException(SCHEMA_NOT_LOADED);
        boardSchema.validate(board);
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
     * Converts the given array of strings to a list of enumerator values of given type
     * @param clazz the type of enum to retrieve, not null
     * @param array array of strings, not null
     * @param <E> the type of enum
     * @return list of enum from array
     * @throws JSONException if the array does not contain valid names of enum values
     */
    public static <E extends Enum<E>> List<E> arrayToEnumList(Class<E> clazz, JSONArray array) {
        List<E> list = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getEnum(clazz, i));
        }
        return list;
    }

    /**
     * Deletes all the schemas loaded until now.
     */
    public static void resetSchemas() {
        weaponPackSchema = null;
        weaponSchema = null;
        powerupPackSchema = null;
        ammoCardPackSchema = null;
        actionCardPackSchema = null;
        boardPackSchema = null;
        boardSchema = null;
    }
}

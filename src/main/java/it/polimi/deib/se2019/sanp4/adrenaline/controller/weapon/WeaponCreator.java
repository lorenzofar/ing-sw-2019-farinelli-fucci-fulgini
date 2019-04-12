package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapon;

import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * Static resource class which loads weapons from configuration file and makes them available as a service.
 * Other classes can retrieve WeaponCards (e.g. to build the card deck) or full weapons, which
 * act as controllers when a player chooses to shoot.
 */
public class WeaponCreator {
    /** The key is the weapon id, the value is the path of its configuration file */
    private static final Map<String, String> weaponConfigMap = new HashMap<>();

    private WeaponCreator() {}

    /**
     * Loads and validates all weapons specified in a weapon pack JSON file.
     * @param filePath absolute path of the pack file
     * @throws MissingResourceException if any of the required resources is not found
     * @throws JSONException if there are errors in the JSON itself
     */
    public static void loadWeaponPack(String filePath) {
        /* Load the file as JSON */
        JSONObject pack = JSONUtils.loadJSONResource(filePath);

        /* Validate it against schema */
        JSONUtils.validateWeaponPack(pack);

        /* If i got here the schema is valid, so no more checks are necessary */
        JSONArray weaponFiles = pack.getJSONArray("weaponFiles");

        /* The array contains resource paths of the weapons */
        for (int i=0; i < weaponFiles.length(); i++) {
            loadWeapon(weaponFiles.getString(i));
        }
    }

    /**
     * Loads and validates a single weapon specified in a JSON weapon configuration file.
     * After this the weapon can be requested to this creator.
     * @param filePath absolute path of the JSON file
     * @throws MissingResourceException if any of the required resources is not found
     * @throws JSONException if there are errors in the JSON itself
     */
    public static void loadWeapon(String filePath) {
        /* Load file as JSON */
        JSONObject weapon = JSONUtils.loadJSONResource(filePath);

        /* Validate it against schema */
        JSONUtils.validateWeapon(weapon);

        /* If we got here it is valid, so save the values in the map */
        String weaponId = weapon.getString("weaponId");
        weaponConfigMap.put(weaponId, filePath);
    }

    /**
     * Checks if a given weapon has been loaded and is available for retrieval.
     * @param weaponId id of the weapon
     * @return whether the weapon is available or not
     */
    public static boolean isWeaponAvailable(String weaponId) {
        return weaponConfigMap.containsKey(weaponId);
    }

    /**
     * Forgets all the weapons loaded until now.
     */
    public static void reset() {
        weaponConfigMap.clear();
    }
}

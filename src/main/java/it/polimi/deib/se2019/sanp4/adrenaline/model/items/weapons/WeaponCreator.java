package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Static resource class which loads weapons from configuration file and makes them available as a service.
 * Other classes can retrieve WeaponCards (e.g. to build the card deck) or full weapons, which
 * act as controllers when a player chooses to shoot.
 */
public class WeaponCreator {
    /** The key is the weapon id, the value is the path of its configuration file */
    private static final Map<String, String> weaponConfigMap = new HashMap<>();

    /** Object mapper used to deserialize weapon cards */
    private static final ObjectMapper objectMapper = JSONUtils.getObjectMapper().copy()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private WeaponCreator() {}

    /**
     * Loads and validates all weapons specified in a weapon pack JSON file.
     * @param filePath absolute path of the pack file
     * @throws MissingResourceException if any of the required resources is not found
     * @throws JSONException if there are errors in the JSON itself
     * @throws ValidationException if the JSON is invalid
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
     * @throws ValidationException if the JSON is invalid
     */
    public static void loadWeapon(String filePath) {
        /* Load file as JSON */
        JSONObject weapon = JSONUtils.loadJSONResource(filePath);

        /* Validate it against schema */
        JSONUtils.validateWeapon(weapon);

        /* If we got here it is valid, so save the values in the map */
        String weaponId = weapon.getString("id");
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
     * Creates a new instance of the required WeaponCard.
     * The weapon with specified id must have been previously loaded by the creator.
     * @param weaponId id of the weapon you want to create
     * @return an object representing the WeaponCard, in its default state
     * @throws CardNotFoundException if the required weapon has not been loaded
     * @throws IOException if anything goes wrong while parsing the JSON
     */
    public static WeaponCard createWeaponCard(String weaponId) throws IOException {
        if (!isWeaponAvailable(weaponId)) throw new CardNotFoundException("Card \"%s\" has not been loaded");

        InputStream input = JSONUtils.class.getResourceAsStream(weaponConfigMap.get(weaponId));
        return objectMapper.readValue(input, WeaponCard.class);
    }

    /**
     * Returns the configuration of a weapon as a JSON tree.
     * @param weaponId identifier of the weapon, not null
     * @return a JSON tree with the configuration of the weapon, as read from file
     * @throws CardNotFoundException if the weapon with given id has not been loaded
     */
    public static JSONObject getWeaponConfiguration(String weaponId) {
        if (!isWeaponAvailable(weaponId)) throw new CardNotFoundException("Card \"%s\" has not been loaded");

        return JSONUtils.loadJSONResource(weaponConfigMap.get(weaponId));
    }

    /**
     * Returns a collection with new instances of the weapon cards loaded until now,
     * suitable for initialising a card stack.
     * @return the collection of loaded weapon cards
     * @throws IOException if a card that was previously loaded cannot be read from file
     * (should never happen in normal scenarios)
     */
    public static Collection<WeaponCard> createWeaponCardDeck() throws IOException {
        Collection<WeaponCard> cards = new LinkedList<>();
        InputStream input;
        WeaponCard card;

        /* Iterate on the weapon config files */
        for (String path : weaponConfigMap.values()) {
            input = JSONUtils.class.getResourceAsStream(path);
            card = objectMapper.readValue(input, WeaponCard.class);
            cards.add(card);
        }

        return cards;
    }

    /**
     * Forgets all the weapons loaded until now.
     */
    public static void reset() {
        weaponConfigMap.clear();
    }
}

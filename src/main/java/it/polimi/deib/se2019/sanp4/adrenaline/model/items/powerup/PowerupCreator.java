package it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.common.JSONUtils;
import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Static resource class which loads powerups from JSON file and makes them available
 * for retrieval.
 * After the initial loading from file, any class can request to build a stack (a.k.a. deck) of powerups.
 */
public class PowerupCreator {

    /** The first key is the type of the powerup, the second key is the color of that type, the value is the number
     * of powerups in the deck for that particular color/type combination
     */
    private static Map<PowerupEnum, Map<AmmoCube, Integer>> powerupMap;

    private static ObjectMapper mapper = JSONUtils.getObjectMapper();

    /** The class is static, so it cannot be instantiated */
    private PowerupCreator(){}

    /**
     * Loads and validates all powerups specified in a powerup pack file.
     * Note that if another pack was previously loaded, the loaded content is overwritten
     * @param filePath resource path of the Powerup Pack JSON file
     * @throws MissingResourceException if the required file is not found
     * @throws JSONException if anything goes wrong while parsing JSON
     * @throws ValidationException if the JSON is invalid
     */
    public static void loadPowerupPack(String filePath){
        /* Parse the pack */
        JSONObject pack = JSONUtils.loadJSONResource(filePath);

        /* Validate it */
        JSONUtils.validatePowerupPack(pack);

        /* Use Jackson to do the job */
        InputStream input = PowerupCreator.class.getResourceAsStream(filePath);
        try {
            powerupMap = mapper.readValue(input, new TypeReference<Map<PowerupEnum, Map<AmmoCube, Integer>>>() {});
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    /**
     * Returns a collection with all the powerup cards in the deck.
     * The objects in the list are all distinct and immutable, i.e. the're suitable to be inserted in
     * the powerup stack.
     * @return a collection with all the powerups in the deck
     */
    public static Collection<PowerupCard> createPowerupDeck() {
        /* Build the empty list */
        Collection<PowerupCard> deck = new LinkedList<>();

        if (powerupMap != null) {
            /* Loop on each powerup type and color */
            powerupMap.forEach((type, colors) -> colors.forEach((color, count) -> {
                for (int i = 0; i < count; i++) {
                    /* Add the specified amount of powerups of given type and color */
                    deck.add(new PowerupCard(type, color));
                }
            }));
        }

        return deck;
    }

    /**
     * Forgets all the information read untill now.
     * The class is brought back to its original state
     */
    static void reset(){
        if (powerupMap != null) powerupMap = null;
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Static resource class which loads powerups from JSON file and makes them available
 * for retrieval.
 * After the initial loading from file, any class can request to build a stack (a.k.a. deck) of powerups.
 */
public class PowerupCreator {

    /* Utility class to store info for each powerup on the deck */
    private static class PowerupInfo {
        private final String id;
        private final String name;
        private final String description;
        /* Saves the number of powerups of this type on the deck for each color */
        private final Map<AmmoCube, Integer> colorCount;

        PowerupInfo(String id, String name, String description) {
            if (id == null || name == null || description == null) throw new NullPointerException();
            if (id.isEmpty() || name.isEmpty() || description.isEmpty()) {
                throw new IllegalArgumentException("Found empty initialization field");
            }
            this.id = id;
            this.name = name;
            this.description = description;

            /* Initialize the colorCount for each color to zero */
            this.colorCount = new EnumMap<>(AmmoCube.class);
            for (AmmoCube color : AmmoCube.values()){
                colorCount.put(color, 0);
            }
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        void addAmount(AmmoCube color, int increment){
            if (increment < 0) throw new IllegalArgumentException("Increment cannot be negative!");
            /* Get the old value */
            int old = colorCount.get(color);
            colorCount.put(color, old + increment);
        }

        public Map<AmmoCube, Integer> getColorCount() {
            return colorCount;
        }
    }

    /** For each powerup type(key) holds information about the powerup itself and how many of them will be on the deck */
    private static final Map<String, PowerupInfo> powerupInfoMap = new HashMap<>();

    /** The class is static, so it cannot be instantiated */
    private PowerupCreator(){}

    /**
     * Loads and validates all powerups specified in a powerup pack file
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

        /* Now extract the different types and build an info class for each */
        JSONArray pwTypes = pack.getJSONArray("types");

        for (int i = 0; i < pwTypes.length(); i++) {
            loadPowerupType(pwTypes.getJSONObject(i));
        }

        /* Now load and set the parameters for the deck (color and count) */
        JSONArray deck = pack.getJSONArray("deck");

        for (int i = 0; i < deck.length(); i++) {
            loadDeckEntry(deck.getJSONObject(i));
        }
    }

    /**
     * Returns a collection with all the powerup cards in the deck.
     * The objects in the list are all distinct and immutable, i.e. the're suitable to be inserted in
     * the powerup stack.
     * @return a collection with all the powerups in the deck
     */
    public static Collection<PowerUpCard> getPowerupDeck() {
        /* Build the empty list */
        Collection<PowerUpCard> list = new LinkedList<>();

        /* Loop on each powerup type */
        for(PowerupInfo info : powerupInfoMap.values()) {
            /* Get the count for each color in a map */
            Map<AmmoCube, Integer> colorCount = info.getColorCount();

            for (Map.Entry<AmmoCube, Integer> entry: colorCount.entrySet()) {
                /* Key is color, value is count */
                /* Add the specified amount of cards of this color to the list */
                for (int i = 0; i < entry.getValue(); i++) {
                    list.add(new PowerUpCard(
                            info.getId(),
                            info.getName(),
                            info.getDescription(),
                            entry.getKey()
                    ));
                }
            }
        }

        return list;
    }

    /**
     * Forgets all the information read untill now.
     * The class is brought back to its original state
     */
    public static void reset(){
        powerupInfoMap.clear();
    }

    /**
     * Utility function to load and build the PowerupInfo object for each type.
     * @param pwType JSON object from the "types" array
     */
    private static void loadPowerupType(JSONObject pwType) {
        /* Extract information from JSON attributes */
        String id = pwType.getString("id");
        PowerupInfo info = new PowerupInfo(
                id, pwType.getString("name"), pwType.getString("description"));

        /* Insert it into the map */
        powerupInfoMap.put(id, info);
    }

    /**
     * Utility function: loads each entry of the "deck" array into the PowerupInfo object.
     * @param entry JSON entry in the "deck" array
     */
    private static void loadDeckEntry(JSONObject entry) {

        /* Check that the required powerup type exists */
        String id = entry.getString("type");
        if (!powerupInfoMap.containsKey(id)){
            throw new IllegalArgumentException(String.format("Powerup type \"%s\" has not been declared", id));
        }

        /* Extract remaining fields */
        AmmoCube color = AmmoCube.valueOf(entry.getString("color"));
        int count = entry.getInt("count");

        /* Increment the count */
        PowerupInfo info = powerupInfoMap.get(id);
        info.addAmount(color, count);
    }
}

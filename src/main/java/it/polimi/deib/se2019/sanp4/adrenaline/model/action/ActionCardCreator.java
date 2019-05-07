package it.polimi.deib.se2019.sanp4.adrenaline.model.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * Shared object responsible for building action cards from file
 * and returning them when asked.
 * The returned action cards are not cloned, but they are immutable
 * so it's safe (and suggested) to share them among players and matches.
 */
public class ActionCardCreator {
    private static final Map<ActionCardEnum, ActionCard> cards
            = new EnumMap<>(ActionCardEnum.class);

    /* Commodity */
    private static ObjectMapper objectMapper = JSONUtils.getObjectMapper();

    /* This class is static and should not be instantiated */
    private ActionCardCreator() {}

    /**
     * Loads and validates all the action cards in the specified action card pack.
     * If two cards with the same id are found, the latter is kept.
     * @param filePath resource path of the action card pack JSON file
     * @throws MissingResourceException if the required file is not found
     * @throws JSONException if anything goes wrong while parsing JSON
     * @throws ValidationException if the JSON is invalid
     */
    public static void loadActionCardPack(String filePath){
        /* Parse the file */
        JSONObject pack = JSONUtils.loadJSONResource(filePath);
        /* Validate it */
        JSONUtils.validateActionCardPack(pack);
        /* Extract the array */
        JSONArray array = pack.getJSONArray("actionCards");

        /* Load each card */
        for (int i = 0; i < array.length(); i++) {
            ActionCardCreator.loadActionCard(array.getJSONObject(i));
        }
    }

    /**
     * Loads an action card from given JSON object.
     * @param card JSON object representing the action card, as extracted by the "actionCards" array
     */
    static void loadActionCard(JSONObject card) {
        try {
            ActionCard deserialized = objectMapper.readValue(card.toString(), ActionCard.class);
            cards.put(deserialized.getType(), deserialized);
        } catch (IOException e) {
            /* This exception never happens because we are not loading from file */
        }
    }

    /**
     * Returns the action card associated to given type.
     * @param type type of the action card, not null
     * @return action card associated to type
     * @throws CardNotFoundException if requested card type does not exist
     */
    public static ActionCard createActionCard(ActionCardEnum type) throws CardNotFoundException {
        if(type == null){
            throw new NullPointerException("Action card type cannot be null");
        }
        ActionCard card = cards.get(type);

        if (card == null){
            throw new CardNotFoundException(String.format("Action card type %s does not exist", type.name()));
        }

        return card;
    }

    /**
     * Forgets all the cards loaded until now, i.e. brings the class back to its original state.
     */
    static void reset(){
        cards.clear();
    }
}

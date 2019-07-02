package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.JSONUtils;
import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Static resource class which loads ammo cards from a JSON file and
 * makes the ammo card deck available for retrieval.
 * After loading from file any class can request to build the deck of ammo cards.
 * The returned cards are not unique, but they are shared in a flyweight fashion;
 * initial loading on a single thread anc immutability of the AmmoCard class makes
 * this operation absolutely thread-safe.
 */
public class AmmoCardCreator {
    /** A static copy of the deck */
    private static Collection<AmmoCard> ammoCards = new ArrayList<>();

    /* Commodity */
    private static ObjectMapper objectMapper = JSONUtils.getObjectMapper();

    private static Logger logger = Logger.getLogger(AmmoCardCreator.class.getName());

    /** This class is static and it should be impossible to instantiate it */
    private AmmoCardCreator(){}

    /**
     * Loads and validates all the cards in the specified ammo card pack.
     * If two cards with the same id are found in the deck, only the first one will be loaded.
     * @param filePath resource path of the ammo card pack JSON file
     * @throws MissingResourceException if the required file is not found
     * @throws JSONException if anything goes wrong while parsing JSON
     * @throws ValidationException if the JSON is invalid
     */
    public static void loadAmmoCardPack(String filePath){
        /* Parse the file */
        JSONObject pack = JSONUtils.loadJSONResource(filePath);

        /* Validate it */
        JSONUtils.validateAmmoCardPack(pack);

        /* Extract the deck array */
        JSONArray deck = pack.getJSONArray("deck");

        /* Add each card */
        for (int i = 0; i < deck.length(); i++) {
            JSONObject card = deck.getJSONObject(i);
            loadAmmoCard(card);
        }
    }

    /**
     * Loads an ammo card from given JSON object. The card is not loaded if a card
     * with the same id is already present.
     * @param card JSON object representing the ammo card, as extracted by the "deck" array
     */
    static void loadAmmoCard(JSONObject card){
        /* Check if a card with the same id exists (avoid conflicts) */
        if (ammoCards.stream().noneMatch(c -> c.getId() == card.getInt("id"))) {
            /* Build the card */
            AmmoCard deserialized;

            try {
                deserialized = objectMapper.readValue(card.toString(), AmmoCard.class);
                ammoCards.add(deserialized);
            } catch (IOException e) {
                /* This exception never happens because we are not loading from file */
            }
        } else {
            logger.log(Level.SEVERE, "Cannot load weapon ammo with id {0} because a card with the" +
                    "same id already exists", card.getInt("id"));
        }
    }

    /**
     * Returns the ammo card associated with this index.
     * @param id identifier of the ammo card
     * @return AmmoCard instance
     * @throws CardNotFoundException if the index is not associated with a card
     */
    public static AmmoCard getAmmoCard(int id) {
        Optional<AmmoCard> card = ammoCards.stream().filter(c -> c.getId() == id).findFirst();
        if (!card.isPresent()) throw new CardNotFoundException("Cannot create AmmoCard with inexistent id " + id);
        return card.get();
    }

    /**
     * Returns an unmodifiable collection with all ammo cards in the deck,
     * the cards are immutable and shared in a flyweight fashion.
     * This is suitable to initialize a card stack.
     * @return unmodifiable collection containing the ammo cards in the deck
     */
    public static Collection<AmmoCard> getAmmoCardDeck(){
        return Collections.unmodifiableCollection(ammoCards);
    }

    /**
     * Forgets all the cards loaded until now, i.e. brings the class back to its original state.
     */
    static void reset(){
        ammoCards.clear();
    }

}

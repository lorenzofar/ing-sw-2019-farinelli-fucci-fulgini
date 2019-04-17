package it.polimi.deib.se2019.sanp4.adrenaline.model.action;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;

import java.util.EnumMap;
import java.util.Map;

/**
 * Shared object responsible for building action cards from file
 * and returning them when asked.
 * The returned action cards are not cloned, but they are immutable
 * so it's safe (and suggested) to share them among players and matches.
 */
public class ActionCardCreator {
    private final Map<ActionCardEnum, ActionCard> cards;

    /*TODO: Load cards from JSON file*/
    public ActionCardCreator() {
        cards = new EnumMap<>(ActionCardEnum.class);
    }

    /**
     * Returns the action card associated to given type.
     *
     * @param type type of the action card, not null
     * @return action card associated to type
     * @throws CardNotFoundException if requested card type does not exist
     */
    public ActionCard createActionCard(ActionCardEnum type) throws CardNotFoundException {
        if(type == null){
            throw new NullPointerException("Action card type cannot be null");
        }
        ActionCard card = cards.get(type);

        if (card == null){
            throw new CardNotFoundException();
        }

        return card;
    }
}

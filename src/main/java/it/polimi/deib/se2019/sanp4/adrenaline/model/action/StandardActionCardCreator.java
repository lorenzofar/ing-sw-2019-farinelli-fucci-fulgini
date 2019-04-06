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
public class StandardActionCardCreator implements ActionCardCreator {
    private final Map<ActionCardEnum, ActionCard> cards;

    /*TODO: Load cards from JSON file*/
    public StandardActionCardCreator() {
        cards = new EnumMap<ActionCardEnum, ActionCard>(ActionCardEnum.class);
    }

    /**
     * Returns the action card associated to given type.
     *
     * @param type type of the action card
     * @return action card associated to type
     * @throws CardNotFoundException
     */
    @Override
    public ActionCard createActionCard(ActionCardEnum type) throws CardNotFoundException {
        ActionCard card = cards.get(type);

        if (card == null){
            throw new CardNotFoundException();
        }

        return card;
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.model.action;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;

/* TODO: Add some description */
public interface ActionCardCreator {

    /**
     * Returns the action card associated to given type.
     * @param type type of the action card
     * @return action card associated to type
     * @throws it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException
     */
    public ActionCard createActionCard(ActionCardEnum type) throws CardNotFoundException;
}

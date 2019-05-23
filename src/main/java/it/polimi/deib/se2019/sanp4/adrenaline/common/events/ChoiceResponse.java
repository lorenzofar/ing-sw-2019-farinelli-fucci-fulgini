package it.polimi.deib.se2019.sanp4.adrenaline.common.events;

import java.io.Serializable;

/**
 * A specialized class describing an event generated when a player makes a choice
 */
public abstract class ChoiceResponse<T extends Serializable> extends ViewEvent {

    private static final long serialVersionUID = -1847346507142333468L;

    /** The choice made by the player */
    private T choice;

    /**
     * Retrieves the choice made by the user
     * @return The object representing the choice
     */
    public T getChoice(){
        return choice;
    }
}

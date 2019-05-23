package it.polimi.deib.se2019.sanp4.adrenaline.common.events;

/**
 * A specialized class describing an event generated when a player makes a choice
 */
public abstract class ChoiceEvent<T> extends ViewEvent {

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

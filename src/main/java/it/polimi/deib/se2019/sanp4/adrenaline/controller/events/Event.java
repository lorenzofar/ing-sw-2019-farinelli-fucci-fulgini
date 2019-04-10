package it.polimi.deib.se2019.sanp4.adrenaline.controller.events;

/**
 * An abstract class representing a generic event the controller can handle.
 * It holds information about the player who has generated it.
 */
public abstract class Event {

    /** The username of the player*/
    private String sender;

    /**
     * Retrieves the player that has generated the event
     * @return The username of the player
     */
    public String getSender(){
        return sender;
    }

}

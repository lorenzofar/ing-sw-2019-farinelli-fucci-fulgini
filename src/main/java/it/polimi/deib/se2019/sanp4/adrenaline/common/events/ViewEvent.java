package it.polimi.deib.se2019.sanp4.adrenaline.common.events;

import java.io.Serializable;

/**
 * An abstract class representing a generic event the controller can handle.
 * It holds information about the player who has generated it.
 */
public abstract class ViewEvent implements Serializable {

    private static final long serialVersionUID = 7324130186876242534L;
    
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

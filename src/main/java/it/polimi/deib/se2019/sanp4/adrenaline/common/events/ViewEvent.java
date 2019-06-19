package it.polimi.deib.se2019.sanp4.adrenaline.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * An abstract class representing a generic event the controller can handle.
 * It holds information about the player who has generated it.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
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

    /**
     * Creates a new view event with provided sender
     * @param sender the name of the user who sent this, not null
     */
    @JsonCreator
    public ViewEvent(@JsonProperty("sender") String sender) {
        if (sender == null) throw new NullPointerException("Sender name cannot be null");
        this.sender = sender;
    }

    /**
     * Accepts to be visited (handled) by given visitor
     * @param visitor the visitor
     */
    public abstract void accept(ViewEventVisitor visitor);
}

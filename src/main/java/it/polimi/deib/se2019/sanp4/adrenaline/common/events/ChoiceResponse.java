package it.polimi.deib.se2019.sanp4.adrenaline.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * A specialized class describing an event generated when a player makes a choice
 */
public class ChoiceResponse<T extends Serializable> extends ViewEvent {

    private static final long serialVersionUID = -1847346507142333468L;

    /** The unique identifier of the request which this responds to */
    private String uuid;

    /** The choice made by the player */
    private T choice;

    /**
     * Retrieves the choice made by the user
     * @return The object representing the choice
     */
    public T getChoice(){
        return choice;
    }

    /**
     * Returns the identifier of the request which this responds to
     * @return identifier of the request
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Creates a new choice response
     * @param sender the name of the player sending this, not null
     * @param uuid unique identifier of the request that this responds to, not null
     * @param choice the chosen choice
     */
    @JsonCreator
    public ChoiceResponse(
            @JsonProperty("sender") String sender,
            @JsonProperty("uuid") String uuid,
            @JsonProperty("choice") T choice
    ) {
        super(sender);
        if (uuid == null) throw new NullPointerException("uuid cannot be null");
        this.uuid = uuid;
        this.choice = choice;
    }

    /**
     * Accepts to be visited (handled) by given visitor
     *
     * @param visitor the visitor
     */
    @Override
    public void accept(ViewEventVisitor visitor) {
        visitor.visit(this);
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;

import java.util.List;

/** A specialized request to ask for a powerup card */
public class PowerupCardRequest extends ChoiceRequest<PowerupCard> {

    private static final long serialVersionUID = -2902511575406764592L;

    /**
     * Creates a new weapon card request
     *
     * @param message  The message associated to the request
     * @param choices  The list of objects representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     * @param uuid     unique identifier of the request, if not provided it will be auto-generated
     */
    @JsonCreator
    public PowerupCardRequest(
            @JsonProperty("message") String message,
            @JsonProperty("choices") List<PowerupCard> choices,
            @JsonProperty("optional") boolean optional,
            @JsonProperty("uuid") String uuid) {
        super(message, choices, optional, PowerupCard.class, uuid);
    }

    /**
     * Creates a new weapon card request, with auto-generated uuid
     *
     * @param message  The message associated to the request
     * @param choices  The list of objects representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     */
    public PowerupCardRequest(String message, List<PowerupCard> choices, boolean optional) {
        super(message, choices, optional, PowerupCard.class);
    }

    @Override
    public void accept(ChoiceRequestVisitor visitor) {
        visitor.handle(this);
    }
}

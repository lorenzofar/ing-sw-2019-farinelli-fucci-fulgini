package it.polimi.deib.se2019.sanp4.adrenaline.common.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** A specialized request to ask for the name of a player */
public class PlayerRequest extends ChoiceRequest<String> {

    private static final long serialVersionUID = 5158410776370103882L;

    /**
     * Creates a new weapon card request
     *
     * @param message  The message associated to the request
     * @param choices  The list of objects representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     * @param uuid     unique identifier of the request, if not provided it will be auto-generated
     */
    @JsonCreator
    public PlayerRequest(
            @JsonProperty("message") String message,
            @JsonProperty("choices") List<String> choices,
            @JsonProperty("optional") boolean optional,
            @JsonProperty("uuid") String uuid) {
        super(message, choices, optional, String.class, uuid);
    }

    /**
     * Creates a new weapon card request, with auto-generated uuid
     *
     * @param message  The message associated to the request
     * @param choices  The list of objects representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     */
    public PlayerRequest(String message, List<String> choices, boolean optional) {
        super(message, choices, optional, String.class);
    }
}

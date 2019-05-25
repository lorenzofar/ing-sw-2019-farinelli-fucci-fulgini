package it.polimi.deib.se2019.sanp4.adrenaline.common.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the request of the initial number of skulls to start the match
 */
public class SkullCountRequest extends ChoiceRequest<Integer> {


    private static final long serialVersionUID = 894792036924073175L;

    /**
     * Creates a new request
     *
     * @param choices  The list of objects representing the available choices
     * @param uuid     unique identifier of the request, if not provided it will be auto-generated
     */
    @JsonCreator
    public SkullCountRequest(
            @JsonProperty("choices") List<Integer> choices,
            @JsonProperty("uuid") String uuid) {
        super("Please select the number of skulls", choices, false, Integer.class, uuid);
    }

    /**
     * Creates a new request, with auto-generated uuid
     *
     * @param choices  The list of objects representing the available choices
     */
    public SkullCountRequest(List<Integer> choices) {
        super("Please select the number of skulls", choices, false, Integer.class);
    }
}

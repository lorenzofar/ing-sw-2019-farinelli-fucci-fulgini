package it.polimi.deib.se2019.sanp4.adrenaline.common.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the request of the board id at the beginning of the game
 */
public class BoardRequest extends ChoiceRequest<Integer> {

    private static final long serialVersionUID = -5466720497153005083L;

    /**
     * Creates a new request
     *
     * @param choices  The list of objects representing the available choices
     * @param uuid     unique identifier of the request, if not provided it will be auto-generated
     */
    @JsonCreator
    public BoardRequest(
            @JsonProperty("choices") List<Integer> choices,
            @JsonProperty("uuid") String uuid) {
        super("Please select the board", choices, false, Integer.class, uuid);
    }

    /**
     * Creates a new request, with auto-generated uuid
     *
     * @param choices  The list of objects representing the available choices
     */
    public BoardRequest(List<Integer> choices) {
        super("Please select the board", choices, false, Integer.class);
    }
}

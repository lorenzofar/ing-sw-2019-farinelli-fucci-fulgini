package it.polimi.deib.se2019.sanp4.adrenaline.common.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PlayerOperationEnum;

import java.util.List;

/** A specialized request to ask for the operation to be performed during a turn */
public class PlayerOperationRequest extends ChoiceRequest<PlayerOperationEnum> {

    private static final long serialVersionUID = -3091597394450215066L;

    /**
     * Creates a new weapon card request
     *
     * @param choices  The list of objects representing the available choices
     * @param uuid     unique identifier of the request, if not provided it will be auto-generated
     */
    @JsonCreator
    public PlayerOperationRequest(
            @JsonProperty("choices") List<PlayerOperationEnum> choices,
            @JsonProperty("uuid") String uuid) {
        super("Choose the operation you want to perform", choices, false, PlayerOperationEnum.class, uuid);
    }

    /**
     * Creates a new weapon card request, with auto-generated uuid
     *
     * @param choices  The list of objects representing the available choices
     */
    public PlayerOperationRequest(List<PlayerOperationEnum> choices) {
        super("Choose the operation you want to perform", choices, false, PlayerOperationEnum.class);
    }

    @Override
    public void accept(ChoiceRequestVisitor visitor) {
        visitor.handle(this);
    }
}

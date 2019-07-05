package it.polimi.deib.se2019.sanp4.adrenaline.common.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;

import java.util.List;

/**
 * A specialized request to ask for the action the user wants to perform
 *
 * @author Alessandro Fulgini
 */
public class ActionRequest extends ChoiceRequest<ActionEnum> {

    private static final long serialVersionUID = -8594391460674651023L;

    /**
     * Creates a new weapon card request
     *
     * @param message  The message associated to the request
     * @param choices  The list of objects representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     * @param uuid     unique identifier of the request, if not provided it will be auto-generated
     */
    @JsonCreator
    public ActionRequest(
            @JsonProperty("message") String message,
            @JsonProperty("choices") List<ActionEnum> choices,
            @JsonProperty("optional") boolean optional,
            @JsonProperty("uuid") String uuid) {
        super(message, choices, optional, ActionEnum.class, uuid);
    }

    /**
     * Creates a new weapon card request, with auto-generated uuid
     *
     * @param message  The message associated to the request
     * @param choices  The list of objects representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     */
    public ActionRequest(String message, List<ActionEnum> choices, boolean optional) {
        super(message, choices, optional, ActionEnum.class);
    }

    /**
     * Accepts to be visited by a visitor, which may properly handle this request
     *
     * @param visitor The visitor which is trying to visit this, not null
     */
    @Override
    public void accept(ChoiceRequestVisitor visitor) {
        visitor.handle(this);
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.EffectDescription;

import java.util.List;

/**
 * Represents the request to choose which effect to use on a weapon
 */
public class EffectRequest extends ChoiceRequest<EffectDescription> {

    private static final long serialVersionUID = 752235591632486019L;

    /**
     * Creates a new request
     *
     * @param message  The message associated to the request
     * @param choices  The list of objects representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     * @param uuid     unique identifier of the request, if not provided it will be auto-generated
     */
    @JsonCreator
    public EffectRequest(
            @JsonProperty("message") String message,
            @JsonProperty("choices") List<EffectDescription> choices,
            @JsonProperty("optional") boolean optional,
            @JsonProperty("uuid") String uuid) {
        super(message, choices, optional, EffectDescription.class, uuid);
    }

    /**
     * Creates a new request, with auto-generated uuid
     *
     * @param message  The message associated to the request
     * @param choices  The list of objects representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     */
    public EffectRequest(String message, List<EffectDescription> choices, boolean optional) {
        super(message, choices, optional, EffectDescription.class);
    }

    @Override
    public void accept(ChoiceRequestVisitor visitor) {
        visitor.handle(this);
    }
}

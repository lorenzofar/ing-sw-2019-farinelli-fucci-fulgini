package it.polimi.deib.se2019.sanp4.adrenaline.common.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;

import java.util.List;

/**
 * A specialized request to ask for a weapon card
 */
public class WeaponCardRequest extends ChoiceRequest<WeaponCard> {

    private static final long serialVersionUID = 7286352744493657016L;

    /**
     * Creates a new weapon card request
     *
     * @param message  The message associated to the request
     * @param choices  The list of objects representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     * @param uuid     unique identifier of the request, if not provided it will be auto-generated
     */
    @JsonCreator
    public WeaponCardRequest(
            @JsonProperty("message") String message,
            @JsonProperty("choices") List<WeaponCard> choices,
            @JsonProperty("optional") boolean optional,
            @JsonProperty("uuid") String uuid) {
        super(message, choices, optional, WeaponCard.class, uuid);
    }

    /**
     * Creates a new weapon card request, with auto-generated uuid
     *
     * @param message  The message associated to the request
     * @param choices  The list of objects representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     */
    public WeaponCardRequest(String message, List<WeaponCard> choices, boolean optional) {
        super(message, choices, optional, WeaponCard.class);
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

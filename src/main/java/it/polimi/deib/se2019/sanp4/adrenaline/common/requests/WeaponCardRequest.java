package it.polimi.deib.se2019.sanp4.adrenaline.common.requests;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;

import java.util.List;

/** A specialized request to ask for a weapon card */
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
    public WeaponCardRequest(String message, List<WeaponCard> choices, boolean optional, String uuid) {
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
}

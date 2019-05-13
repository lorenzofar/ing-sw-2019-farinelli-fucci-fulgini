package it.polimi.deib.se2019.sanp4.adrenaline.controller.request;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.Request;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;

import java.util.List;

/** A specialized request to ask for a weapon card */
public class WeaponCardRequest extends Request<WeaponCard> {

    /**
     * Creates a new request for a weapon card
     *
     * @param message  The message associated to the request
     * @param choices  The list of weapon cards representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     */
    public WeaponCardRequest(String message, List<WeaponCard> choices, boolean optional) {
        super(message, choices, optional);
    }
}

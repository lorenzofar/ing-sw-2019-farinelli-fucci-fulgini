package it.polimi.deib.se2019.sanp4.adrenaline.controller.request;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.Request;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;

import java.util.Set;

/** A specialized request to ask for a coordinates pair (namely a square) */
public class CoordPairRequest extends Request<CoordPair> {
    /**
     * Creates a new request
     *
     * @param message  The message associated to the request
     * @param choices  The list of coord pairs representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     */
    public CoordPairRequest(String message, Set<CoordPair> choices, boolean optional) {
        super(message, choices, optional);
    }
}

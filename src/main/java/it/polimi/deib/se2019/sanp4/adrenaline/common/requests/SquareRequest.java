package it.polimi.deib.se2019.sanp4.adrenaline.common.requests;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;

import java.util.List;

/** A specialized request to ask for a coordinates pair (namely a square) */
public class SquareRequest extends ChoiceRequest<CoordPair> {

    /**
     * Creates a new square request
     *
     * @param message  The message associated to the request
     * @param choices  The list of objects representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     * @param uuid     unique identifier of the request, if not provided it will be auto-generated
     */
    public SquareRequest(String message, List<CoordPair> choices, boolean optional, String uuid) {
        super(message, choices, optional, CoordPair.class, uuid);
    }

    /**
     * Creates a new square request, with auto-generated uuid
     *
     * @param message  The message associated to the request
     * @param choices  The list of objects representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     */
    public SquareRequest(String message, List<CoordPair> choices, boolean optional) {
        super(message, choices, optional, CoordPair.class);
    }
}

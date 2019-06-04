package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;

/**
 * A lightweight representation of an ammo square in the view.
 */
public class AmmoSquareView extends SquareView {

    private static final long serialVersionUID = -5130799856399259160L;

    //TODO: decide whether to put the ammocard or not

    /**
     * Creates a new ammo square view
     * @param location the coordinates of the square
     * @param roomColor the color of the room the square is in
     */
    @JsonCreator
    public AmmoSquareView(
            @JsonProperty("location") CoordPair location,
            @JsonProperty("roomColor") RoomColor roomColor) {
        super(location, roomColor);
    }

    /**
     * Retrieves the marker indicating the type of the square
     * Each subclass of SquareView will print a different marker
     *
     * @return The string representing the marker
     */
    @Override
    public String printTypeMarker() {
        return "A";
    }
}

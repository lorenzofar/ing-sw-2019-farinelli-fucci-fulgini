package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;

/**
 * A lightweight representation of an ammo square in the view.
 */
public class AmmoSquareView extends SquareView {

    private static final long serialVersionUID = -5130799856399259160L;

    /**
     * Private constructor to be used only by Jackson.
     */
    @JsonCreator
    private AmmoSquareView() {
        super();
    }

    public AmmoSquareView(CoordPair location, RoomColor roomColor) {
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

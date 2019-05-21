package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;

public class AmmoSquareView extends SquareView {
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
    public String getTypeMarker() {
        return "A";
    }
}

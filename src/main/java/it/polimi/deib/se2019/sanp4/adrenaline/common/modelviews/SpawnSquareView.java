package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;

/**
 * A lightweight representation of a spawn square in the view.
 */
public class SpawnSquareView extends SquareView {

    private static final long serialVersionUID = 157597379039936760L;

    /**
     * Private constructor to be used only by Jackson.
     */
    @JsonCreator
    private SpawnSquareView() {
        super();
    }

    public SpawnSquareView(CoordPair location, RoomColor roomColor) {
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
        return "S";
    }
}

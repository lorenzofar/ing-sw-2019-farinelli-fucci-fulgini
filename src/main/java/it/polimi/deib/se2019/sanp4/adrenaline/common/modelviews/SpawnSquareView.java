package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;

import java.util.ArrayList;
import java.util.List;

/**
 * A lightweight representation of a spawn square in the view.
 */
public class SpawnSquareView extends SquareView {

    private static final long serialVersionUID = 157597379039936760L;

    /**
     * List of the weapons contained in the square
     */
    private List<String> weapons;

    /**
     * Private constructor to be used only by Jackson.
     */
    @JsonCreator
    private SpawnSquareView() {
        super();
    }

    public SpawnSquareView(CoordPair location, RoomColor roomColor) {
        super(location, roomColor);
        weapons = new ArrayList<>();
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

    /**
     * Retrieves the weapons contained in the square
     * @return The list of ids of the weapons
     */
    public List<String> getWeapons() {
        return new ArrayList<>(weapons);
    }

    /**
     * Sets the weapons contained in the square
     * @param weapons The list of ids of the weapons
     */
    public void setWeapons(List<String> weapons) {
        if (weapons != null && !weapons.contains(null)) {
            this.weapons = weapons;
        }
    }
}

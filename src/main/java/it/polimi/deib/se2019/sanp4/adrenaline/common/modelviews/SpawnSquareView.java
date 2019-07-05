package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;

import java.util.ArrayList;
import java.util.List;

/**
 * A lightweight representation of a spawn square in the view.
 *
 * @author Tiziano Fucci, Lorenzo Farinelli
 */
public class SpawnSquareView extends SquareView {

    private static final long serialVersionUID = 157597379039936760L;

    /**
     * List of the weapons contained in the square
     */
    private List<String> weapons;

    /**
     * Creates a spawn square view
     *
     * @param location  the coordinates of the spawn square
     * @param roomColor the color of the room the square is in
     */
    @JsonCreator
    public SpawnSquareView(
            @JsonProperty("location") CoordPair location,
            @JsonProperty("roomColor") RoomColor roomColor) {
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

    @Override
    public String printSquareContent() {
        // We return a string that tells how many weapons are in the square
        return String.format("%d wpns", weapons.size());
    }

    /**
     * Retrieves the weapons contained in the square
     *
     * @return The list of ids of the weapons
     */
    public List<String> getWeapons() {
        return new ArrayList<>(weapons);
    }

    /**
     * Sets the weapons contained in the square
     *
     * @param weapons The list of ids of the weapons
     */
    public void setWeapons(List<String> weapons) {
        if (weapons != null && !weapons.contains(null)) {
            this.weapons.clear();
            this.weapons.addAll(weapons);
        }
    }
}

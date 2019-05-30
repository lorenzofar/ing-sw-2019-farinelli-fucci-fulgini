package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.*;

import java.io.Serializable;
import java.util.*;

/**
 * A lightweight representation of a square in the view
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SpawnSquareView.class, name = "SPAWN_SQUARE_VIEW"),
        @JsonSubTypes.Type(value = AmmoSquareView.class, name = "AMMO_SQUARE_VIEW"),
})
public abstract class SquareView implements Serializable {

    private static final long serialVersionUID = 1076256883407049643L;

    /**
     * The location of the square in cartesian coordinates
     */
    private CoordPair location;

    /**
     * Set of players inside the square
     */
    private Set<String> players;
    /**
     * Color of the room the square is inside
     */
    private RoomColor roomColor;
    /**
     * Adjacency map of the connected squares
     */
    private Map<CardinalDirection, SquareConnectionType> adjacentMap;

    /**
     * Private constructor to be used only by Jackson.
     */
    @JsonCreator
    SquareView() {}

    public SquareView(CoordPair location, RoomColor roomColor) {
        this.location = location;
        this.roomColor = roomColor;
        this.players = new HashSet<>();
        this.adjacentMap = new EnumMap<>(CardinalDirection.class);
    }

    /**
     * Retrieve the location of the square in cartesian coordinates
     *
     * @return The object representing the location
     */
    public CoordPair getLocation() {
        return location;
    }


    /**
     * Retrieves the players inside the square
     *
     * @return The set of players' username
     */
    public Set<String> getPlayers() {
        return players;
    }

    /**
     * Sets the players inside the square
     * If a null object is provided, nothing happens
     *
     * @param players The list of names of the players
     */
    public void setPlayers(Set<String> players) {
        this.players = players;
    }

    /**
     * Add a player inside the square
     * If the provided player is null, nothing happens
     *
     * @param player The object representing the player
     */
    public void addPlayer(String player) {
        if (player != null) {
            players.add(player);
        }
    }

    /**
     * Remove a player from the square
     * If the provided player is null or not present, nothing happens
     *
     * @param player The username of the player
     */
    public void removePlayer(String player) {
        if (player != null) {
            Optional<String> playerToRemove = players.stream().filter(p -> p.equals(player)).findFirst();
            playerToRemove.ifPresent(playerView -> players.remove(playerView));
        }
    }

    /**
     * Retrieves the color of the room the square is inside
     *
     * @return The object representing the color
     */
    public RoomColor getRoomColor() {
        return roomColor;
    }

    /**
     * Retrieves the marker indicating the type of the square
     * Each subclass of SquareView will print a different marker
     *
     * @return The string representing the marker
     */
    public abstract String printTypeMarker();

    /**
     * Retrieves the map of adjacency of the square
     *
     * @return The object representing the map
     */
    public Map<CardinalDirection, SquareConnectionType> getAdjacentMap() {
        return adjacentMap;
    }

    /**
     * Sets the map of adjacency of the square
     * If the provided map is null, nothing happens
     *
     * @param adjacentMap The object representing the map
     */
    public void setAdjacentMap(Map<CardinalDirection, SquareConnectionType> adjacentMap) {
        if (adjacentMap != null) {
            this.adjacentMap = adjacentMap;
        }
    }
}

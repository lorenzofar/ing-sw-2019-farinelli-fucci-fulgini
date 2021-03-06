package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CardinalDirection;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.SquareConnectionType;

import java.io.Serializable;
import java.util.*;

/**
 * A lightweight representation of a square in the view
 *
 * @author Tiziano Fucci, Lorenzo Farinelli
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
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
     * Creates a new square view
     *
     * @param location  the coordinates of the square
     * @param roomColor the color of the room the square is in
     */
    public SquareView(
            @JsonProperty("location") CoordPair location,
            @JsonProperty("roomColor") RoomColor roomColor) {
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
     * Retrieves a textual representation of the content inside the square
     *
     * @return The string representing the content
     */
    public abstract String printSquareContent();

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

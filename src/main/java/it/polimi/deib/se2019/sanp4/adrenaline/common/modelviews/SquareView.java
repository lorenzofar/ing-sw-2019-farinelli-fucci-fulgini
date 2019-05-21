package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CardinalDirection;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.SquareConnectionType;

import java.util.*;

/**
 * A lightweight representation of a square in the view
 */
public abstract class SquareView {

    /**
     * The location of the square in cartesian coordinates
     */
    private CoordPair location;

    /**
     * Set of players inside the square
     */
    private Set<PlayerView> players;
    /**
     * Color of the room the square is inside
     */
    private RoomColor roomColor;
    /**
     * Adjacency map of the connected squares
     */
    private Map<CardinalDirection, SquareConnectionType> adjacentMap;

    public SquareView(CoordPair location, RoomColor roomColor) {
        //TODO: Check null color and location
        this.location = location;
        this.roomColor = roomColor;
        this.players = new HashSet<>();
        this.adjacentMap = new EnumMap<>(CardinalDirection.class);
        //TODO: Check creation of adjacent map
    }

    /**
     * Retrieve the location of the square in cartesian coordinates
     * @return The object representing the location
     */
    private CoordPair getLocation(){
        return location;
    }


    /**
     * Retrieves the players inside the square
     *
     * @return The set of players' username
     */
    public Set<PlayerView> getPlayers() {
        return players;
    }

    /**
     * Add a player inside the square
     * If the provided player is null, nothing happens
     *
     * @param player The object representing the player
     */
    public void addPlayer(PlayerView player) {
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
            Optional<PlayerView> playerToRemove = players.stream().filter(playerView -> playerView.getName().equals(player)).findFirst();
            playerToRemove.ifPresent(playerView -> players.remove(playerView));
        }
    }

    /**
     * Remove a player from the square
     * If the provided player is null or not present, nothing happens
     *
     * @param player The object representing the player
     */
    public void removePlayer(PlayerView player) {
        if (player != null) {
            players.remove(player);
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
     * @return The string representing the marker
     */
    public abstract String getTypeMarker();

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

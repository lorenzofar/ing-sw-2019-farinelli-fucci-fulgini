package it.polimi.deib.se2019.sanp4.adrenaline.client.modelviews;

import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CardinalDirection;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.SquareConnection;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A lightweight representation of a square in the view
 */
public class SquareView {
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
    private Map<CardinalDirection, SquareConnection> adjacentMap;

    public SquareView(RoomColor roomColor) {
        //TODO: Check null color
        this.roomColor = roomColor;
        this.players = new HashSet<>();
        this.adjacentMap = new EnumMap<>(CardinalDirection.class);
        //TODO: Check creation of adjacent map
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
     * Add a player inside the square
     *
     * @param player The username of the player
     */
    public void addPlayer(String player) {
        if (player != null) {
            players.add(player);
        }
    }

    /**
     * Remove a player from the square
     *
     * @param player The username of the player
     */
    public void removePlayer(String player) {
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
     * Retrieves the map of adjacency of the square
     *
     * @return The object representing the map
     */
    public Map<CardinalDirection, SquareConnection> getAdjacentMap() {
        return adjacentMap;
    }
}

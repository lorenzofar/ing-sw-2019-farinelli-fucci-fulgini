package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.PlayerNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.*;

/**
 * A class representing a square of the game board
 */
public abstract class Square {

    /** The location of the square in cartesian coordinates */
    private CoordPair location;

    /** A map of all the squares it is adjacent to */
    private AdjacentMap adjacentSquares;

    /** A list of players inside the square */
    private Set<Player> players;

    /** The room the square is into */
    private Room room;

    /** Default constructor, only to be used by Jackson */
    protected Square(){}

    /**
     * Creates a new square at the specified location with no players inside
     * @param location The cartesian coordinates of the location
     */
    Square(CoordPair location){
        if(location == null){
            throw new NullPointerException("Location cannot be null");
        }
        this.location = location;
        this.adjacentSquares = new AdjacentMap();
        this.players = new HashSet<>(5);
        //TODO: Complete the constructor
    }

    /**
     * Puts a player inside the square
     * @param player The object representing the player, not null
     */
    public void addPlayer(Player player){
        if(player == null){
            throw new NullPointerException("Player cannot be null");
        }
        this.players.add(player);
    }

    /**
     * Removes a player from the square
     * @param player The object representing the player, not null
     * @throws PlayerNotFoundException If the player is not present in the square
     */
    public void removePlayer(Player player) throws PlayerNotFoundException {
        if(player == null){
            throw new NullPointerException("Player cannot be null");
        }
        if(!players.contains(player)){
            throw new PlayerNotFoundException("The player is not inside the square");
        }
        this.players.remove(player);
    }

    /**
     * Retrieves all the players that are currently inside the square
     * @return A list of objects representing the players
     */
    public List<Player> getPlayers(){
        return new ArrayList<>(this.players);
    }

    /**
     * Sets the room for this square.
     * @param room the room this square belongs to
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * Retrieves the room the square is inside
     * @return The object representing the room
     */
    public Room getRoom(){
        return this.room;
    }

    /**
     * Retrieves the squares the square is connected to
     * @return The object representing the connections mapping
     */
    public AdjacentMap getAdjacentSquares() {
        return adjacentSquares;
    }

    /**
     * Retrieves the location of the square
     * @return The cartesian coordinates of the location
     */
    public CoordPair getLocation() {
        return location;
    }
}

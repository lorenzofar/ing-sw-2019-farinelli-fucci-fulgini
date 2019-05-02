package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * A class representing a room of the game board.
 * Each room is characterized by a unique color and is composed of squares
 */
public class Room {

    /** The color of the room */
    private RoomColor color;

    /** A collection of distinct squares belonging to the room */
    private Collection<Square> squares;

    /** Default constructor only to be used by Jackson */
    protected Room(){}

    /**
     * Creates a new room of the specified color containing the provided squares
     * @param color The color of this room
     */
    Room(RoomColor color){
        if(color == null){
            throw new NullPointerException("Found null parameters");
        }
        this.squares = new HashSet<>();
        this.color = color;
    }

    /**
     * Adds a square to this room. Also sets the room attribute of the square.
     * @param square square to be added, not null
     */
    public void addSquare(Square square){
        if (square == null) throw new NullPointerException("Cannot add a null squuare to the room");
        squares.add(square);
        square.setRoom(this);
    }

    /**
     * Removes given square from the room. If the square does not belong to the room nothing happens.
     * @param square the square to be removed, not null
     */
    public void removeSquare(Square square){
        if(square == null){
            throw new NullPointerException("Square cannot be null");
        }
        if(squares.contains(square)) {
            squares.remove(square);
            square.setRoom(null);
        }

    }

    /**
     * Returns the squares in this room.
     * @return unmodifiable collection of squares in this room
     */
    public Collection<Square> getSquares() {
        return Collections.unmodifiableCollection(squares);
    }

    /**
     * Retrieves all the players inside the room
     * @return A collection of objects representing the players
     */
    public Collection<Player> getPlayers(){
        // Remap the list of squares to the players they contain and then flatten it
        return squares.stream().map(Square::getPlayers).flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * Retrieves the color of the room
     * @return The color of the room
     */
    public RoomColor getColor(){
        return this.color;
    }
}

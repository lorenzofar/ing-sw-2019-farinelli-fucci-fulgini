package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a room of the game board.
 * Each room is characterized by a unique color and is composed of squares
 */
public class Room {

    /** The color of the room */
    private RoomColor color;

    /** The list of squares belonging to the room */
    private List<Square> squares;

    /**
     * Creates a new room of the specified color containing the provided squares
     * @param squares The list of objects representing the squares
     */
    Room(List<Square> squares, RoomColor color){
        if(squares == null || color == null){
            throw new NullPointerException("Found null parameters");
        }
        this.squares = squares;
        this.color = color;
    }

    /**
     * Retrieves all the players inside the room
     * @return The list of objects representing the players
     */
    public List<Player> getPlayers(){
        List<Player> players = new ArrayList<>();
        this.squares.forEach(square ->
            //TODO: Check list assignment
            players.addAll(square.getPlayers())
        );
        return players;
    }

    /**
     * Retrieves the color of the room
     * @return The color of the room
     */
    public RoomColor getColor(){
        return this.color;
    }
}

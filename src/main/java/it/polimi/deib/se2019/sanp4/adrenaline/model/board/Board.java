package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/* A class representing the board game */
public class Board {

    /** A list of objects representing the squares the board is composed of */
    private List<Square> squares;

    /**
     * Creates a new game board containing the specified squares
     * @param squares A collection of objects representing the squares
     */
    Board(Collection<Square> squares){
        this.squares = new ArrayList<>(squares);
    }

    /**
     * Determines if a square can be reached from another one with at most the provided number of moves
     * @param start The object representing the initial square
     * @param end The object representing the final square
     * @param maxMoves The upper bound of allowed moves
     * @return {@code true} if the final square is reachable, {@code false} otherwise
     */
    private boolean isReachable(Square start, Square end, int maxMoves){
        //TODO: Implement this method
        return false;
    }

    /**
     * Retrieves the player that are visible for the provided player
     * @param player The object representing the player
     * @return A list of objects representing the visible players
     */
    public List<Player> getVisiblePlayers(Player player){
        //TODO: Implement this method
        return Collections.emptyList();
    }

    /**
     * Retrieves the players that are not visible for the provided player
     * @param player The object representing the player
     * @return A list of objects representing the non-visible players
     */
    public List<Player> getNotVisiblePlayers(Player player){
        //TODO: Implement this method
        return Collections.emptyList();
    }

    /**
     * Retrieves the squares that can be reached from the provided square with a number of moves between two bounds:
     * @param square The object representing the starting square
     * @param maxMoves The minimum allowed number of moves
     * @param minMoves The maximum allowed number of moves
     * @return A list of objects representing the reachable squares
     */
    public List<Square> getReachableSquares(CoordPair square, int maxMoves, int minMoves){
        //TODO: Implement this method
        return Collections.emptyList();
    }

    /**
     * Retrieves the squares that are placed in front of the provided square when facing a certain cardinal direction,
     * whose distance is contained between two bounds
     * @param square The object representing the squre
     * @param direction The cardinal direction
     * @param minMoves The minimum allowed number of moves
     * @param maxMoves The maximum allowed number of moves
     * @return A list of objects representing the squares
     */
    public List<Square> getAheadSquares(CoordPair square, CardinalDirection direction, int minMoves, int maxMoves){
        //TODO: Implement this method
        return Collections.emptyList();
    }

    /**
     * Retrieves the square composing a path between two provided squares
     * @param start The object representing the starting square
     * @param end The object representing the destination square
     * @return A list of objects representing the squares composing the path
     */
    public List<Square> getPath(Square start, Square end){
        //TODO: Implement this method
        return Collections.emptyList();
    }

    /**
     * Retrieves the square located at the provided location
     * @param location The cartesian coordinates of the location
     * @return The object representing the square
     */
    public Square getSquare(CoordPair location){
        //TODO: Implement this method
        return null;
    }

    /**
     * Retrieves the squares composing the board
     * @return The list of objects representing the squares
     */
    public List<Square> getSquares() {
        return new ArrayList<>(this.squares);
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.*;

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
     * @param start The object representing the initial square, not null
     * @param end The object representing the final square, not null
     * @param maxMoves The upper bound of allowed moves, must be positive
     * @return {@code true} if the final square is reachable, {@code false} otherwise
     */
    private boolean isReachable(Square start, Square end, int maxMoves){
        if(start == null || end == null){
            throw new NullPointerException("Squares cannot be null");
        }
        if(maxMoves < 0){
            throw new IllegalArgumentException("Square cannot be reached by a negative amount of moves");
        }
        //TODO: Implement this method
        return false;
    }

    /**
     * Given a starting square, this method navigates the board and returns the set of visited squares
     * matching the specified filters
     * @param start the square from which start, not null
     * @param visibility determines on which condition you can move from a square to its adjacent
     * @param direction the visit will proceed only in this direction, optional
     * @param minDist the minimum amount of moves from the starting square
     * @param maxDist the maximum amount of moves from the starting square
     * @return the set of visited squares matching the specified filters
     * @throws IllegalArgumentException if distances are negative or minDist > maxDist
     * @throws NullPointerException if start or visibility are null
     */
    public Set<Square> getScopedSquares (CoordPair start, VisibilityEnum visibility, CardinalDirection direction,
                                         Integer minDist, Integer maxDist) {
        if (start == null || visibility == null) {
            throw new NullPointerException("start and visibility must be not null");
        }
        if ((minDist != null && minDist < 0) || (maxDist != null && maxDist < 0) ||
                (minDist != null && maxDist != null && minDist > maxDist)) {
            throw new IllegalArgumentException();
        }
        //TODO: implement this method

        return null;
    }

    /**
     * Retrieves the square composing a path between two provided squares
     * @param start The object representing the starting square
     * @param end The object representing the destination square
     * @return A list of objects representing the squares composing the path
     */
    public List<Square> getPath(CoordPair start, CoordPair end){
        if(start == null || end == null){
            throw new NullPointerException("Squares cannot be null");
        }
        //TODO: Implement this method
        return Collections.emptyList();
    }

    /**
     * Retrieves the square located at the provided location
     * @param location The cartesian coordinates of the location
     * @return The object representing the square
     */
    public Square getSquare(CoordPair location){
        if(location == null){
            throw new NullPointerException("Location cannot be null");
        }
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

package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* A class representing the board game */
public class Board {

    /** A matrix of objects representing the squares the board is composed of. The addressing is [x][y] */
    private Square[][] squares;

    private Map<RoomColor, Room> rooms;

    /** Default constructor only to be used by Jackson */
    protected Board(){}

    /**
     * Creates an empty game board of given size.
     * @param xSize horizontal size (number of columns)
     * @param ySize vertical size (number of rows)
     */
    public Board(int xSize, int ySize){
        if (xSize <= 0 || ySize <=0) throw new IllegalArgumentException("Board size must be greater than 0");

        /* Allocate the matrix */
        squares = new Square[xSize][ySize];

        /* Create the empty rooms */
        rooms = new EnumMap<>(RoomColor.class);
        rooms.keySet().forEach(color -> rooms.put(color, new Room(color)));
    }

    /**
     * Adds given square to the board.
     * @param square the square to be added, not null
     */
    public void addSquare(Square square){
        if (square == null) throw new NullPointerException("Cannot add null square to the board");
        int x = square.getLocation().getX();
        int y = square.getLocation().getY();
        /* Check that the square is inside the board */
        if (x >= squares.length || y >= squares[0].length){
            throw new IndexOutOfBoundsException(
                    String.format("Cannot add a square at coords (%d, %d) because it's outside the board", x, y));
        }
        /* Add it to the board */
        squares[x][y] = square;
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
        if(maxMoves == 0){
            // A 0 amount of moves we have to stay in the current square,
            // hence the destination can be reached only if it the starting point
            return start.equals(end);
        }

        // We initialize the list of navigable squares with those I can reach with 1 move from the start
        Set<CoordPair> navigableSquares = new LinkedHashSet<>(start.getAdjacentSquares().getReachableSquares());

        int startIndex = 0;

        // We iterate over the maximum number of moves
        // At each step we retrieve all the squares we can move into, by
        for (int i = 1; i<maxMoves; i++){
            // First we get all the squares we can navigate from the currently considered squares
            // We just consider the newly added squares, by taking a subset starting at 'startIndex'
            Stream<CoordPair> navigableSquaresToAdd = navigableSquares
                    .stream()
                    .skip(startIndex)
                    .flatMap(location->
                            getSquare(location)
                                    .getAdjacentSquares()
                                    .getReachableSquares()
                                    .stream()
                                    // Then we remove the start square from the collection
                                    .filter(square -> square != start.getLocation()));
            // We update the startIndex to point at the last element of the list (before insertion)
            startIndex = navigableSquares.size();
            // And eventually we put all the new squares inside the list
            navigableSquaresToAdd.forEach(navigableSquares::add);
            // We check whether we have found the destination and if yes we stop and return
            if(navigableSquares.stream().anyMatch(coordPair -> coordPair.equals(end.getLocation()))){
                return true;
            }
        }
        // At the end we check whether we can reach the destination and we return accordingly
        return navigableSquares.stream().anyMatch(coordPair -> coordPair.equals(end.getLocation()));
    }

    /**
     * Retrieve all the squares that are visible from the provided squares, according to the game rules
     * @param start The object representing the square
     * @return The set of objects representing the visible squares
     */
    private Set<Square> getVisibleSquares(Square start){
        // Here we have to determine which squares are visible from the provided square
        // First we add all the squares that are in its same room
        Set<Square> visibleSquares = new HashSet<>(start.getRoom().getSquares());
        // Then we get all the squares it is connected by a door and retrieve the visible rooms
        start.getAdjacentSquares().values()
                .stream()
                // Keep only the squares connected by a door
                .filter(squareConnection -> squareConnection.getConnectionType() == SquareConnectionType.DOOR)
                // Get their coordinates
                .map(SquareConnection::getSquare)
                // Retrieve the square objects
                .map(this::getSquare)
                // Retrieve their rooms
                .map(Square::getRoom)
                // Retrieve all the squares belonging to the room and add them to the set
                .forEach(room -> visibleSquares.addAll(room.getSquares()));
        return visibleSquares;
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

        return squares[location.getX()][location.getY()];
    }

    /**
     * Retrieves the squares composing the board
     * @return A collection of objects representing the squares
     */
    public Collection<Square> getSquares() {
        return Arrays.stream(squares).flatMap(Arrays::stream).collect(Collectors.toList());
    }

    /**
     * Returns the rooms composing the board
     * @return An unmodifiable map of the rooms in this board
     */
    public Map<RoomColor, Room> getRooms() {
        return Collections.unmodifiableMap(rooms);
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.PlayerMoveUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class represents the game board.
 * It holds the squares and the rooms in which the squares are organised.
 * By convention the square with coordinate (0,0) is in the upper-left corner; square (1,0) is immediately at its right,
 * while square (0,1) is immediately beneath it.
 */
public class Board extends Observable<ModelUpdate> {

    /** A matrix of objects representing the squares the board is composed of. The addressing is [x][y] */
    private Square[][] squares;

    private Map<RoomColor, Room> rooms;

    /** Maps each color in the powerups to its spawn point */
    private Map<AmmoCube, SpawnSquare> spawnPoints;

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
        for (RoomColor color : RoomColor.values()) {
            rooms.put(color, new Room(color));
        }

        /* Create empty map of spawn points */
        spawnPoints = new EnumMap<>(AmmoCube.class);
    }

    /**
     * Adds given square to the board.
     * If there is already a square on the same point, the square is replaced and adjacencies are recomputed
     * @param square the square to be added, not null
     */
    void addSquare(Square square){
        if (square == null) throw new NullPointerException("Cannot add null square to the board");
        int x = square.getLocation().getX();
        int y = square.getLocation().getY();
        /* Check that the square is inside the board */
        if (x >= squares.length || y >= squares[0].length){
            throw new IndexOutOfBoundsException(
                    String.format("Cannot add a square at coords (%d, %d) because it's outside the board", x, y));
        }

        /* Eventually we put it into the board matrix */
        squares[x][y] = square;

    }

    /**
     * Moves a player from its current square to the provided one.
     * @param player The player to be moved
     * @param end The square where the player is moved
     */
    public void movePlayer(Player player, Square end) {
        Square start = player.getCurrentSquare();
        if (start != null) {
            start.removePlayer(player);
        }
        end.addPlayer(player);
        player.setCurrentSquare(end);
        if(start != null) {
            player.notifyObservers(new PlayerMoveUpdate(player.getName(), start.getLocation(), end.getLocation()));
        } else {
            player.notifyObservers(new PlayerMoveUpdate(player.getName(), null, end.getLocation()));
        }
    }

    /**
     * Retrieve all the squares that are visible from the provided squares, according to the game rules
     * @param start The object representing the square
     * @return The set of objects representing the visible squares
     */
    public Set<Square> getVisibleSquares(Square start){
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
     * Performs a width-first recursive visit of the neighbouring squares starting from {@code start},
     * with the possibility of setting a constrained direction, the type of navigable connections
     * and the maximum number of steps.
     * The visit stops when the maximum number of steps is reached or when the visitable neighbors
     * are already in {@code alreadyVisited}
     * @param start The square from which to start the navigation, not null
     * @param alreadyVisited The set of already visited squares, not null, will be modified
     * @param directionFilter A predicate which constraints the visit to a certain direction, not null
     * @param connectionFilter A predicate which filters {@link SquareConnectionType}, this will be applied
     *                         to determine which adjacent squares can be visited
     * @param maxSteps the maximum number of steps after which the visit ends
     *                 (if 0 will only add start, if negative it will not add nothing), optional
     * @return A set with the visited squares, including the ones already visited
     * @throws NullPointerException If start, alreadyVisited or connectionFilter are null
     * @throws IllegalArgumentException If maxSteps is negative
     */
    public Set<Square> visitNeighbors(Square start,
                                      Set<Square> alreadyVisited,
                                      Predicate<Map.Entry<CardinalDirection, SquareConnection>> directionFilter,
                                      Predicate<SquareConnection> connectionFilter,
                                      Integer maxSteps) {
        /* Check input parameters */
        if (start == null || alreadyVisited == null || directionFilter == null || connectionFilter == null) {
            throw new NullPointerException("Found null parameters");
        }

        /* Add this square */
        if (maxSteps == null || maxSteps >= 0) {
            alreadyVisited.add(start);
        }

        /* Now determine the visitable neighbours */
        Set<Square> toVisit = start.getAdjacentSquares().entrySet().stream()
                /* Filter by direction */
                .filter(directionFilter)
                /* Retrieve the square connections */
                .map(Map.Entry::getValue)
                /* Filter by connection type */
                .filter(connectionFilter)
                /* Map to coordinates */
                .map(SquareConnection::getSquare)
                /* Map coordinates to squares */
                .map(this::getSquare)
                /* Strip out the already visited squares */
                .filter(square -> !alreadyVisited.contains(square))
                .collect(Collectors.toSet());

        /* Proceed with visit */
        if (!toVisit.isEmpty() && (maxSteps == null || maxSteps > 0)) {
            for (Square square : toVisit) {
                /* Visit each one of the neighbours and decrement maxSteps */
                alreadyVisited.addAll(visitNeighbors(
                        square,alreadyVisited,
                        directionFilter, connectionFilter,
                        maxSteps == null ? null : maxSteps - 1));
                /* The newly visited squares are added to alreadyVisited */
            }
        }

        return alreadyVisited;
    }

    /**
     * Given a starting square, this method navigates the board and returns the set of visited squares
     * matching the specified filters
     * @param start the square from which start, not null
     * @param visibility determines on which condition you can move from a square to its adjacent
     * @param direction the visit will proceed only in this direction, optional
     * @param minDist the minimum amount of moves from the starting square, optional
     * @param maxDist the maximum amount of moves from the starting square, optional
     * @return the set of visited squares matching the specified filters
     * @throws IllegalArgumentException if distances are negative or minDist &gt; maxDist
     * @throws NullPointerException if start or visibility are null
     */
    public Set<Square> querySquares(Square start, VisibilityEnum visibility, CardinalDirection direction,
                                    Integer minDist, Integer maxDist) {
        if (start == null || visibility == null) {
            throw new NullPointerException("Start and visibility must be non-null");
        }
        /* Distance parameters are checked by the visiting function */

        /* Retrieve the squares visible according to the visibility modifier */
        Stream<Square> queried = visibility.squareGenerator.apply(start, this);

        /* Apply distance and direction modifiers if needed */
        if (direction != null || minDist != null || maxDist != null) {
            /* Generate direction filter */
            Predicate<Map.Entry<CardinalDirection, SquareConnection>> directionFilter;
            if (direction != null) {
                directionFilter = entry -> entry.getKey() == direction; /* Travel only in that direction */
            } else {
                directionFilter = entry -> true; /* All directions are ok */
            }

            /* Visit within minimum distance (their distance is < min. dist.-1) */
            /* Note that passing a negative parameter will cause toRemove to be empty */
            int limit = minDist == null ? -1 : minDist - 1;
            Set<Square> toRemove = visitNeighbors(start,
                    new HashSet<>(),
                    directionFilter,
                    visibility.connectionFilter,
                    limit);

            /* Then visit within max distance */
            Set<Square> toKeep = visitNeighbors(start,
                    new HashSet<>(),
                    directionFilter,
                    visibility.connectionFilter,
                    maxDist);

            /* Remove the squares within min distance */
            toKeep.removeAll(toRemove);

            /* Intersect with the squares queried for visibility */
            queried = queried.filter(toKeep::contains);
        }

        return queried.collect(Collectors.toSet());
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
        return getSquare(location.getX(), location.getY());
    }

    /**
     * Retrieves the square at the specified coordinates, if it exists
     * @param x x coordinate (horizontal, left-zero-based)
     * @param y y coordinate (vertical, top-zero-based)
     * @return the square at specified cordinates, null if it doesn't exist
     */
    public Square getSquare(int x, int y) {
        try {
            return squares[x][y];
        } catch (IndexOutOfBoundsException ignore) {
            return null;
        }
    }

    /**
     * Retrieves the squares composing the board
     * @return A collection of objects representing the squares
     */
    public Collection<Square> getSquares() {
        return Arrays.stream(squares).flatMap(Arrays::stream).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Returns the rooms composing the board
     * @return An unmodifiable map of the rooms in this board
     */
    public Map<RoomColor, Room> getRooms() {
        return Collections.unmodifiableMap(rooms);
    }

    /**
     * Returns a map with the spawn points
     * @return unmodifiable map of spawn points
     */
    public Map<AmmoCube, Square> getSpawnPoints() {
        return Collections.unmodifiableMap(spawnPoints);
    }

    /**
     * Set a square as the spawn point for a specific color
     * @param color color of the spawn point
     * @param square square where to spawn
     */
    void setSpawnPoint(AmmoCube color, SpawnSquare square) {
        spawnPoints.put(color, square);
    }
}

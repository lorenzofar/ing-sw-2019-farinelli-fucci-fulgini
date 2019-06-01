package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;

import java.io.Serializable;
import java.util.Map;

/**
 * A lightweight representation of the game board in the view
 */
public class BoardView implements Serializable {

    private static final long serialVersionUID = -444568262120667535L;
    /**
     * A matrix representing the disposal of squares in the board.
     */
    private SquareView[][] squares;

    /**
     * The unique identifier of this board configuration.
     */
    private final int id;
    /**
     * Maps each color in the powerups to its spawn point.
     */
    private Map<AmmoCube, CoordPair> spawnPoints;

    /**
     * Creates a new board view
     * @param id the board identifier
     * @param xSize the number of columns
     * @param ySize the number of rows
     */
    @JsonCreator
    public BoardView(
            @JsonProperty("id") int id,
            @JsonProperty("xSize") int xSize,
            @JsonProperty("ySize") int ySize) {
        this.id = id;
        this.squares = new SquareView[xSize][ySize];
    }

    /**
     * Retrieve the number of columns composing the board
     *
     * @return The number of columns
     */
    @JsonIgnore
    public int getRowsCount() {
        return squares[0].length;
    }

    /**
     * Retrieves the number of rows composing the board
     *
     * @return The number of rows
     */
    @JsonIgnore
    public int getColumnsCount() {
        return squares.length;
    }

    /**
     * Retrieves the squares composing the board
     *
     * @return The matrix representing the squares
     */
    public SquareView[][] getSquares() {
        return squares;
    }

    /**
     * Sets the squares composing the board
     *
     * @param squares The matrix representing the squares
     */
    public void setSquares(SquareView[][] squares) {
        if (squares != null) {
            this.squares = squares;
        }
    }

    /**
     * Retrieves the square in the specified location
     *
     * @param location The object representing the location
     * @return The object representing the square
     */
    public SquareView getSquare(CoordPair location) {
        if (location == null) {
            return null;
        }
        try {
            return squares[location.getX()][location.getY()];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Map<AmmoCube, CoordPair> getSpawnPoints() {
        return spawnPoints;
    }

    public void setSpawnPoints(Map<AmmoCube, CoordPair> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }

    public int getId() {
        return id;
    }
}

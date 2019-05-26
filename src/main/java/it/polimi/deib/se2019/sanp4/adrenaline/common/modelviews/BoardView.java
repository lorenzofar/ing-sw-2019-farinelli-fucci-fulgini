package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;

import java.io.Serializable;

/**
 * A lightweight representation of the game board in the view
 */
public class BoardView implements Serializable {

    private static final long serialVersionUID = -444568262120667535L;
    /**
     * A matrix representing the disposal of squares in the board
     */
    private SquareView[][] squares;

    /**
     * Private constructor to be used only by Jackson.
     */
    private BoardView() {}

    public BoardView(int xSize, int ySize) {
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
}

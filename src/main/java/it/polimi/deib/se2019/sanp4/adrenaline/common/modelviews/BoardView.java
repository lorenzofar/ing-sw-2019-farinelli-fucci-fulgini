package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;

/**
 * A lightweight representation of the game board in the view
 */
public class BoardView {

    /**
     * A matrix representing the disposal of squares in the board
     */
    private SquareView[][] squares;

    public BoardView() {
        this.squares = new SquareView[0][0];
        //TODO: Check how squares are added to the matrix
    }

    /**
     * Retrieve the number of columns composing the board
     *
     * @return The number of columns
     */
    public int getRowsCount() {
        return squares[0].length;
    }

    /**
     * Retrieves the number of rows composing the board
     *
     * @return The number of rows
     */
    public int getColumnsCount() {
        return squares.length;
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

package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import org.junit.Test;

import static org.junit.Assert.*;

public class BoardTest {

    @Test(expected = IllegalArgumentException.class)
    public void createBoard_NegativeValueProvided_ShouldThrowIllegalArgumentException(){
        Board board = new Board(-1, 0);
    }

    @Test
    public void createBoard_ShouldSucceed(){
        Board board = new Board(4,3);
    }

    @Test(expected = NullPointerException.class)
    public void addSquare_NullSquareProvided_ShouldThrowNullPointerException(){
        Board board = new Board(4,3);
        board.addSquare(null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addSquare_SquareOutsideBoardProvided_ShouldThrowNullPointerException(){
        Board board = new Board(4,3);
        CoordPair location = new CoordPair(5,3);
        board.addSquare(new AmmoSquare(location));
    }

    @Test
    public void addSquare_ValidSquareProvided_PreviousSquareExistent_ShouldSucceed(){
        Board board = new Board(4,3);
        CoordPair location = new CoordPair(3,2);
        board.addSquare(new AmmoSquare(location));
        board.addSquare(new AmmoSquare(location));
    }

    @Test(expected = NullPointerException.class)
    public void getPath_NullSquareProvided_ShouldThrowNullPointerException(){
        Board board = new Board(4,3);
        board.getPath(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void getSquare_NullSquareProvided_ShouldThrowNullPointerException(){
        Board board = new Board(4,3);
        board.getSquare(null);
    }

    @Test
    public void getSquare_ShouldReturnSameSquare(){
        Board board = new Board(4,3);
        CoordPair location = new CoordPair(3,2);
        Square square = new AmmoSquare(location);
        board.addSquare(square);
        assertEquals(square, board.getSquare(location));
        assertTrue(board.getSquares().contains(square));
    }

}
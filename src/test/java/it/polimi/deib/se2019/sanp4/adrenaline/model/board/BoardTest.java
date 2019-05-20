package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.BoardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class BoardTest {

    private static Board testBoard;
    @BeforeClass
    public static void setUp() throws BoardNotFoundException {
        /* Load schemas for validation */
        JSONUtils.loadBoardPackSchema("/schemas/board_pack.schema.json");
        JSONUtils.loadBoardSchema("/schemas/board.schema.json");
        BoardCreator.loadBoard("/assets/std_boards/board_1.json");
        testBoard = BoardCreator.createBoard(1);
    }


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

    @Test(expected = NullPointerException.class)
    public void getNavigableSquares_NullStartProvided_ShouldThrowNullPointerException(){
        Board board = new Board(4,3);
        board.getNavigableSquares(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNavigableSquares_NegativeMaxMoves_ShouldThrowIllegalArgumentException(){
        Board board = new Board(4,3);
        CoordPair start = new CoordPair(1,1);
        board.getNavigableSquares(start, -1);
    }

    @Test
    public void isReachable_EnoughMaxMoves() {
        CoordPair start = new CoordPair(0,0);
        CoordPair end = new CoordPair(3,2);
        assertTrue(testBoard.isReachable(start, end, 5));
    }

    @Test
    public void isReachable_SameSquareProvided() {
        CoordPair start = new CoordPair(0,0);
        CoordPair end = new CoordPair(3,2);
        assertTrue(testBoard.isReachable(start, start, 0));
    }

    @Test
    public void getVisibleSquares() {
        CoordPair start = new CoordPair(0,0);
        assertEquals(6, testBoard.getVisibleSquares(testBoard.getSquare(start)).size());
    }

    @Test
    public void getScopedSquares() {
        CoordPair start = new CoordPair(0,0);
        assertEquals(2, testBoard.getScopedSquares(start, VisibilityEnum.VISIBLE, CardinalDirection.E,
                                1,2).size());
        start = new CoordPair(0,1);
        assertEquals(3, testBoard.getScopedSquares(start, VisibilityEnum.VISIBLE, CardinalDirection.E,
                0,5).size());
    }

    @Test
    public void getSquares_shouldContainNoNullElements() {
        Collection<Square> squares = testBoard.getSquares();
        squares.forEach(Assert::assertNotNull);
    }
}
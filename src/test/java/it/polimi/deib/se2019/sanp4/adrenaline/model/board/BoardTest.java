package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.BoardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.BoardView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.JSONUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.CardinalDirection.*;
import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum.*;

public class BoardTest {

    private static Board testBoard;

    private static Predicate<Map.Entry<CardinalDirection, SquareConnection>> allDirections = entry -> true;

    private static Predicate<SquareConnection> walkable = sc -> sc.getConnectionType() != SquareConnectionType.WALL;

    private static Predicate<SquareConnection> ignoreWalls = sc -> true;

    private static Square existentSquare;

    /* Returns predicate to filter in direction */
    private static Predicate<Map.Entry<CardinalDirection, SquareConnection>> direction(CardinalDirection d) {
        return entry -> entry.getKey() == d;
    }

    @BeforeClass
    public static void setUp() throws BoardNotFoundException {
        /* Load schemas for validation */
        JSONUtils.loadBoardPackSchema("/schemas/board_pack.schema.json");
        JSONUtils.loadBoardSchema("/schemas/board.schema.json");
        BoardCreator.loadBoard("/assets/std_boards/board_1.json");
        testBoard = BoardCreator.createBoard(1);
        existentSquare = testBoard.getSquare(2, 1);
    }


    @Test
    public void createBoard_NegativeValueProvided_ShouldThrowIllegalArgumentException(){
        try {
            new Board(0, -1, 0);
            fail();
        } catch (IllegalArgumentException e) {
            /* OK */
        }

        try {
            new Board(0, 5, -1);
            fail();
        } catch (IllegalArgumentException e) {
            /* OK */
        }
    }

    @Test
    public void createBoard_ShouldSucceed(){
        Board board = new Board(0, 4,3);
        /* Check that the board is empty */
        assertTrue(board.getSquares().isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void addSquare_NullSquareProvided_ShouldThrowNullPointerException(){
        Board board = new Board(0, 4,3);
        board.addSquare(null);
    }

    @Test
    public void addSquare_SquareOutsideBoardProvided_ShouldThrowNullPointerException(){
        Board board = new Board(0, 4,3);

        try {
            board.addSquare(new AmmoSquare(new CoordPair(5,3)));
            fail();
        } catch (IndexOutOfBoundsException e) {
            /* OK */
        }
        try {
            board.addSquare(new AmmoSquare(new CoordPair(1,7)));
            fail();
        } catch (IndexOutOfBoundsException e) {
            /* OK */
        }
    }

    @Test
    public void addSquare_notAssigned_shouldAssign() {
        Board board = new Board(0, 4,3);

        Square newSquare = new AmmoSquare(new CoordPair(1,1));
        board.addSquare(newSquare);

        assertSame(newSquare, board.getSquare(newSquare.getLocation()));
    }

    @Test
    public void addSquare_previouslyAssigned_shouldSubstitute() {
        Board board = new Board(0, 4,3);
        board.addSquare(new AmmoSquare(new CoordPair(1,1)));

        Square newSquare = new AmmoSquare(new CoordPair(1,1));
        board.addSquare(newSquare);

        assertSame(newSquare, board.getSquare(newSquare.getLocation()));
    }

    @Test(expected = NullPointerException.class)
    public void getSquare_NullSquareProvided_ShouldThrowNullPointerException(){
        Board board = new Board(0, 4,3);
        board.getSquare(null);
    }

    @Test
    public void getSquare_ShouldReturnSameSquare(){
        Board board = new Board(0, 4,3);
        CoordPair location = new CoordPair(3,2);
        Square square = new AmmoSquare(location);
        board.addSquare(square);
        assertEquals(square, board.getSquare(location));
        assertTrue(board.getSquares().contains(square));
    }

    @Test(expected = NullPointerException.class)
    public void getSquare_nullLocation_shouldThrow() {
        testBoard.getSquare(null);
    }

    @Test
    public void getSquare_outOfBounds_shouldReturnNull() {
        assertNull(testBoard.getSquare(new CoordPair(10, 15)));
    }

    @Test
    public void getSquare_emptyPlace_shouldReturnNull() {
        assertNull(testBoard.getSquare(new CoordPair(3, 0)));
    }

    @Test
    public void getVisibleSquares() {
        CoordPair start = new CoordPair(2,1);
        Collection<Square> visible = testBoard.getVisibleSquares(testBoard.getSquare(start));

        Set<Square> expected = new HashSet<>();
        expected.add(testBoard.getSquare(0,0));
        expected.add(testBoard.getSquare(1,0));
        expected.add(testBoard.getSquare(2,0));
        expected.add(testBoard.getSquare(0,1));
        expected.add(testBoard.getSquare(1,1));
        expected.add(testBoard.getSquare(2,1));
        expected.add(testBoard.getSquare(3,1));
        expected.add(testBoard.getSquare(3,2));

        assertTrue(visible.containsAll(expected));
        assertTrue(expected.containsAll(visible));
    }

    @Test
    public void getSquares_shouldNotContainNullElements() {
        Collection<Square> squares = testBoard.getSquares();
        squares.forEach(Assert::assertNotNull);
    }

    @Test
    public void movePlayer_notSpawned_shouldSpawn() {
        Player player = ModelTestUtil.generatePlayer("name");
        Square end = testBoard.getSquare(1,1);

        testBoard.movePlayer(player, end);
        /* Check that the player is in the correct position */
        assertEquals(end, player.getCurrentSquare());

        /* Check that the square contains the player */
        assertTrue(end.getPlayers().contains(player));

        /* Check that no other square contains the player */
        testBoard.getSquares().forEach(square ->
                assertTrue(square == end || !square.getPlayers().contains(player)));

        /* Remove the player in order to revert side effects */
        end.removePlayer(player);
    }

    @Test
    public void movePlayer_alreadyInSquare_shouldSpawn() {
        Player player = ModelTestUtil.generatePlayer("name");

        /* Put the player in a square */
        Square start = testBoard.getSquare(0,0);
        start.addPlayer(player);
        player.setCurrentSquare(start);

        /* Move it to another square */
        Square end = testBoard.getSquare(1,1);
        testBoard.movePlayer(player, end);

        /* Check that the player is in the correct position */
        assertEquals(end, player.getCurrentSquare());

        /* Check that the square contains the player */
        assertTrue(end.getPlayers().contains(player));

        /* Check that no other square contains the player */
        testBoard.getSquares().forEach(square ->
                assertTrue(square == end || !square.getPlayers().contains(player)));
    }

    /* ====== VISIT METHOD ======== */

    @Test(expected = NullPointerException.class)
    public void visitNeighbors_nullStart_shouldThrow() {
        testBoard.visitNeighbors(null, new HashSet<>(), allDirections, walkable, 1);
    }

    @Test(expected = NullPointerException.class)
    public void visitNeighbors_nullAlreadyVisited_shouldThrow() {
        testBoard.visitNeighbors(existentSquare, null, allDirections, walkable, 1);
    }

    @Test(expected = NullPointerException.class)
    public void visitNeighbors_nullDirectionFilter_shouldThrow() {
        testBoard.visitNeighbors(existentSquare, new HashSet<>(), null, walkable, 1);
    }

    @Test(expected = NullPointerException.class)
    public void visitNeighbors_nullConnectionFilter_shouldThrow() {
        testBoard.visitNeighbors(existentSquare, new HashSet<>(), allDirections, null, 1);
    }

    @Test
    public void visitNeighbors_noDirectionConstraint_onlyWalkable() {
        Square start = testBoard.getSquare(2,1);
        Set<Square> visited = testBoard.visitNeighbors(start, new HashSet<>(), allDirections, walkable, 2);

        Set<Square> expected = new LinkedHashSet<>();
        expected.add(testBoard.getSquare(2,1));
        expected.add(testBoard.getSquare(1,0));
        expected.add(testBoard.getSquare(2,0));
        expected.add(testBoard.getSquare(1,1));
        expected.add(testBoard.getSquare(0,1));
        expected.add(testBoard.getSquare(1,2));
        expected.add(testBoard.getSquare(3,1));
        expected.add(testBoard.getSquare(3,2));

        assertTrue(visited.containsAll(expected));
        assertTrue(expected.containsAll(visited));
    }

    @Test
    public void visitNeighbors_noDirectionConstraint_onlyWalkable_shouldRetrieveAllBoard() {
        Square start = testBoard.getSquare(2,1);
        Set<Square> visited = testBoard.visitNeighbors(start, new HashSet<>(), allDirections, walkable, null);

        Set<Square> expected = new HashSet<>(testBoard.getSquares());

        assertTrue(visited.containsAll(expected));
        assertTrue(expected.containsAll(visited));
    }

    @Test
    public void visitNeighbors_directionConstraint_onlyWalkable() {
        Square start = testBoard.getSquare(2,0);

        Set<Square> visited = testBoard.visitNeighbors(start, new HashSet<>(), direction(W), walkable, null);

        Set<Square> expected = new LinkedHashSet<>();
        expected.add(testBoard.getSquare(0,0));
        expected.add(testBoard.getSquare(1,0));
        expected.add(testBoard.getSquare(2,0));

        assertTrue(visited.containsAll(expected));
        assertTrue(expected.containsAll(visited));
    }

    @Test
    public void visitNeighbors_directionConstraint_ignoreWalls_shouldPassInsideWall() {
        Square start = testBoard.getSquare(2,1);
        Set<Square> visited = testBoard.visitNeighbors(start, new HashSet<>(), direction(W), ignoreWalls, null);

        Set<Square> expected = new HashSet<>();
        expected.add(testBoard.getSquare(0,1));
        expected.add(testBoard.getSquare(1,1));
        expected.add(testBoard.getSquare(2,1));

        assertTrue(visited.containsAll(expected));
        assertTrue(expected.containsAll(visited));
    }

    @Test
    public void visitNeighbors_negativeMaxDist_shouldReturnEmptySet() {
        Set<Square> visited =
                testBoard.visitNeighbors(existentSquare, new HashSet<>(), allDirections, walkable, -5);

        assertTrue(visited.isEmpty());
    }

    /* ======== QUERY METHOD ========== */

    @Test(expected = NullPointerException.class)
    public void querySquares_nullStart_shouldThrow() {
        testBoard.querySquares(null, VISIBLE, N, 1, 2);
    }

    @Test(expected = NullPointerException.class)
    public void querySquares_nullVisibility_shouldThrow() {
        testBoard.querySquares(existentSquare, null, N, 1, 2);
    }

    @Test
    public void querySquares_visibleQuery_noConstraints_shouldReturnVisibleSquares() {
        Square start = testBoard.getSquare(2,1);
        Set<Square> visited = testBoard.querySquares(start, VISIBLE, null, null, null);

        Set<Square> expected = new HashSet<>();
        expected.add(testBoard.getSquare(0,0));
        expected.add(testBoard.getSquare(1,0));
        expected.add(testBoard.getSquare(2,0));
        expected.add(testBoard.getSquare(0,1));
        expected.add(testBoard.getSquare(1,1));
        expected.add(testBoard.getSquare(2,1));
        expected.add(testBoard.getSquare(3,1));
        expected.add(testBoard.getSquare(3,2));

        assertTrue(visited.containsAll(expected));
        assertTrue(expected.containsAll(visited));
    }

    @Test
    public void querySquares_notVisibleQuery_noConstraints_shouldReturnAllButVisible() {
        Square start = testBoard.getSquare(2,1);
        Set<Square> visited = testBoard.querySquares(start, NOT_VISIBLE, null, null, null);

        Set<Square> expected = new HashSet<>(testBoard.getSquares());
        expected.remove(testBoard.getSquare(0,0));
        expected.remove(testBoard.getSquare(1,0));
        expected.remove(testBoard.getSquare(2,0));
        expected.remove(testBoard.getSquare(0,1));
        expected.remove(testBoard.getSquare(1,1));
        expected.remove(testBoard.getSquare(2,1));
        expected.remove(testBoard.getSquare(3,1));
        expected.remove(testBoard.getSquare(3,2));

        assertTrue(visited.containsAll(expected));
        assertTrue(expected.containsAll(visited));
    }

    @Test
    public void querySquares_anyQuery_minDist_2_shouldNotRetrieveSurrounding() {
        Square start = testBoard.getSquare(2,1);
        Set<Square> visited = testBoard.querySquares(start, ANY, null, 2, null);

        Set<Square> expected = new HashSet<>(testBoard.getSquares());
        expected.remove(testBoard.getSquare(2,1));
        expected.remove(testBoard.getSquare(1,1));
        expected.remove(testBoard.getSquare(2,0));
        expected.remove(testBoard.getSquare(3,1));

        assertTrue(visited.containsAll(expected));
        assertTrue(expected.containsAll(visited));
    }

    @Test
    public void querySquares_anyQuery_maxDist_2() {
        Square start = testBoard.getSquare(2,0);
        Set<Square> visited = testBoard.querySquares(start, ANY, null,null, 2);

        Set<Square> expected = new HashSet<>();
        expected.add(testBoard.getSquare(0,0));
        expected.add(testBoard.getSquare(1,0));
        expected.add(testBoard.getSquare(2,0));
        expected.add(testBoard.getSquare(1,1));
        expected.add(testBoard.getSquare(2,1));
        expected.add(testBoard.getSquare(3,1));

        assertTrue(visited.containsAll(expected));
        assertTrue(expected.containsAll(visited));
    }

    @Test
    public void querySquares_ignoreWalls_shouldPassThroughWalls() {
        Square start = testBoard.getSquare(0,0);
        Set<Square> visited = testBoard.querySquares(start, IGNORE_WALLS, null,null, 2);

        Set<Square> expected = new HashSet<>();
        expected.add(testBoard.getSquare(0,0));
        expected.add(testBoard.getSquare(1,0));
        expected.add(testBoard.getSquare(2,0));
        expected.add(testBoard.getSquare(0,1));
        expected.add(testBoard.getSquare(1,1));

        assertTrue(visited.containsAll(expected));
        assertTrue(expected.containsAll(visited));
    }

    @Test
    public void querySquares_directional_shouldGoOnlyInOneDirection() {
        Square start = testBoard.getSquare(3,1);
        Set<Square> visited = testBoard.querySquares(start, ANY, S,null, null);

        Set<Square> expected = new HashSet<>();
        expected.add(testBoard.getSquare(3,1));
        expected.add(testBoard.getSquare(3,2));

        assertTrue(visited.containsAll(expected));
        assertTrue(expected.containsAll(visited));
    }

    @Test
    public void calculateAlignedDirection_sameLocation_shouldReturnNull() {
        CoordPair a = new CoordPair(0,0);
        CoordPair b = new CoordPair(0,0);

        assertNull(Board.calculateAlignedDirection(a,b));
    }

    @Test
    public void calculateAlignedDirection_notAligned_shouldReturnNull() {
        CoordPair a = new CoordPair(0,0);
        CoordPair b = new CoordPair(1,1);

        assertNull(Board.calculateAlignedDirection(a,b));
    }

    @Test
    public void calculateAlignedDirection_BtoTheNorth_shouldReturnN() {
        CoordPair a = new CoordPair(1,1);
        CoordPair b = new CoordPair(1,0);

        assertEquals(N, Board.calculateAlignedDirection(a,b));
    }

    @Test
    public void calculateAlignedDirection_BtoTheSouth_shouldReturnS() {
        CoordPair a = new CoordPair(1,0);
        CoordPair b = new CoordPair(1,1);

        assertEquals(S, Board.calculateAlignedDirection(a,b));
    }

    @Test
    public void calculateAlignedDirection_BtoTheWest_shouldReturnW() {
        CoordPair a = new CoordPair(1,1);
        CoordPair b = new CoordPair(2,1);

        assertEquals(E, Board.calculateAlignedDirection(a,b));
    }

    @Test
    public void calculateAlignedDirection_BtoTheEast_shouldReturnE() {
        CoordPair a = new CoordPair(1,1);
        CoordPair b = new CoordPair(0,1);

        assertEquals(W, Board.calculateAlignedDirection(a,b));
    }

    @Test
    public void generateView_ShouldSucceed() {
        BoardView boardView = testBoard.generateView();
        for (int x = 0; x < boardView.getColumnsCount(); x++) {
            for (int y = 0; y < boardView.getRowsCount(); y++) {
                if (boardView.getSquares()[x][y] != null) {
                    assertEquals(new CoordPair(x,y), boardView.getSquares()[x][y].getLocation());
                }
            }
        }
        assertEquals(boardView.getSpawnPoints().get(AmmoCube.RED), new CoordPair(0,1));
        assertEquals(boardView.getSpawnPoints().get(AmmoCube.BLUE), new CoordPair(2,0));
        assertEquals(boardView.getSpawnPoints().get(AmmoCube.YELLOW), new CoordPair(3,2));
    }
}
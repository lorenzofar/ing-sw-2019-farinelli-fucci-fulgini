package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.BoardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
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
}
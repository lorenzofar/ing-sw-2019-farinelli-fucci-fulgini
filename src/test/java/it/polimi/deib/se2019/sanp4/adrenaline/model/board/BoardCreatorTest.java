package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.BoardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoardCreatorTest {
    @BeforeClass
    public static void setUp() {
        /* Load schemas for validation */
        JSONUtils.loadBoardPackSchema("/schemas/board_pack.schema.json");
        JSONUtils.loadBoardSchema("/schemas/board.schema.json");
    }

    @After
    public void tearDown() {
        /* Bring it to its original state */
        BoardCreator.reset();
    }

    @Test
    public void loadBoard_validBoard_shouldSucceed() {
        /* Load board with id zero */
        BoardCreator.loadBoard("/assets/test_boards/board_valid.json");
        assertTrue(BoardCreator.isBoardAvailable(0));

        /* Also test reset */
        BoardCreator.reset();
        assertFalse(BoardCreator.isBoardAvailable(0));
    }

    @Test(expected = ValidationException.class)
    public void loadBoard_invalidSyntax_shouldThrow() {
        BoardCreator.loadBoard("/assets/test_boards/board_invalid_syntax.json");
    }

    @Test
    public void loadBoardPack_validPack_shouldSucceed() {
        BoardCreator.loadBoardPack("/assets/board_pack_valid.json");
        assertTrue(BoardCreator.isBoardAvailable(0));
    }

    @Test(expected = ValidationException.class)
    public void loadBoardPack_invalidPack_shouldThrow() {
        BoardCreator.loadBoardPack("/assets/board_pack_invalid.json");
    }

    @Test
    public void loadBoardPack_standardPack_shouldSucceed() throws BoardNotFoundException {
        BoardCreator.loadBoardPack("/assets/standard_boards.json");

        /* Check that all the boards have been loaded and try to create them */
        for (int i = 0; i < 4; i++) {
            assertTrue(BoardCreator.isBoardAvailable(i));
            assertNotNull(BoardCreator.createBoard(i));
        }
    }

    public void checkTestBoard(Board board){
        /* Verify the id */
        assertEquals(0, board.getId());
        /* Verifying that the (0,0) square respects our test board structure */
        CoordPair currSquare = new CoordPair(0,0);
        assertEquals(RoomColor.PURPLE, board.getSquare(currSquare).getRoom().getColor());
        AdjacentMap currAdjacent = board.getSquare(currSquare).getAdjacentSquares();
        assertNull(currAdjacent.get(CardinalDirection.N));
        assertNull(currAdjacent.get(CardinalDirection.W));
        assertNull(currAdjacent.get(CardinalDirection.S));
        assertEquals(SquareConnectionType.DOOR,  currAdjacent.get(CardinalDirection.E).getConnectionType());
        CoordPair connectedSquare = new CoordPair(1,0);
        assertEquals(board.getSquare(connectedSquare), board.getSquare(currAdjacent.getConnection(CardinalDirection.E).getSquare()));
        /* (1,0) square */
        currSquare = new CoordPair(1,0);
        assertEquals(RoomColor.GREEN, board.getSquare(currSquare).getRoom().getColor());
        currAdjacent = board.getSquare(currSquare).getAdjacentSquares();
        assertNull(currAdjacent.get(CardinalDirection.N));
        assertEquals(SquareConnectionType.DOOR, currAdjacent.get(CardinalDirection.W).getConnectionType());
        assertEquals(SquareConnectionType.DOOR, currAdjacent.get(CardinalDirection.S).getConnectionType());
        assertEquals(SquareConnectionType.DOOR, currAdjacent.get(CardinalDirection.E).getConnectionType());
        connectedSquare = new CoordPair(0,0);
        assertEquals(board.getSquare(connectedSquare), board.getSquare(currAdjacent.getConnection(CardinalDirection.W).getSquare()));
        connectedSquare = new CoordPair(2,0);
        assertEquals(board.getSquare(connectedSquare), board.getSquare(currAdjacent.getConnection(CardinalDirection.E).getSquare()));
        connectedSquare = new CoordPair(1,1);
        assertEquals(board.getSquare(connectedSquare), board.getSquare(currAdjacent.getConnection(CardinalDirection.S).getSquare()));
        /* (2,0) square */
        currSquare = new CoordPair(2,0);
        assertEquals(RoomColor.BLUE, board.getSquare(currSquare).getRoom().getColor());
        currAdjacent = board.getSquare(currSquare).getAdjacentSquares();
        assertNull(currAdjacent.get(CardinalDirection.N));
        assertEquals(SquareConnectionType.DOOR, currAdjacent.get(CardinalDirection.W).getConnectionType());
        assertEquals(SquareConnectionType.FLOOR, currAdjacent.get(CardinalDirection.S).getConnectionType());
        assertNull(currAdjacent.get(CardinalDirection.E));
        connectedSquare = new CoordPair(1,0);
        assertEquals(board.getSquare(connectedSquare), board.getSquare(currAdjacent.getConnection(CardinalDirection.W).getSquare()));
        connectedSquare = new CoordPair(2,1);
        assertEquals(board.getSquare(connectedSquare), board.getSquare(currAdjacent.getConnection(CardinalDirection.S).getSquare()));
        /* (1,1) square */
        currSquare = new CoordPair(1,1);
        assertEquals(RoomColor.YELLOW, board.getSquare(currSquare).getRoom().getColor());
        currAdjacent = board.getSquare(currSquare).getAdjacentSquares();
        assertEquals(SquareConnectionType.DOOR, currAdjacent.get(CardinalDirection.N).getConnectionType());
        assertNull(currAdjacent.get(CardinalDirection.W));
        assertEquals(SquareConnectionType.WALL, currAdjacent.get(CardinalDirection.E).getConnectionType());
        assertNull(currAdjacent.get(CardinalDirection.S));
        connectedSquare = new CoordPair(1,0);
        assertEquals(board.getSquare(connectedSquare), board.getSquare(currAdjacent.getConnection(CardinalDirection.N).getSquare()));
        connectedSquare = new CoordPair(2,1);
        assertEquals(board.getSquare(connectedSquare), board.getSquare(currAdjacent.getConnection(CardinalDirection.E).getSquare()));
        /* (2,1) square */
        currSquare = new CoordPair(2,1);
        assertEquals(RoomColor.BLUE, board.getSquare(currSquare).getRoom().getColor());
        currAdjacent = board.getSquare(currSquare).getAdjacentSquares();
        assertEquals(SquareConnectionType.FLOOR, currAdjacent.get(CardinalDirection.N).getConnectionType());
        assertNull(currAdjacent.get(CardinalDirection.E));
        assertEquals(SquareConnectionType.WALL, currAdjacent.get(CardinalDirection.W).getConnectionType());
        assertNull(currAdjacent.get(CardinalDirection.S));
        connectedSquare = new CoordPair(2,0);
        assertEquals(board.getSquare(connectedSquare), board.getSquare(currAdjacent.getConnection(CardinalDirection.N).getSquare()));
        connectedSquare = new CoordPair(1,1);
        assertEquals(board.getSquare(connectedSquare), board.getSquare(currAdjacent.getConnection(CardinalDirection.W).getSquare()));
        /* Verifying that the spawn points are in the correct position */
        CoordPair spawn = new CoordPair(0,0);
        assertEquals(spawn, board.getSpawnPoints().get(AmmoCube.RED).getLocation());
        spawn = new CoordPair(2,0);
        assertEquals(spawn, board.getSpawnPoints().get(AmmoCube.BLUE).getLocation());
        spawn = new CoordPair(1,1);
        assertEquals(spawn, board.getSpawnPoints().get(AmmoCube.YELLOW).getLocation());
    }

    @Test
    public void createBoard_validBoard_shouldSucceed() throws BoardNotFoundException {
        BoardCreator.loadBoard("/assets/test_boards/board_valid.json");
        Board board = BoardCreator.createBoard(0);
        checkTestBoard(board);
    }

    @Test
    public void createBoard_redundantSquareInRoom_shouldSucceed() throws BoardNotFoundException {
        // This board is equal to board_valid, but square (0,1) in the BLUE room has not been previously declared
        // As a result, it won't be added to the room and the result is identical to board_valid

        BoardCreator.loadBoard("/assets/test_boards/board_valid.json");
        Board board = BoardCreator.createBoard(0);
        checkTestBoard(board);
    }

    @Test(expected = BoardNotFoundException.class)
    public void createBoard_notLoaded_shouldThrow() throws BoardNotFoundException {
        BoardCreator.createBoard(42);
    }

    @Test(expected = JSONException.class)
    public void createBoard_squareOutOfBoundsInField_shouldThrow() throws BoardNotFoundException {
        /* The validator can't detect the error */
        BoardCreator.loadBoard("/assets/test_boards/board_invalid_out_of_bounds_field.json");
        /* But it comes around when building the board */
        BoardCreator.createBoard(0); /* This throws */
    }

    @Test(expected = JSONException.class)
    public void createBoard_squareWithNoRoom_shouldThrow() throws BoardNotFoundException {
        /* The validator can't detect the error */
        BoardCreator.loadBoard("/assets/test_boards/board_invalid_square_no_room.json");
        /* But it comes around when building the board */
        BoardCreator.createBoard(0); /* This throws */
    }

    @Test(expected = JSONException.class)
    public void createBoard_inexistentSpawnPoint_shouldThrow() throws BoardNotFoundException {
        /* The validator can't detect the error */
        BoardCreator.loadBoard("/assets/test_boards/board_invalid_spawnpoint_inexistent.json");
        /* But it comes around when building the board */
        BoardCreator.createBoard(0); /* This throws */
    }

    @Test(expected = JSONException.class)
    public void createBoard_wrongTypeSpawnPoint_shouldThrow() throws BoardNotFoundException {
        /* The validator can't detect the error */
        BoardCreator.loadBoard("/assets/test_boards/board_invalid_spawnpoint_wrong_type.json");
        /* But it comes around when building the board */
        BoardCreator.createBoard(0); /* This throws */
    }
}
package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.BoardNotFoundException;
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

    @Ignore
    @Test
    public void loadBoardPack_standardPack_shouldSucceed() {
        /* TODO: Test the standard pack once we have one */
    }

    @Ignore
    @Test
    public void createBoard_validBoard_shouldSucceed() throws BoardNotFoundException {
        BoardCreator.loadBoard("/assets/test_boards/board_valid.json");
        Board board = BoardCreator.createBoard(0);

        /* TODO: Check that the board has the required properties */
        fail();
    }

    @Ignore
    @Test
    public void createBoard_redundantSquareInRoom_shouldSucceed() throws BoardNotFoundException {
        // This board is equal to board_valid, but square (0,1) in the BLUE room has not been previously declared
        // As a result, it won't be added to the room and the result is identical to board_valid

        BoardCreator.loadBoard("/assets/test_boards/board_valid.json");
        Board board = BoardCreator.createBoard(0);

        /* TODO: Check that the board has the required properties */
        fail();
    }

    @Test(expected = BoardNotFoundException.class)
    public void createboard_notLoaded_shouldThrow() throws BoardNotFoundException {
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
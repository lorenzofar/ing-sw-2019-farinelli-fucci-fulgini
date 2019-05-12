package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.BoardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * Shared class responsible for building game boards from file.
 * The board JSON files must be loaded before being able to request them.
 */
public class BoardCreator {

    /** The format is {@code &lt;board id, file path&gt;} */
    private static final Map<Integer, String> boardFiles = new HashMap<>();

    /* Commodity */
    private static ObjectMapper objectMapper = JSONUtils.getObjectMapper();

    /* This class is static and should not be instantiated */
    private BoardCreator() {}

    /**
     * Loads and validates all boards specified in a board pack JSON file.
     * @param filePath absolute resource path of the pack JSON file
     * @throws MissingResourceException if any of the required resources is not found
     * @throws JSONException if there are errors in the JSON itself
     * @throws ValidationException if the JSON is invalid
     */
    public static void loadBoardPack(String filePath) {
        /* Load the file as JSON */
        JSONObject pack = JSONUtils.loadJSONResource(filePath);

        /* Validate it against schema */
        JSONUtils.validateBoardPack(pack);

        /* If i got here the schema is valid, so no more checks are necessary */
        JSONArray boardFiles = pack.getJSONArray("boardFiles");

        /* The array contains resource paths of the boards */
        for (int i=0; i < boardFiles.length(); i++) {
            loadBoard(boardFiles.getString(i));
        }
    }

    /**
     * Loads the given board from file.
     * @param filePath description of the board
     * @throws MissingResourceException if the required file is not found
     * @throws JSONException if anything goes wrong while parsing JSON
     * @throws ValidationException if the JSON is invalid
     */
    public static void loadBoard(String filePath) {
        /* Load the resource */
        JSONObject board = JSONUtils.loadJSONResource(filePath);

        /* Validate it against schema */
        JSONUtils.validateBoard(board);

        /* Get the id */
        int id = board.getInt("id");

        /* Save the board */
        boardFiles.put(id, filePath);
    }

    /**
     * Creates a new board with given id, which must have been previously loaded.
     * @param id id of the board to be created
     * @return the new board
     * @throws BoardNotFoundException if the board description has not been loaded
     * @throws MissingResourceException if the board description file cannot be found
     * @throws JSONException if anything goes wrong while parsing JSON
     */
    public static Board createBoard(int id) throws BoardNotFoundException {
        /* Load the resource */
        String filePath = boardFiles.get(id);
        if (filePath == null) {
            throw new BoardNotFoundException(String.format("Board with id \"%d\" has not been loaded", id));
        }
        JSONObject boardDesc = JSONUtils.loadJSONResource(filePath);

        /* Build the empty board */
        Board board = new Board(
                boardDesc.getInt("xSize"),
                boardDesc.getInt("ySize")
        );

        /* Deserialize the list of squares and add them to the board */
        Square[] squares = deserializeSquares(boardDesc.getJSONArray("squares"));
        for (Square square : squares) {
            try {
                board.addSquare(square);
            } catch (IndexOutOfBoundsException e) {
                /* The square is out of the board size */
                throw new JSONException(e);
            }
        }

        /* Now fill in the rooms with squares */
        JSONArray rooms = boardDesc.getJSONArray("rooms");
        for (int i = 0; i < rooms.length(); i++) {
            fillRoom(board, rooms.getJSONObject(i));
        }

        /* Check that each square belongs to a room */
        for (Square square : squares) {
            if (square.getRoom() == null) {
                throw new JSONException(String.format("Square %s belongs to no room", square.getLocation()));
            }
        }

        /* Set the spawn points */
        JSONObject spawnPoints = boardDesc.getJSONObject("spawnPoints");
        for (AmmoCube color : AmmoCube.values()) {
            setSpawnPoint(board, color, spawnPoints);
        }

        return board;
    }

    /**
     * Returns whether the board with specified id has been loaded or not.
     * @param id id of the board
     * @return if the board is available or not
     */
    public static boolean isBoardAvailable(int id) {
        return boardFiles.containsKey(id);
    }

    /**
     * Discards all the board files loaded until now, i.e. brings the class back to its original state
     */
    public static void reset() {
        boardFiles.clear();
    }

    /* Helper method */
    private static Square[] deserializeSquares(JSONArray squares) {
        /* Use Jackson to do the job */
        try {
            return objectMapper.readValue(squares.toString(), Square[].class);
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    /* Helper method */
    private static void fillRoom(Board board, JSONObject roomDesc) {
        try {
            /* Get the room color */
            RoomColor color = RoomColor.valueOf(roomDesc.getString("color"));
            /* Get the square coordinates */
            CoordPair[] coordPairs = objectMapper.readValue(
                    roomDesc.getJSONArray("squares").toString(),
                    CoordPair[].class
            );

            /* Get the actual room and insert the squares based on provided coordinates */
            /* NOTE: In case a non-existent square is said to be inside a room, it is simply not added */
            Room room = board.getRooms().get(color);
            for (CoordPair coordPair : coordPairs) {
                Square square = board.getSquare(coordPair);
                if (square != null) {
                    room.addSquare(square);
                }
            }
        } catch (IllegalArgumentException | IOException e) {
            throw new JSONException(e);
        }
    }

    /* Helper method */
    private static void setSpawnPoint(Board board, AmmoCube color, JSONObject spawnPoints) {
        try {
            /* Get the coordpair */
            CoordPair location = objectMapper.readValue(
                    spawnPoints.getJSONObject((color.name())).toString(),
                    CoordPair.class
            );

            /* Then get the associated square and test it is a spawn square */
            Square square = board.getSquare(location);
            if (square == null) {
                throw new JSONException(String.format("The square %s does not exist", location));
            }
            if (!(square instanceof SpawnSquare)) {
                throw new JSONException(String.format("The square %s is not a spawn square", location));
            }

            /* Set it as a spawn point */
            board.setSpawnPoint(color, (SpawnSquare) square);
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }
}

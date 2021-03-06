package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ModelManager;
import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.*;
import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.*;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer.setColumnConstraints;
import static it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer.setRowConstraints;

/**
 * The controller used to handle the game screen, responsible of updating the information rendered in all the
 * custom controls according to data retrieved from the {@link ModelManager}. It shows:
 * <ul>
 * <li>The game board, with the killshots track and the map containing all the squares with their content</li>
 * <li>The player board of the user</li>
 * <li>The name of the player that is currently playing</li>
 * <li>The score of the user</li>
 * <li>The frenzy status of the match (i.e. on/off)</li>
 * <li>The list of players participating in the match</li>
 * <li>The weapons owned by the other players</li>
 * <li>The player boards of the other players</li>
 * <li>The powerup cards owned by the user</li>
 * <li>The weapon cards owned by the user</li>
 * </ul>
 *
 * @author Lorenzo Farinelli
 */
public class GameController extends GUIController {

    private static final double[] GAME_CONTAINER_ROWS = {20.77, 68 + 11.23};
    private static final double[] TOP_ROW_COLUMNS = {51.4, 48.6};
    private static final double[] MIDDLE_ROW_COLUMNS = {16, 68, 16};
    /* ===== BOARD ROWS AND COLUMNS ===== */
    private static final double[] BOARD_CONTAINER_ROWS = {27.67, 31.14, 27.02, 14.18};
    private static final double[] BOARD_CONTAINER_COLUMNS = {22.5, 27, 28, 22.5};
    /* ===== TOP ROW WEAPONS ===== */
    private static final double[] TOP_ROW_SPAWN_WEAPONS_COLUMNS = {2.3, 21, 1.6, 20.6, 1.8, 21, 31.7};
    private static final double[] SX_COL_SPAWN_WEAPONS_ROWS = {19.14, 17.12, 1.24, 17.06, 0.96, 17.09, 27.40};
    private static final double[] DX_COL_SPAWN_WEAPONS_ROWS = {45.06, 16.57, 1.56, 16.47, 1.78, 16.48, 2.08};

    private static final double BOARD_RATIO = 1389.6 / 1836.72;
    /**
     * The percentage height of the screen the board occupies
     */
    private static final double BOARD_ROW_RATIO = 0.75;
    private static final int SPAWN_WEAPONS_COUNT = 3;

    @FXML
    private Scene gameScene;
    @FXML
    private GridPane gameContainer;
    @FXML
    private GridPane boardContainer;
    @FXML
    private GridPane topRow;
    @FXML
    private KillshotsTrack killshotsTrack;
    @FXML
    private GridPane topWeaponsContainer;
    @FXML
    private GridPane middleRow;
    @FXML
    private GridPane middleSxContainer;
    @FXML
    private GridPane middleDxContainer;

    /**
     * The matrix of overlays above the square composing the game board
     */
    private SquareOverlay[][] squareOverlays;

    @FXML
    private AmmoPane ammoPane;
    @FXML
    private MatchInfoPane matchInfoPane;
    @FXML
    private WeaponsInfoPane weaponsInfoPane;
    @FXML
    private PlayerBoardControl userBoard;
    @FXML
    private VBox playerBoardsContainer;
    @FXML
    private PowerupsContainer powerupsContainer;
    @FXML
    private WeaponsContainer weaponsContainer;
    @FXML
    private Label squareRequestMessage;

    /**
     * A map describing the player board control associated to each player
     */
    private Map<String, PlayerBoardControl> playerBoards;

    /**
     * The map associating each spawn color to the list of weapon images sockets
     */
    private Map<AmmoCube, List<WeaponImage>> spawnWeaponsImages;

    /**
     * A string consumer that takes a weapon id and shows the corresponding card image in a new window
     */
    private Consumer<String> weaponsConsumer = weaponId -> {
        // Show a new window for the image viewer
        ImageViewerController imageViewerController = (ImageViewerController) ((GUIRenderer) clientView.getRenderer()).showNewWindow("/fxml/imageViewer.fxml", "Weapon details");
        imageViewerController.setImage(String.format("/images/weapons/%s.png", weaponId));
    };

    @FXML
    public void initialize() {
        // Set row and column constraints for the grid pane
        setRowConstraints(gameContainer, GAME_CONTAINER_ROWS);
        setColumnConstraints(topRow, TOP_ROW_COLUMNS);
        setColumnConstraints(topWeaponsContainer, TOP_ROW_SPAWN_WEAPONS_COLUMNS);
        setColumnConstraints(middleRow, MIDDLE_ROW_COLUMNS);
        setRowConstraints(boardContainer, BOARD_CONTAINER_ROWS);
        setColumnConstraints(boardContainer, BOARD_CONTAINER_COLUMNS);
        setRowConstraints(middleSxContainer, SX_COL_SPAWN_WEAPONS_ROWS);
        setRowConstraints(middleDxContainer, DX_COL_SPAWN_WEAPONS_ROWS);

        gameContainer.minHeightProperty().bind(gameScene.heightProperty().multiply(BOARD_ROW_RATIO));
        gameContainer.maxHeightProperty().bind(gameScene.heightProperty().multiply(BOARD_ROW_RATIO));
        gameContainer.minWidthProperty().bind(gameScene.heightProperty().multiply(BOARD_ROW_RATIO).divide(BOARD_RATIO));
        gameContainer.maxWidthProperty().bind(gameScene.heightProperty().multiply(BOARD_ROW_RATIO).divide(BOARD_RATIO));
        squareRequestMessage.minWidthProperty().bind(gameScene.heightProperty().multiply(BOARD_ROW_RATIO).divide(BOARD_RATIO));
        squareRequestMessage.minWidthProperty().bind(gameScene.heightProperty().multiply(BOARD_ROW_RATIO).divide(BOARD_RATIO));

        userBoard.prefWidthProperty().bind(gameScene.heightProperty().multiply(0.2).multiply(0.9).multiply(PlayerBoardControl.BOARD_RATIO));

        /* ===== BOARD LAYOUT ===== */
        middleRow.prefWidthProperty().bind(gameContainer.widthProperty());
        middleRow.prefHeightProperty().bind(gameContainer.heightProperty().multiply(GAME_CONTAINER_ROWS[1] / 100));
        killshotsTrack.prefWidthProperty().bind(topRow.widthProperty().multiply(TOP_ROW_COLUMNS[0] / 100));
        killshotsTrack.prefHeightProperty().bind(topRow.heightProperty());
        topWeaponsContainer.prefWidthProperty().bind(topRow.widthProperty().multiply(TOP_ROW_COLUMNS[1] / 100));
        topWeaponsContainer.prefHeightProperty().bind(topRow.heightProperty());
        boardContainer.prefWidthProperty().bind(middleRow.widthProperty().multiply(MIDDLE_ROW_COLUMNS[1] / 100));
        boardContainer.prefHeightProperty().bind(middleRow.heightProperty());
        middleSxContainer.prefWidthProperty().bind(middleRow.widthProperty().multiply(MIDDLE_ROW_COLUMNS[0] / 100));
        middleSxContainer.prefHeightProperty().bind(middleRow.heightProperty());
        middleDxContainer.prefWidthProperty().bind(middleRow.widthProperty().multiply(MIDDLE_ROW_COLUMNS[2] / 100));
        middleDxContainer.prefHeightProperty().bind(middleRow.heightProperty());
        topWeaponsContainer.setPadding(new Insets(0, 0, 12, 0));
        middleSxContainer.setPadding(new Insets(0, 12, 0, 0));
        middleDxContainer.setPadding(new Insets(0, 0, 0, 12));
        /* ======================== */

        // Initialize the map of spawn weapons containers
        Map<AmmoCube, GridPane> spawnWeaponsContainers = new EnumMap<>(AmmoCube.class);
        spawnWeaponsImages = new EnumMap<>(AmmoCube.class);
        spawnWeaponsContainers.put(AmmoCube.BLUE, topWeaponsContainer);
        spawnWeaponsContainers.put(AmmoCube.RED, middleSxContainer);
        spawnWeaponsContainers.put(AmmoCube.YELLOW, middleDxContainer);

        // Create the containers for weapons in the three tracks
        for (AmmoCube color : AmmoCube.values()) {
            // Create an empty list of images
            List<WeaponImage> weaponImages = new ArrayList<>();
            // Get the container in which to put the images
            GridPane container = spawnWeaponsContainers.get(color);
            // Then create as many images to host all the weapons in the square
            for (int i = 0; i < SPAWN_WEAPONS_COUNT; i++) {
                WeaponImage weaponImage = new WeaponImage();
                // Change orientation and position according to the color of their container
                if (color == AmmoCube.BLUE) {
                    GridPane.setColumnIndex(weaponImage, 2 * i + 1);
                    weaponImage.prefHeightProperty().bind(container.heightProperty());
                    weaponImage.prefWidthProperty().bind(container.widthProperty().multiply(container.getColumnConstraints().get(2 * i + 1).getPercentWidth() / 100));
                    weaponImage.setOrientation(OrientationEnum.UP);
                } else {
                    GridPane.setRowIndex(weaponImage, 2 * i + 1);
                    weaponImage.prefWidthProperty().bind(container.widthProperty());
                    weaponImage.prefHeightProperty().bind(container.heightProperty().multiply(container.getRowConstraints().get(2 * i + 1).getPercentHeight() / 100));
                    weaponImage.setOrientation(color == AmmoCube.RED ? OrientationEnum.LEFT : OrientationEnum.RIGHT);
                }
                weaponImages.add(weaponImage);
            }
            // Populate the container with the newly created images
            container.getChildren().clear();
            container.getChildren().addAll(weaponImages);
            // And eventually update the map
            spawnWeaponsImages.put(color, weaponImages);
        }
    }

    /**
     * Set up the layout of the game screen by creating overlays and binding dimensions
     */
    void buildMatchScreen() {

        // Get the model manager
        ModelManager modelManager = clientView.getModelManager();

        /* ===== BOARD RENDERING ===== */
        final BoardView boardView = modelManager.getBoard();
        if (boardView == null) {
            return;
        }

        // Build the matrix of overlays
        squareOverlays = new SquareOverlay[boardView.getColumnsCount()][boardView.getRowsCount()];
        // Then create the overlays according to the board matrix
        for (int i = 0; i < boardView.getSquares().length; i++) {
            for (int j = 0; j < boardView.getSquares()[i].length; j++) {
                SquareView square = boardView.getSquares()[i][j];
                if (square != null) {
                    SquareOverlay overlay = square.printTypeMarker().equals("S") ? new SpawnSquareOverlay(square.getLocation()) : new AmmoSquareOverlay(square.getLocation());
                    // Add it to the container
                    boardContainer.getChildren().add(overlay);
                    // Set the correct column and row indexes
                    GridPane.setRowIndex(overlay, j);
                    GridPane.setColumnIndex(overlay, i);
                    // Then bind its dimensions to those of the container
                    overlay.prefWidthProperty().bind(boardContainer.widthProperty().multiply(BOARD_CONTAINER_COLUMNS[i] / 100));
                    overlay.prefHeightProperty().bind(boardContainer.heightProperty().multiply(BOARD_CONTAINER_ROWS[j] / 100));
                    squareOverlays[i][j] = overlay;
                } else {
                    squareOverlays[i][j] = null;
                }
            }
        }

        // Initialize the map storing the player boards for each player
        playerBoards = new HashMap<>();
        // And put inside it the player board of the user
        playerBoards.put(clientView.getUsername(), userBoard);

        // Then create the player boards for the other players
        clientView.getModelManager().getPlayers().keySet().stream().filter(player -> !player.equals(clientView.getUsername())).forEach(player -> {
            PlayerBoardControl playerBoardControl = new PlayerBoardControl();
            playerBoardControl.prefHeightProperty().bind(playerBoardsContainer.widthProperty().divide(PlayerBoardControl.BOARD_RATIO));
            GridPane.setHgrow(playerBoardControl, Priority.ALWAYS);
            playerBoardsContainer.getChildren().add(playerBoardControl);
            playerBoards.put(player, playerBoardControl);
        });

        // Then bind their widths to that of the container
        playerBoardsContainer.widthProperty().addListener((obs, oldVal, newVal) ->
                playerBoards.values().stream().filter(board -> !board.equals(userBoard)).forEach(playerBoard -> {
                    playerBoard.setMinWidth(newVal.doubleValue() * 0.8);
                    playerBoard.setMaxWidth(newVal.doubleValue() * 0.8);
                })
        );

        // Set the image viewer handler for the weapons table and weapons container
        // and make it open a new window with the image of the selected weapon card
        weaponsInfoPane.setImageViewer(weaponsConsumer);
        weaponsContainer.setWeaponsConsumer(weaponsConsumer);

        // Then load the background
        Platform.runLater(() -> setBoard(boardView.getId()));

        refreshMatchScreen();

        // For each player, update its player board
        clientView.getModelManager().getPlayers().keySet().forEach(this::updatePlayerBoard);
    }

    /**
     * Sets the game board container to have the provided board as background
     *
     * @param boardId The identifier of the board
     */
    public void setBoard(int boardId) {
        // Compute the needed background size for the board asset
        BackgroundSize boardSize = new BackgroundSize(100, 100, true, true, true, false);
        // Create a new background image from the provided board
        Image boardImage = new Image(String.format("/images/boards/board_%d.png", boardId));
        BackgroundImage boardBackground = new BackgroundImage(boardImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, boardSize);
        // Set the background of the board container
        gameContainer.setBackground(new Background(boardBackground));
    }

    /**
     * Asks the user to select a square of the game board to reply to the provided request
     *
     * @param request The object representing the square request
     */
    void askSquareSelection(SquareRequest request) {
        // Set the text of the square request message
        squareRequestMessage.setText(request.getMessage());
        // First retrieve all the squares the user can select among and create a pool of observable overlays
        Collection<SelectableOverlay<CoordPair>> selectableSquares = Arrays.stream(squareOverlays).flatMap(Arrays::stream).filter(Objects::nonNull).filter(overlay -> request.getChoices().contains(overlay.getData())).collect(Collectors.toList());
        // Then create a selection handler to handle the request and set it as the current one in the view
        clientView.setSelectionHandler(new SelectionHandler<CoordPair>(selectableSquares));
    }

    /**
     * Resets and clears all the messages shown to the user in previous requests
     */
    void resetRequestMessages() {
        squareRequestMessage.setText("");
    }

    /**
     * Refresh all the containers and rendered content in the screen
     */
    void refreshMatchScreen() {
        updateBoard();
        updateKillshotsTrack();
        updateMatchInfo();
        updateWeaponsInfo();
        updateAmmoAmount();
        updateSpawnWeapons();
        updateOwnedPowerups();
    }

    /**
     * Update the ammo information shown in the ammo pane with the most recent one
     */
    void updateAmmoAmount() {
        ammoPane.setAmmo(clientView.getModelManager().getPlayers().get(clientView.getUsername()).getAmmo());
    }

    /**
     * Update the match information shown in the corresponding pane
     */
    void updateMatchInfo() {
        // Set the list of players
        matchInfoPane.setPlayers(clientView.getModelManager().getPlayersColors());
        // Set the score of the player
        matchInfoPane.setScore(clientView.getModelManager().getPlayers().get(clientView.getUsername()).getScore());
        // Set the frenzy mode indicator
        matchInfoPane.setFrenzyModeIndicator(clientView.getModelManager().getMatch().isFrenzy());
        // Set the currently playing player
        if (clientView.getModelManager().getCurrentTurn() != null) {
            matchInfoPane.setCurrentPlayer(clientView.getModelManager().getCurrentTurn().getPlayer());
        }
    }

    /**
     * Updates the weapon cards contained in each spawn square
     */
    void updateSpawnWeapons() {
        spawnWeaponsImages.forEach((cube, images) -> {
            // We get the corresponding spawn square for the cube
            CoordPair location = clientView.getModelManager().getBoard().getSpawnPoints().get(cube);
            SpawnSquareView spawnSquare = (SpawnSquareView) clientView.getModelManager().getBoard().getSquare(location);
            List<String> weaponCards = spawnSquare.getWeapons();
            // Compute the length of the shortest list
            int limit = Math.min(weaponCards.size(), images.size());
            int i = 0;
            while (i < limit) {
                images.get(i).setWeapon(weaponCards.get(i));
                i++;
            }
            while (i < images.size()) {
                // If there are remaining places we fill them with null values
                images.get(i).setWeapon(null);
                i++;
            }
        });
    }

    /**
     * Update the indicator of available skulls in the killshots track
     */
    void updateKillshotsTrack() {
        MatchView match = clientView.getModelManager().getMatch();
        if (match == null) {
            return;
        }
        killshotsTrack.setSkulls(match.getTotalSkulls(),
                match.getKillshotsTrack().stream()
                        .map(shooter -> clientView.getModelManager().getPlayersColors().get(shooter))
                        .collect(Collectors.toList()));
    }

    /**
     * Update the displayed information on the player board of the provided player
     *
     * @param player The username of the player
     */
    void updatePlayerBoard(String player) {
        // First load the view of the player board
        PlayerBoardView playerBoardView = clientView.getModelManager().getPlayerBoards().get(player);
        if (playerBoardView == null) {
            // The provided player does not exist
            return;
        }
        // Then load the corresponding UI control
        PlayerBoardControl playerBoardControl = playerBoards.get(player);
        if (playerBoardControl == null) {
            // The provided player has no associated board controls
            // We do not do anything, since all the boards for all the players are created on game launch
            return;
        }
        Platform.runLater(() -> {
            // Eventually update the information
            playerBoardControl.setPlayerName(player);
            playerBoardControl.setBoardColor((PlayerColor) clientView.getModelManager().getPlayersColors().get(player));
            playerBoardControl.setBoardState(playerBoardView.getState());
            playerBoardControl.setDamageTokens(
                    playerBoardView.getDamages().stream().map(shooter -> clientView.getModelManager().getPlayersColors().get(shooter)).collect(Collectors.toList()));
            playerBoardControl.setPlayerDeaths(clientView.getModelManager().getPlayerBoards().get(player).getDeaths());
            playerBoardControl.setPlayerMarks(clientView.getModelManager().getPlayerBoards().get(player).getMarks());
        });
    }

    /**
     * Update the rendered content of all the square overlays contained in the board
     */
    void updateBoard() {
        // Get all the squares contained in the board and update the corresponding overlay
        for (SquareView[] squaresRow : clientView.getModelManager().getBoard().getSquares()) {
            for (SquareView squareView : squaresRow) {
                // We do not consider null cells (i.e. missing squares)
                if (squareView != null) {
                    updateSquareOverlay(squareView.getLocation());
                }
            }
        }
    }

    /**
     * Update the rendered content of the overlay corresponding to the provided square
     *
     * @param location The location of the square in cartesian coordinates
     */
    void updateSquareOverlay(CoordPair location) {
        if (squareOverlays == null) {
            return;
        }
        SquareOverlay squareOverlay = squareOverlays[location.getX()][location.getY()];
        SquareView squareView = clientView.getModelManager().getBoard().getSquare(location);
        // Check whether the square is an ammo square
        if (!clientView.getModelManager().getBoard().getSpawnPoints().containsValue(location)) {
            // And update its ammo card
            ((AmmoSquareOverlay) squareOverlay).setAmmoCard(((AmmoSquareView) squareView).getAmmoCard());
        }
        // Then get all the players that are inside that square and update the square content
        Map<String, ColoredObject> squarePlayers = clientView.getModelManager().getPlayersColors()
                .entrySet().stream()
                .filter(entry -> squareView.getPlayers().contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        squareOverlay.updateContent(squarePlayers);
    }

    /**
     * Updates the rendered content of the pane showing information about weapons owned by players
     */
    void updateWeaponsInfo() {
        // First retrieve all the weapons owned by the user
        List<WeaponCard> userWeapons = clientView.getModelManager().getPlayers().get(clientView.getUsername()).getWeapons();
        // Then all the weapons owned by the enemies
        Map<String, List<WeaponCard>> enemiesWeapons = clientView.getModelManager().getPlayers().entrySet().stream().filter(entry -> !entry.getKey().equals(clientView.getUsername()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getWeapons()));
        weaponsInfoPane.setPlayersWeapons(enemiesWeapons);
        weaponsContainer.setWeapons(userWeapons);
    }

    /**
     * Updates the rendered powerup cards owned by the user
     */
    void updateOwnedPowerups() {
        powerupsContainer.setPowerups(clientView.getModelManager().getPlayers().get(clientView.getUsername()).getPowerups());
    }
}
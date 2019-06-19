package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ModelManager;
import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.*;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.ActionCardView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.BoardView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.SquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ActionRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PlayerOperationRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerOperationEnum;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer.setColumnConstraints;
import static it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer.setRowConstraints;

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

    private static final int SPAWN_WEAPONS_COUNT = 3;

    @FXML
    private VBox gameScene;
    @FXML
    private GridPane gameContainer;
    @FXML
    private GridPane boardContainer;
    @FXML
    private GridPane topRow;
    @FXML
    private GridPane killshotsTrackContainer;
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

    /**
     * The container for all the supported players operations
     * It references the control defined in the FXML file
     */
    @FXML
    private PlayerOperationsTrack playerOperationsTrack;
    @FXML
    private ActionsTrack actionsTrack;

    /**
     * The map associating each spawn color to the list of weapon images sockets
     */
    private Map<AmmoCube, List<WeaponImage>> spawnWeaponsImages;

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

        gameContainer.prefHeightProperty().bind(gameScene.heightProperty());
        gameContainer.prefWidthProperty().bind(gameScene.widthProperty());

        middleRow.prefWidthProperty().bind(gameContainer.widthProperty());
        middleRow.prefHeightProperty().bind(gameContainer.heightProperty().multiply(GAME_CONTAINER_ROWS[1] / 100));

        killshotsTrackContainer.prefWidthProperty().bind(topRow.widthProperty().multiply(TOP_ROW_COLUMNS[0] / 100));
        killshotsTrackContainer.prefHeightProperty().bind(topRow.heightProperty());
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

        // TODO: Check the order of objects creation
        // Build the matrix of overlays
        squareOverlays = new SquareOverlay[boardView.getColumnsCount()][boardView.getRowsCount()];
        // Then create the overlays according to the board matrix
        for (int i = 0; i < boardView.getSquares().length; i++) {
            for (int j = 0; j < boardView.getSquares()[i].length; j++) {
                SquareView square = boardView.getSquares()[i][j];
                SquareOverlay overlay = OverlaysFactory.getInstance().createSquareOverlay(square);
                if (overlay != null) {
                    // Add it to the container
                    boardContainer.getChildren().add(overlay);
                    // Set the correct column and row indexes
                    GridPane.setRowIndex(overlay, j);
                    GridPane.setColumnIndex(overlay, i);
                    // Then bind its dimensions to those of the container
                    overlay.prefWidthProperty().bind(boardContainer.widthProperty().multiply(BOARD_CONTAINER_COLUMNS[i] / 100));
                    overlay.prefHeightProperty().bind(boardContainer.heightProperty().multiply(BOARD_CONTAINER_ROWS[j] / 100));
                }
                squareOverlays[i][j] = overlay;
            }
        }

        // Then load the action card of the user into the actions track
        ActionCardView userActionCard = modelManager.getActionCard(clientView.getUsername());
        actionsTrack.setActionCard(userActionCard);

        // Then load the background
        Platform.runLater(() -> setBoard(boardView.getId()));

        //TODO: Finish implementing this method
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
        // First retrieve all the squares the user can select among and create a pool of observable overlays
        Collection<SelectableOverlay<CoordPair>> selectableSquares = Arrays.stream(squareOverlays).flatMap(Arrays::stream).filter(overlay -> request.getChoices().contains(overlay.getData())).collect(Collectors.toList());
        // Then create a selection handler to handle the request and set it as the current one in the view
        clientView.setSelectionHandler(new SelectionHandler<CoordPair>(selectableSquares));
    }

    /**
     * Asks the user to select an operation to perform in the current turn, replying to the related server request
     *
     * @param request The object representing the operation request
     */
    void askOperationSelection(PlayerOperationRequest request) {
        // First retrieve all the selectable overlays in the operations track
        Collection<SelectableOverlay<PlayerOperationEnum>> selectableOperations = playerOperationsTrack.getSelectableOverlays(request.getChoices());
        // Then create a new selection handler and set it in the client view
        clientView.setSelectionHandler(new SelectionHandler<PlayerOperationEnum>(selectableOperations));
    }

    /**
     * Asks the user to select an action to perform, replying to the related server request
     *
     * @param request The object representing the request
     */
    void askActionSelection(ActionRequest request) {
        // First retrieve all the selectable overlays in the actions track
        Collection<SelectableOverlay<ActionEnum>> selectableActions = actionsTrack.getSelectableOverlays(request.getChoices());
        // Then create a new selection handler and set it in the client view
        clientView.setSelectionHandler(new SelectionHandler<ActionEnum>(selectableActions));
    }
}

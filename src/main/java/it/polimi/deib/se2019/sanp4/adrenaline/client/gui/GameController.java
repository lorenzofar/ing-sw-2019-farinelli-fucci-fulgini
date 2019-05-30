package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class GameController extends GUIController {

    private static final double[] GAME_CONTAINER_ROWS = {20.77, 68, 11.23};
    private static final double[] TOP_ROW_COLUMNS = {51.4, 48.6};
    private static final double[] MIDDLE_ROW_COLUMNS = {16, 68, 16};
    private static final double[] BOTTOM_ROW_COLUMNS = {50, 50}; // TODO: Set real values
    /* ===== BOARD ROWS AND COLUMNS ===== */
    private static final double[] BOARD_CONTAINER_ROWS = {32.2, 36.3, 31.5};
    private static final double[] BOARD_CONTAINER_COLUMNS = {22.5, 27, 28, 22.5};

    @FXML
    private VBox gameScene;
    @FXML
    private GridPane gameContainer;
    @FXML
    private GridPane boardContainer;
    @FXML
    private GridPane topRow;
    @FXML
    private GridPane middleRow;
    @FXML
    private GridPane bottomRow;

    /**
     * Creates row constraints for the provided grid pane according to the provided heights
     *
     * @param targetPane  The pane to create the constraints into
     * @param rowsHeights The array containing the percentage height for each row
     */
    private void setRowConstraints(GridPane targetPane, double[] rowsHeights) {
        for (double rowHeight : rowsHeights) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(rowHeight);
            targetPane.getRowConstraints().add(rowConstraints);
        }
    }

    /**
     * Creates column constraints for the provided grid pane according to the provided widths
     *
     * @param targetPane    The pane to create the constraints into
     * @param columnsWidths The array containing the percentage width for each column
     */
    private void setColumnConstraints(GridPane targetPane, double[] columnsWidths) {
        for (double columnWidth : columnsWidths) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(columnWidth);
            targetPane.getColumnConstraints().add(columnConstraints);
        }
    }

    @FXML
    public void initialize() {
        // Set row and column constraints for the grid pane
        setRowConstraints(gameContainer, GAME_CONTAINER_ROWS);
        setColumnConstraints(topRow, TOP_ROW_COLUMNS);
        setColumnConstraints(middleRow, MIDDLE_ROW_COLUMNS);
        setColumnConstraints(bottomRow, BOTTOM_ROW_COLUMNS);
        setRowConstraints(boardContainer, BOARD_CONTAINER_ROWS);
        setColumnConstraints(boardContainer, BOARD_CONTAINER_COLUMNS);

        gameContainer.prefHeightProperty().bind(gameScene.heightProperty());
        gameContainer.prefWidthProperty().bind(gameScene.widthProperty());

        middleRow.prefWidthProperty().bind(gameContainer.widthProperty());
        middleRow.prefHeightProperty().bind(gameContainer.heightProperty().multiply(GAME_CONTAINER_ROWS[1]/100));

        boardContainer.prefWidthProperty().bind(middleRow.widthProperty().multiply(MIDDLE_ROW_COLUMNS[1]/100));
        boardContainer.prefHeightProperty().bind(middleRow.heightProperty());

        //TODO: Finish implementing this method
    }

    /**
     * Sets the game board container to have the provided board as background
     *
     * @param boardId The identifier of the board
     */
    public void setBoard(String boardId) {
        // Compute the needed background size for the board asset
        BackgroundSize boardSize = new BackgroundSize(100, 100, true, true, true, false);
        // Create a new background image from the provided board
        Image boardImage = new Image(String.format("/assets/boards/%s.png", boardId));
        BackgroundImage boardBackground = new BackgroundImage(boardImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, boardSize);
        // Set the background of the board container
        gameContainer.setBackground(new Background(boardBackground));
    }
}

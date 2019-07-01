package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom control representing the player board, showing information about:
 * <ul>
 * <li>Player name</li>
 * <li>Count of deaths</li>
 * <li>Count of marks</li>
 * <li>Received damages</li>
 * </ul>
 */
public class PlayerBoardControl extends VBox {

    /**
     * The ratios of damage track cells for boards in FRENZY mode
     */
    private static final double[] FRENZY_CELLS = {8.27, 8.23, 25, 8.25, 8.21, 8.23, 8.23, 8.36, 8.20, 8.26, 9.18, 8.30};
    /**
     * The ratios of damage track cells for boards in REGULAR mode
     */
    private static final double[] REGULAR_CELLS = {8.02, 8.75, 8.48, 8.08, 8.72, 8.51, 8.08, 8.14, 8.05, 8.69, 8.57, 7.90};
    /**
     * The ratios of the board rows
     */
    private static final double[] ROWS = {34, 32, 34};
    /**
     * The ratios of the columns in the middle row, containing the damage track
     */
    private static final double[] MIDDLE_ROW_COLUMNS = {9, 67, 24};
    /**
     * The ratio of the board
     */
    public static final double BOARD_RATIO = 1121.0 / 274.0;

    /**
     * A map associating each board state to the ratios of cells in the damage track
     */
    private final Map<String, double[]> stateCellsMap;
    /**
     * The state of the board, defaulting with the regular state
     */
    private String boardState = "regular";
    /**
     * The color of the board, matching that of the player owning it
     */
    private PlayerColor boardColor;
    /**
     * The grid-pane containing the damage tokens of the player board
     */
    private GridPane damageTokensContainer;
    /**
     * The actual player board, with the corresponding graphic
     */
    private GridPane playerBoardGrid;
    /**
     * The property storing the name of the player
     */
    private StringProperty playerName;
    /**
     * The property storing the count of deaths of the player
     */
    private IntegerProperty playerDeaths;
    /**
     * The property storing the count of marks the player received
     */
    private IntegerProperty playerMarks;

    public PlayerBoardControl() {
        super();
        this.setSpacing(8);
        // Initialize the grid pane for the player board
        playerBoardGrid = new GridPane();

        stateCellsMap = new HashMap<>();
        stateCellsMap.put("regular", REGULAR_CELLS);
        stateCellsMap.put("frenzy", FRENZY_CELLS);

        // Initialize properties
        playerName = new SimpleStringProperty("---");
        playerMarks = new SimpleIntegerProperty(0);
        playerDeaths = new SimpleIntegerProperty(0);

        // First create an horizontal container to host information and stats
        HBox playerInfoContainer = new HBox(8);
        playerInfoContainer.setAlignment(Pos.CENTER_LEFT);
        // Create a label for the player name
        Label playerNameLabel = new Label();
        playerNameLabel.textProperty().bind(playerName);
        playerNameLabel.getStyleClass().add(GUIRenderer.CSS_BOLD_TITLE);
        playerInfoContainer.getChildren().add(playerNameLabel);
        // One for the count of deaths
        Label playerDeathsLabel = new Label();
        playerDeathsLabel.textProperty().bind(playerDeaths.asString("%d deaths"));
        playerInfoContainer.getChildren().add(playerDeathsLabel);
        // And one for the count of marks
        Label playerMarksLabel = new Label();
        playerMarksLabel.textProperty().bind(playerMarks.asString("%d marks"));
        playerInfoContainer.getChildren().add(playerMarksLabel);
        this.getChildren().add(playerInfoContainer);

        // Then create the board layout adding the three rows
        GUIRenderer.setRowConstraints(playerBoardGrid, ROWS);

        for (int i = 0; i < ROWS.length; i++) {
            GridPane row = new GridPane();
            row.prefHeightProperty().bind(playerBoardGrid.heightProperty().multiply(ROWS[i] / 100));
            row.prefWidthProperty().bind(playerBoardGrid.widthProperty());
            GridPane.setRowIndex(row, i);
            playerBoardGrid.getChildren().add(row);
        }
        GridPane middleRow = (GridPane) playerBoardGrid.getChildren().get(1);

        damageTokensContainer = new GridPane();
        GridPane.setColumnIndex(damageTokensContainer, 1);
        damageTokensContainer.prefWidthProperty().bind(middleRow.widthProperty().multiply(MIDDLE_ROW_COLUMNS[1] / 100));
        damageTokensContainer.prefHeightProperty().bind(middleRow.heightProperty());
        middleRow.getChildren().add(damageTokensContainer);

        // Then set the columns of the tokens container
        GUIRenderer.setColumnConstraints(middleRow, MIDDLE_ROW_COLUMNS);
        GUIRenderer.setColumnConstraints(damageTokensContainer, stateCellsMap.get(boardState));

        // Then bind the width of the board to that of the container
        this.widthProperty().addListener((ob, oldval, newVal) -> {
            playerBoardGrid.setMinWidth(newVal.doubleValue());
            playerBoardGrid.setMaxWidth(newVal.doubleValue());
            playerBoardGrid.setMinHeight(newVal.doubleValue() / BOARD_RATIO);
            playerBoardGrid.setMaxHeight(newVal.doubleValue() / BOARD_RATIO);
        });
        this.getChildren().add(playerBoardGrid);
    }

    /**
     * Create a new pane to act as an overlay for a damage token, with the provided color
     *
     * @param ratio   The ratio to which is related to that of the parent
     * @param parent  The node representing its parent
     * @param hexCode The HEX code of the color
     * @return The Pane representing the overlay
     */
    private Pane createTokenOverlay(double ratio, GridPane parent, String hexCode) {
        Pane overlay = new Pane();
        // Bind the newly created pane to the father
        overlay.prefHeightProperty().bind(parent.heightProperty());
        overlay.prefWidthProperty().bind(parent.widthProperty().multiply(ratio));
        overlay.setBackground(new Background((new BackgroundFill(Color.web(hexCode), null, null))));
        return overlay;
    }

    /**
     * Update the background image of the player board according to its color and state
     */
    private void updateBoardBackground() {
        if (boardColor == null) {
            return;
        }
        // Compute the needed background size for the board asset
        BackgroundSize boardSize = new BackgroundSize(100, 100, true, true, true, false);
        Image boardImage = new Image(String.format("/images/playerboards/%s_%s.png", boardColor.name().toLowerCase(), boardState));
        BackgroundImage boardBackground = new BackgroundImage(boardImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, boardSize);
        // Set the background of the board container
        playerBoardGrid.setBackground(new Background(boardBackground));
    }

    /**
     * Sets the state of the player board
     *
     * @param boardState The string representing the state
     */
    public void setBoardState(String boardState) {
        boolean stateChanged = this.boardState == null || !this.boardState.equalsIgnoreCase(boardState);
        this.boardState = boardState.toLowerCase();
        if (stateChanged) {
            updateBoardBackground();
        }
    }

    /**
     * Sets the color of the board
     *
     * @param boardColor The object representing the color
     */
    public void setBoardColor(PlayerColor boardColor) {
        boolean colorChanged = this.boardColor == null || !this.boardColor.equals(boardColor);
        this.boardColor = boardColor;
        if (colorChanged) {
            updateBoardBackground();
        }
    }

    /**
     * Sets the damage tokens belonging to the player board
     *
     * @param damageTokens The list of objects representing the color of the shooter
     */
    public void setDamageTokens(List<ColoredObject> damageTokens) {
        // First remove all the tokens
        damageTokensContainer.getChildren().clear();
        double[] cellRatios = stateCellsMap.get(boardState);
        // Then for each token in the list create a new overlay and insert it into the container
        for (int i = 0; i < damageTokens.size(); i++) {
            // First we create an overlay for the token
            Pane tokenOverlay = createTokenOverlay(cellRatios[i] / 100, damageTokensContainer, damageTokens.get(i).getHexCode());
            tokenOverlay.getStyleClass().add("glossy");
            tokenOverlay.setOpacity(0.75);
            GridPane.setColumnIndex(tokenOverlay, i);
            damageTokensContainer.getChildren().add(tokenOverlay);
        }
    }

    /**
     * Sets the name of the player owning the player board
     *
     * @param name The name of the player
     */
    public void setPlayerName(String name) {
        playerName.setValue(name);
    }

    /**
     * Set the amount of deaths the player already experienced
     *
     * @param deaths The number of deaths
     */
    public void setPlayerDeaths(int deaths) {
        playerDeaths.setValue(deaths);
    }

    /**
     * Sets the amount of marks present on the player board
     *
     * @param marks The number of marks
     */
    public void setPlayerMarks(int marks) {
        playerMarks.setValue(marks);
    }
}
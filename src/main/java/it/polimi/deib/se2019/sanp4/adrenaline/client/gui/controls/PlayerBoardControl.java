package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom control representing the player board, showing the damages track and the count of marks
 */
public class PlayerBoardControl extends GridPane {

    private static final double[] FRENZY_CELLS = {8.27, 8.23, 25, 8.25, 8.21, 8.23, 8.23, 8.36, 8.20, 8.26, 9.18, 8.30};
    private static final double[] REGULAR_CELLS = {8.02, 8.75, 8.48, 8.08, 8.72, 8.51, 8.08, 8.14, 8.05, 8.69, 8.57, 7.90};

    private static final double[] ROWS = {34, 32, 34};
    private static final double[] MIDDLE_ROW_COLUMNS = {9, 67, 24};

    private static final double BOARD_RATIO = 1121.0 / 274.0;

    private final Map<String, double[]> stateCellsMap;

    private String boardState = "regular";
    private PlayerColor boardColor;

    // TODO: Show count of deaths

    /**
     * The grid-pane containing the damage tokens of the player board
     */
    private GridPane damageTokensContainer;

    public PlayerBoardControl() {
        super();
        super.setGridLinesVisible(true);

        stateCellsMap = new HashMap<>();
        stateCellsMap.put("regular", REGULAR_CELLS);
        stateCellsMap.put("frenzy", FRENZY_CELLS);

        // First create the layout adding the three rows
        GUIRenderer.setRowConstraints(this, ROWS);
        int i = 0;
        for (double rowHeight : ROWS) {
            GridPane row = new GridPane();
            row.prefHeightProperty().bind(this.heightProperty().multiply(rowHeight / 100));
            row.prefWidthProperty().bind(this.widthProperty());
            GridPane.setRowIndex(row, i);
            this.getChildren().add(row);
            i++;
        }
        GridPane middleRow = (GridPane) this.getChildren().get(2);

        damageTokensContainer = new GridPane();
        GridPane.setColumnIndex(damageTokensContainer, 1);
        damageTokensContainer.prefWidthProperty().bind(middleRow.widthProperty().multiply(MIDDLE_ROW_COLUMNS[1] / 100));
        damageTokensContainer.prefHeightProperty().bind(middleRow.heightProperty());
        middleRow.getChildren().add(damageTokensContainer);

        // Then set the columns of the tokens container
        GUIRenderer.setColumnConstraints(middleRow, MIDDLE_ROW_COLUMNS);
        GUIRenderer.setColumnConstraints(damageTokensContainer, stateCellsMap.get(boardState));

        this.widthProperty().addListener((ob, oldval, newVal) -> {
            this.setMinHeight(newVal.doubleValue() / BOARD_RATIO);
            this.setMaxHeight(newVal.doubleValue() / BOARD_RATIO);
        });
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
        this.setBackground(new Background(boardBackground));
    }

    /**
     * Sets the state of the player board
     *
     * @param boardState The string representing the state
     */
    public void setBoardState(String boardState) {
        this.boardState = boardState.toLowerCase();
        updateBoardBackground();
    }

    /**
     * Sets the color of the board
     *
     * @param boardColor The object representing the color
     */
    public void setBoardColor(PlayerColor boardColor) {
        this.boardColor = boardColor;
        updateBoardBackground();
    }

    /**
     * Sets the damage tokens belonging to the player board
     *
     * @param damageTokens The list of objects representing the color of the shooter
     */
    public void setDamageTokens(List<ColoredObject> damageTokens) {
        // First remove all the tokens
        damageTokensContainer.getChildren().clear();
        int i = 0;
        double[] cellRatios = stateCellsMap.get(boardState);
        // Then for each token in the list create a new overlay and insert it into the container
        for (ColoredObject token : damageTokens) {
            // First we create an overlay for the token
            Pane tokenOverlay = createTokenOverlay(cellRatios[i] / 100, damageTokensContainer, token.getHexCode());
            tokenOverlay.getStyleClass().add("glossy-pane");
            tokenOverlay.setOpacity(0.75);
            GridPane.setColumnIndex(tokenOverlay, i);
            damageTokensContainer.getChildren().add(tokenOverlay);
            i++;
        }
    }
}
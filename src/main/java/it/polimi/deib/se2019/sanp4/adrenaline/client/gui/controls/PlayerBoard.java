package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * A custom control representing the player board, showing the damages track and the count of marks
 */
public class PlayerBoard extends GridPane {

    private static final double[] TOKENS_CELLS = {/* TODO: add real values*/};
    private static final double[] POINTS_INDICATORS_CELLS = {/* TODO: add real values*/};
    private static final double[] ROWS = {/* TODO: add real values*/};
    private static final double[] TOP_ROW_COLUMNS = {/* TODO: add real values*/};
    private static final double[] MIDDLE_ROW_COLUMNS = {/* TODO: add real values*/};
    private static final double[] BOTTOM_ROW_COLUMNS = {/* TODO: add real values*/};

    /**
     * The grid-pane containing the damage tokens of the player board
     */
    private GridPane damageTokensContainer;
    /**
     * The grid-pane containing the indicators for the current point bonus
     */
    private GridPane pointsIndicatorsContainer;

    public PlayerBoard() {
        super();
        super.setGridLinesVisible(true);
        // First create the layout adding the three rows
        GUIRenderer.setRowConstraints(this, ROWS);
        int i = 0;
        for (double rowHeight : ROWS) {
            GridPane row = new GridPane();
            row.prefHeightProperty().bind(this.heightProperty().multiply(rowHeight / 100));
            row.setBackground(new Background((new BackgroundFill(Color.web(AmmoCube.values()[i].getHexCode()), null, null))));
            row.prefWidthProperty().bind(this.widthProperty());
            GridPane.setRowIndex(row, i);
            this.getChildren().add(row);
            i++;
        }
        GridPane topRow = (GridPane) this.getChildren().get(1);
        GridPane middleRow = (GridPane) this.getChildren().get(2);
        GridPane bottomRow = (GridPane) this.getChildren().get(3);

        damageTokensContainer = new GridPane();
        GridPane.setColumnIndex(damageTokensContainer, 1);
        damageTokensContainer.prefWidthProperty().bind(middleRow.widthProperty().multiply(MIDDLE_ROW_COLUMNS[1] / 100));
        damageTokensContainer.prefHeightProperty().bind(middleRow.heightProperty());
        middleRow.getChildren().add(damageTokensContainer);

        pointsIndicatorsContainer = new GridPane();
        GridPane.setColumnIndex(pointsIndicatorsContainer, 1);
        pointsIndicatorsContainer.prefWidthProperty().bind(bottomRow.widthProperty().multiply(BOTTOM_ROW_COLUMNS[1] / 100));
        pointsIndicatorsContainer.prefHeightProperty().bind(bottomRow.prefHeightProperty());
        bottomRow.getChildren().add(pointsIndicatorsContainer);

        // Then set the columns of the tokens container
        GUIRenderer.setColumnConstraints(topRow, TOP_ROW_COLUMNS);
        GUIRenderer.setColumnConstraints(middleRow, MIDDLE_ROW_COLUMNS);
        GUIRenderer.setColumnConstraints(bottomRow, BOTTOM_ROW_COLUMNS);
        GUIRenderer.setColumnConstraints(damageTokensContainer, TOKENS_CELLS);
        GUIRenderer.setColumnConstraints(pointsIndicatorsContainer, POINTS_INDICATORS_CELLS);
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
     * Sets the count of marks present in the player board
     *
     * @param marks The count of marks
     */
    public void setMarks(int marks) {
        //TODO: Implement this method
    }

    /**
     * Sets the visual indicator for the current maximum points provided by the board,
     * according to how many times the player has already been killed
     *
     * @param deaths The count of deaths of the player
     */
    public void setMaximumPoints(int deaths) {
        // Elements are covered unless only one element remains free
        // We hence consider the difference between total columns and count of deaths
        // And put covers accordingly
        pointsIndicatorsContainer.getChildren().clear();
        int occupiedCells = POINTS_INDICATORS_CELLS.length - deaths < 1 ? POINTS_INDICATORS_CELLS.length - 1 : deaths;
        for (int i = 0; i < occupiedCells; i++) {
            // Create black covers to hide the unavailable points
            Pane pointOverlay = createTokenOverlay(POINTS_INDICATORS_CELLS[i] / 100, pointsIndicatorsContainer, "#000000");
            GridPane.setColumnIndex(pointOverlay, i);
            pointsIndicatorsContainer.getChildren().add(pointOverlay);
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
        int i = 0;
        // Then for each token in the list create a new overlay and insert it into the container
        for (ColoredObject token : damageTokens) {
            // First we create an overlay for the token
            Pane tokenOverlay = createTokenOverlay(TOKENS_CELLS[i] / 100, damageTokensContainer, token.getHexCode());
            GridPane.setColumnIndex(tokenOverlay, i);
            damageTokensContainer.getChildren().add(tokenOverlay);
            i++;
        }
    }
}
package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Map;

/**
 * An abstract class describing an overlay representing a square of the game board
 */
public abstract class SquareOverlay extends SelectableOverlay<CoordPair> {

    /**
     * The columns the square layout is composed of
     */
    private static final int SQUARE_COLS = 3;

    SquareOverlay(CoordPair location) {
        super("/fxml/controls/squareOverlay.fxml");
        this.setData(location);
    }

    /**
     * Fill the provided square content with the provided players
     *
     * @param players       The map associating each player to its character color
     * @param squareContent The pane in which to put the content into
     */
    void fillPlayers(Map<String, ColoredObject> players, GridPane squareContent) {
        int i = 0;
        boolean contentAlreadyPresent = !squareContent.getChildren().isEmpty();
        for (Map.Entry<String, ColoredObject> entry : players.entrySet()) {
            // Create a new pawn for the player, filling it with its color
            Circle playerPawn = new Circle();
            playerPawn.radiusProperty().bind(squareContent.widthProperty().subtract(24).divide(12));
            // Set the color of the pawn
            playerPawn.setFill(Color.web(entry.getValue().getHexCode()));
            // And add a black border around it
            playerPawn.setStroke(Color.BLACK);
            playerPawn.setStrokeWidth(4);
            // Add a glossy effect
            playerPawn.getStyleClass().addAll("glossy", "shadowed");
            // Then set its row and column indexes
            GridPane.setColumnIndex(playerPawn, i % SQUARE_COLS);
            GridPane.setRowIndex(playerPawn, (i / SQUARE_COLS) + (contentAlreadyPresent ? 1 : 0));
            // And add to the pane
            squareContent.getChildren().add(playerPawn);
            i++;
        }
    }

    /**
     * Updates the rendered content of the square overlay, filling it with the provided players
     *
     * @param players The map associating each player inside the square to its character color
     */
    public abstract void updateContent(Map<String, ColoredObject> players);
}

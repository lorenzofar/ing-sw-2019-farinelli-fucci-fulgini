package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * A control representing the killshots track, providing methods to show current amount of remaining skulls
 * Available skulls are those that are visible, unavailable ones are removed from the left,
 * in order to make the flaming skull the last one
 */
public class KillshotsTrack extends GridPane {

    private static final double[] COLUMNS = {13.73, 8.12, 8.23, 8.18, 8.10, 8.26, 8.18, 8.13, 8.55, 20.52};

    private int totalSkulls;

    public KillshotsTrack() {
        super();
        super.setGridLinesVisible(true);

        GUIRenderer.setColumnConstraints(this, COLUMNS);
    }

    /**
     * Fills the cells of the track according to the number of taken skulls
     *
     * @param takenSkulls The number of taken skulls
     */
    private void fillCells(int takenSkulls) {
        this.getChildren().clear();
        // We first consider the difference between the number of total skulls and the number of cells
        // We subtract 2 not to consider the left and right padding
        int delta = COLUMNS.length - 2 - totalSkulls;
        // Then for each of the skulls to cover:
        for (int i = 0; i < takenSkulls + delta; i++) {
            Pane skullOverlay = new Pane();
            skullOverlay.prefWidthProperty().bind(this.widthProperty().multiply(COLUMNS[i]));
            skullOverlay.prefHeightProperty().bind(this.heightProperty());
            GridPane.setColumnIndex(skullOverlay, i);
            skullOverlay.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
            this.getChildren().add(skullOverlay);
        }
    }

    /**
     * Sets the skulls present in the killshots track
     *
     * @param totalSkulls The inital count of skulls
     * @param takenSkulls The amount of skulls taken from the track
     */
    public void setSkulls(int totalSkulls, int takenSkulls) {
        this.totalSkulls = totalSkulls;
        fillCells(takenSkulls);
    }
}
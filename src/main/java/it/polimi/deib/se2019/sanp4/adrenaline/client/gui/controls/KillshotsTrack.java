package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * A control representing the killshots track, providing methods to show current amount of remaining skulls
 * Available skulls are those that are visible, unavailable ones are removed from the left,
 * in order to make the flaming skull the last one
 */
public class KillshotsTrack extends GridPane {

    private static final double[] COLUMNS = {/*TODO: Add columns percentages*/};


    private int totalSkulls;

    public KillshotsTrack(int totalSkulls) {
        super();
        super.setGridLinesVisible(true);
        this.totalSkulls = totalSkulls;

        // Create all the columns
        for (double column : COLUMNS) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(column);
            super.getColumnConstraints().add(columnConstraints);
        }
        // And then fill unavailable cells
        fillCells(0);
    }

    /**
     * Fills the cells of the track according to the number of taken skulls
     *
     * @param takenSkulls The number of taken skulls
     */
    private void fillCells(int takenSkulls) {
        this.getChildren().clear();
        // We first consider the difference between the number of total skulls and the number of cells
        int delta = COLUMNS.length - totalSkulls;
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
     * Sets the amount of skulls taken from the killshots track
     *
     * @param takenSkulls The number of taken skulls
     */
    public void setTakenSkulls(int takenSkulls) {
        fillCells(takenSkulls);
    }
}
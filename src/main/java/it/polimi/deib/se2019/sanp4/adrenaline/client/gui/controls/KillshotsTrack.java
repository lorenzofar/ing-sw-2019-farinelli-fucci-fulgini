package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;

/**
 * A control representing the killshots track, providing methods to show current amount of remaining skulls
 * Available skulls are those that are visible, unavailable ones are removed from the left,
 * in order to make the flaming skull the last one
 */
public class KillshotsTrack extends GridPane {

    private static final double[] COLUMNS = {12.5, 12.5, 12.5, 12.5, 12.5, 12.5, 12.5, 12.5};

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
    private void fillCells(List<ColoredObject> takenSkulls) {
        this.getChildren().clear();
        // We first consider the difference between the number of total skulls and the number of cells
        int delta = COLUMNS.length - totalSkulls;
        // Then for each of the skulls to cover:
        for (int i = 0; i < takenSkulls.size() + delta; i++) {
            Pane skullOverlay = new Pane();
            skullOverlay.prefWidthProperty().bind(this.widthProperty().multiply(COLUMNS[i]));
            skullOverlay.prefHeightProperty().bind(this.heightProperty());
            GridPane.setColumnIndex(skullOverlay, i);
            // Set the color of the skull overlay
            // If the skull is covered because it is unavailable, its color will be black
            // Otherwise it will take the color of the player
            Paint skullColor = i < delta ? Color.BLACK : Color.web(takenSkulls.get(i - delta).getHexCode());
            skullOverlay.setBackground(new Background(new BackgroundFill(skullColor, null, null)));
            skullOverlay.setOpacity(0.75);
            this.getChildren().add(skullOverlay);
        }
    }

    /**
     * Sets the skulls present in the killshots track
     *
     * @param totalSkulls The initial count of skulls
     * @param takenSkulls The list of skulls taken from the track, where every entry represent the color of the player performing it
     */
    public void setSkulls(int totalSkulls, List<ColoredObject> takenSkulls) {
        this.totalSkulls = totalSkulls;
        fillCells(takenSkulls);
    }
}
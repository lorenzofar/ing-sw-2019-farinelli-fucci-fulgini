package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * A class describing an overlay used to select the initial count of skulls
 * It is based on HoverableOverlay to fire events when it is hovered
 */
public class SkullSelectionOverlay extends HoverableOverlay<Integer> {

    private int count;

    @FXML
    private Button overlayRoot;

    public SkullSelectionOverlay(int count) {
        super("/fxml/controls/SkullSelectionOverlay.fxml");
        this.count = count;
    }

    @FXML
    public void initialize() {
        super.setHoverableRoot(overlayRoot);
        // Set the skull image as the graphic of the button
        Image skullImage = new Image("/assets/items/skull.jpg");
        overlayRoot.setGraphic(new ImageView(skullImage));
        //TODO: Add listener for when the style class property changes to change image
    }

    public int getCount() {
        return this.count;
    }
}

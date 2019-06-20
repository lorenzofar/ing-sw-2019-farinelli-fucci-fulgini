package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

/**
 * An overlay used to represent and select an integer
 */
public class IntegerSelectionOverlay extends SelectableOverlay<Integer> {

    @FXML
    private Button overlayRoot;

    @FXML
    private Label label;

    public IntegerSelectionOverlay() {
        super("/fxml/controls/labelSelectionOverlay.fxml");
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
        overlayRoot.getStyleClass().add("integer-selection-overlay");
    }

    public void setInteger(Integer integer) {
        setData(integer != null ? integer : 0);
        label.setText(integer != null ? integer.toString() : "0");
        overlayRoot.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        overlayRoot.setGraphic(label);
    }
}

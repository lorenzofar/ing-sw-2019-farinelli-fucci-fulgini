package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

/**
 * An overlay used to select an integer value, showing it in a square container

 * @author Lorenzo Farinelli
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

    /**
     * Sets the integer value associated to the overlay.
     * if a null value is provided the overlay is associated to the value 0, to be used when the user can choose
     * not to select anything
     *
     * @param integer The value of the integer
     */
    public void setInteger(Integer integer) {
        setData(integer != null ? integer : 0);
        label.setText(integer != null ? integer.toString() : "0");
        overlayRoot.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        overlayRoot.setGraphic(label);
    }
}

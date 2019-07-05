package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerOperationEnum;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

/**
 * An overlay used to represent and select an operation the player can perform
 *
 * @author Lorenzo Farinelli
 */
public class PlayerOperationOverlay extends SelectableOverlay<PlayerOperationEnum> {

    @FXML
    private Button overlayRoot;

    @FXML
    private Label label;

    public PlayerOperationOverlay() {
        super("/fxml/controls/labelSelectionOverlay.fxml");
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
        overlayRoot.getStyleClass().add("player-operation-overlay");
    }

    /**
     * Sets the player operation associated to the overlay.
     * If a null object is provided, the overlay shows a "do nothing" message, to be used when an operation is optional
     *
     * @param operation The object representing the operation
     */
    public void setOperation(PlayerOperationEnum operation) {
        setData(operation);
        // If a null operation is provided, it means the user can choose to do nothing
        label.setText(operation != null ? operation.getMessage() : "Do nothing");
        overlayRoot.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        overlayRoot.setGraphic(label);
    }
}

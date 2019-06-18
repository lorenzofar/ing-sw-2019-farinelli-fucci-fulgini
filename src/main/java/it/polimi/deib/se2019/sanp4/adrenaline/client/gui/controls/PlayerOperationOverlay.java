package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerOperationEnum;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

/**
 * An overlay used to represent and select an operation the player can perform
 */
public class PlayerOperationOverlay extends SelectableOverlay<PlayerOperationEnum> {

    @FXML
    private Button overlayRoot;

    @FXML
    private Label operationLabel;

    PlayerOperationOverlay() {
        super("/fxml/controls/playerOperationOverlay.fxml");
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
    }

    public void setOperation(PlayerOperationEnum operation) {
        setData(operation);
        // If a null operation is provided, it means the user can choose to do nothing
        operationLabel.setText(operation != null ? operation.getMessage() : "Do nothing");
        overlayRoot.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        overlayRoot.setGraphic(operationLabel);
    }
}

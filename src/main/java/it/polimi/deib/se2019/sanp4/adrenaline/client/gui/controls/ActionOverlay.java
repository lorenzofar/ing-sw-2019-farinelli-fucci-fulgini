package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

/**
 * An overlay used to represent and select an action the player can perform
 */
public class ActionOverlay extends SelectableOverlay<ActionEnum> {

    @FXML
    private Button overlayRoot;
    @FXML
    private Label label;

    public ActionOverlay() {
        super("/fxml/controls/labelSelectionOverlay.fxml");
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
        overlayRoot.getStyleClass().add("player-operation-overlay");
    }

    public void setAction(ActionEnum action) {
        setData(action);
        label.setText(action != null ? action.toString() : "No action");
        overlayRoot.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        overlayRoot.setGraphic(label);
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

/**
 * An overlay ised to represent and select an action the player can perform
 */
public class ActionOverlay extends SelectableOverlay<ActionEnum> {

    @FXML
    private Button overlayRoot;
    @FXML
    private Label actionLabel;

    ActionOverlay() {
        super("/fxml/controls/actionOverlay.fxml");
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
    }

    public void setAction(ActionEnum action) {
        setData(action);
        actionLabel.setText(action != null ? action.toString() : "No action");
        overlayRoot.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        overlayRoot.setGraphic(actionLabel);
    }
}

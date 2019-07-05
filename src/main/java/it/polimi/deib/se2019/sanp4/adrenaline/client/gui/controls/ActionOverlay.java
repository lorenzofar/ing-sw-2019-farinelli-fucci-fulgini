package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

/**
 * An overlay used to represent and select an action the player can perform
 *
 * @author Lorenzo Farinelli
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

    /**
     * Sets the action associated to the overlay
     * If a null action is provided, the overlay shows a "no action" message, to be used when an action is optional
     *
     * @param action The object representing the action
     */
    public void setAction(ActionEnum action) {
        setData(action);
        label.setText(action != null ? action.toString() : "No action");
        overlayRoot.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        overlayRoot.setGraphic(label);
    }
}

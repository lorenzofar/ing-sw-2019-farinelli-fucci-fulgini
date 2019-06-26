package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.PlayerOperationOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerOperationEnum;
import javafx.scene.layout.GridPane;

import java.util.List;

public class PlayerOperationRequestController extends RequestController<PlayerOperationEnum> {
    /**
     * Retrieves the number of columns the grid is divided into
     *
     * @return The number of columns
     */
    @Override
    public int getColumnsCount() {
        return 1;
    }

    /**
     * Create the overlays of the needed type according the list of choices provided by the request
     *
     * @param request The object representing the request
     */
    @Override
    public void createOverlays(ChoiceRequest<PlayerOperationEnum> request) {
        int i = 0;
        // If the request is optional, we add a null choice
        List<PlayerOperationEnum> choices = request.getChoices();
        if (request.isOptional()) {
            choices.add(0, null);
        }
        for (PlayerOperationEnum operation : choices) {
            PlayerOperationOverlay overlay = new PlayerOperationOverlay();
            overlay.setOperation(operation);
            overlay.enable();
            GridPane.setColumnIndex(overlay, i % getColumnsCount());
            GridPane.setRowIndex(overlay, i / getColumnsCount());
            overlays.add(overlay);
            i++;
        }
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.ActionOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import javafx.scene.layout.GridPane;

import java.util.List;

/**
 * A specialized request controller used to handle action requests.
 * The window will show a vertical list of buttons representing the available actions the user can select.
 *
 * @author Lorenzo Farinelli
 */
public class ActionRequestController extends RequestController<ActionEnum> {
    /**
     * Retrieves the number of columns the grid is divided into
     *
     * @return The number of columns
     */
    @Override
    int getColumnsCount() {
        return 1;
    }

    /**
     * Create the overlays of the needed type according the list of choices provided by the request
     *
     * @param request The object representing the request
     */
    @Override
    void createOverlays(ChoiceRequest<ActionEnum> request) {
        int i = 0;
        // If the request is optional, we add a null choice
        List<ActionEnum> choices = request.getChoices();
        if (request.isOptional()) {
            choices.add(0, null);
        }
        for (ActionEnum action : choices) {
            ActionOverlay overlay = new ActionOverlay();
            overlay.setAction(action);
            overlay.enable();
            GridPane.setColumnIndex(overlay, i % getColumnsCount());
            GridPane.setRowIndex(overlay, i / getColumnsCount());
            overlays.add(overlay);
            i++;
        }
    }
}

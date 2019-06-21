package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.IntegerSelectionOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import javafx.scene.layout.GridPane;

import java.util.List;

public class SkullsRequestController extends RequestController<Integer> {

    /**
     * Retrieves the number of columns the grid is divided into
     *
     * @return The number of columns
     */
    @Override
    public int getColumnsCount() {
        return 8;
    }

    /**
     * Create the overlays of the needed type according the list of choices provided by the request
     *
     * @param request The object representing the request
     */
    @Override
    public void createOverlays(ChoiceRequest<Integer> request) {
        int i = 0;
        // If the request is optional, we add a null choice
        List<Integer> choices = request.getChoices();
        if (request.isOptional()) {
            choices.add(0, 0);
        }
        for (Integer n : choices) {
            IntegerSelectionOverlay overlay = new IntegerSelectionOverlay();
            overlay.setInteger(n);
            overlay.enable();
            GridPane.setColumnIndex(overlay, i % getColumnsCount());
            GridPane.setRowIndex(overlay, i / getColumnsCount());
            overlays.add(overlay);
            i++;
        }
    }
}
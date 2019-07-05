package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.BoardSelectionOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import javafx.scene.layout.GridPane;

/**
 * A specialized request controller used to handle board requests.
 * The window will show a grid with the images of the boards the user can select
 *
 * @author Lorenzo Farinelli
 */
public class BoardRequestController extends RequestController<Integer> {

    /**
     * Retrieves the number of columns the grid is divided into
     *
     * @return The number of columns
     */
    @Override
    int getColumnsCount() {
        return 2;
    }

    /**
     * Create the overlays of the needed type according the list of choices provided by the request
     *
     * @param request The object representing the request
     */
    @Override
    void createOverlays(ChoiceRequest<Integer> request) {
        request.getChoices().forEach(i -> {
            BoardSelectionOverlay overlay = new BoardSelectionOverlay();
            overlay.setBoardId(i);
            overlay.enable();
            overlays.add(overlay);
            GridPane.setColumnIndex(overlay, i % getColumnsCount());
            GridPane.setRowIndex(overlay, i / getColumnsCount());
        });
    }
}
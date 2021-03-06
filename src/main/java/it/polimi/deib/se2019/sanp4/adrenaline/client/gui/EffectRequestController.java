package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.EffectSelectionOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.EffectDescription;
import javafx.scene.layout.GridPane;

import java.util.List;

/**
 * A specialized request controller used to handle weapon effect requests.
 * The window will show a vertical track of the weapon effects the user can select
 *
 * @author Lorenzo Farinelli
 */
public class EffectRequestController extends RequestController<EffectDescription> {
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
    void createOverlays(ChoiceRequest<EffectDescription> request) {
        int i = 0;
        List<EffectDescription> choices = request.getChoices();
        if (request.isOptional()) {
            choices.add(null);
        }
        for (EffectDescription effect : choices) {
            EffectSelectionOverlay overlay = new EffectSelectionOverlay();
            overlay.setEffect(effect);
            overlay.enable();
            GridPane.setColumnIndex(overlay, i % getColumnsCount());
            GridPane.setRowIndex(overlay, i / getColumnsCount());
            overlays.add(overlay);
            i++;
        }
    }
}

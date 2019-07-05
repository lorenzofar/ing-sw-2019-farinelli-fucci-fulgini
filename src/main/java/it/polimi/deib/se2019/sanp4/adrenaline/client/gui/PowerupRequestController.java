package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.PowerupOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import javafx.scene.layout.GridPane;

import java.util.List;

/**
 * A specialized request controller used to handle powerup requests.
 * The window will show an horizontal track of the powerup cards the user can select
 *
 * @author Lorenzo Farinelli
 */
public class PowerupRequestController extends RequestController<PowerupCard> {

    /**
     * Retrieves the number of columns the grid is divided into
     *
     * @return The number of columns
     */
    @Override
    public int getColumnsCount() {
        return 4;
    }

    /**
     * Create the overlays of the needed type according the list of choices provided by the request
     *
     * @param request The object representing the request
     */
    @Override
    public void createOverlays(ChoiceRequest<PowerupCard> request) {
        int i = 0;
        // If the request is optional, we add a null choice
        List<PowerupCard> choices = request.getChoices();
        if (request.isOptional()) {
            choices.add(null);
        }
        for (PowerupCard powerup : choices) {
            PowerupOverlay overlay = new PowerupOverlay();
            overlay.setPowerupCard(powerup);
            overlay.enable();
            GridPane.setColumnIndex(overlay, i % getColumnsCount());
            GridPane.setRowIndex(overlay, i / getColumnsCount());
            overlays.add(overlay);
            i++;
        }
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.WeaponCardOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import javafx.scene.layout.GridPane;

import java.util.List;

/**
 * A specialized request controller used to handle weapon requests.
 * The window will show an horizontal track of the weapon cards the user can select
 */
public class WeaponCardRequestController extends RequestController<WeaponCard> {

    /**
     * Retrieves the number of columns the grid is divided into
     *
     * @return The number of columns
     */
    @Override
    int getColumnsCount() {
        return 4;
    }

    /**
     * Create the overlays of the needed type according the list of choices provided by the request
     *
     * @param request The object representing the request
     */
    @Override
    void createOverlays(ChoiceRequest<WeaponCard> request) {
        int i = 0;
        // If the request is optional we add a null choice
        List<WeaponCard> choices = request.getChoices();
        if (request.isOptional()) {
            choices.add(null);
        }
        for (WeaponCard weapon : choices) {
            WeaponCardOverlay overlay = new WeaponCardOverlay();
            overlay.setWeaponCard(weapon);
            overlay.enable();
            GridPane.setColumnIndex(overlay, i % getColumnsCount());
            GridPane.setRowIndex(overlay, i / getColumnsCount());
            overlays.add(overlay);
            i++;
        }
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.PlayerSelectionOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;
import javafx.scene.layout.GridPane;

import java.util.List;

/**
 * A specialized request controller used to handle player requests.
 * The window will show an horizontal track with the selectable players, showing their name and image
 *
 * @author Lorenzo Farinelli
 */
public class PlayerRequestController extends RequestController<String> {
    /**
     * Retrieves the number of columns the grid is divided into
     *
     * @return The number of columns
     */
    @Override
    int getColumnsCount() {
        return 5;
    }

    /**
     * Create the overlays of the needed type according the list of choices provided by the request
     *
     * @param request The object representing the request
     */
    @Override
    void createOverlays(ChoiceRequest<String> request) {
        int i = 0;
        List<String> choices = request.getChoices();
        if (request.isOptional()) {
            choices.add(null);
        }
        for (String playerName : choices) {
            PlayerSelectionOverlay overlay = new PlayerSelectionOverlay();
            if (playerName != null) {
                overlay.setPlayer(playerName, (PlayerColor) clientView.getModelManager().getPlayersColors().get(playerName));
            } else {
                overlay.setPlayer(playerName, null);
            }
            overlay.enable();
            GridPane.setColumnIndex(overlay, i % getColumnsCount());
            GridPane.setRowIndex(overlay, i / getColumnsCount());
            overlays.add(overlay);
            i++;
        }
    }
}

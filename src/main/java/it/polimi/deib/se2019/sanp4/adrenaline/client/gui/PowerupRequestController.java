package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.ObservableOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.PowerupOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;
import java.util.List;

public class PowerupRequestController extends GUIController implements RequestController<PowerupCard> {

    @FXML
    private GridPane powerupsContainer;

    @FXML
    private Label message;

    private List<ObservableOverlay> powerupsOverlays;

    private StringProperty messageProperty;

    @FXML
    public void initialize() {
        powerupsOverlays = new ArrayList<>();
        messageProperty = new SimpleStringProperty("");
        message.textProperty().bind(messageProperty);
    }

    /**
     * Prepares the window to perform the provided request
     * and populates the message and available choices
     *
     * @param request The object representing the request
     */
    @Override
    public void setup(ChoiceRequest<PowerupCard> request) {
        powerupsOverlays.forEach(ObservableOverlay::clearListeners);
        powerupsOverlays.clear();
        powerupsContainer.getChildren().clear();
        powerupsContainer.getRowConstraints().clear();


        // Build rows to put cards inside
        for (int i = 0; i < request.getChoices().size() / 3; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            powerupsContainer.getRowConstraints().add(rowConstraints);
        }

        int i = 0;
        messageProperty.set(request.getMessage());
        for (PowerupCard powerup : request.getChoices()) {
            PowerupOverlay overlay = new PowerupOverlay();
            overlay.setPowerupCard(powerup);
            overlay.enable();
            GridPane.setColumnIndex(overlay, i % 3);
            GridPane.setRowIndex(overlay, i / 3);
            powerupsOverlays.add(overlay);
            i++;
        }

        clientView.setSelectionHandler(
                new SelectionHandler<PowerupCard>(clientView, powerupsOverlays, overlay -> ((PowerupOverlay) overlay).getPowerupCard(), stage));

        powerupsContainer.getChildren().addAll(powerupsOverlays);
    }
}

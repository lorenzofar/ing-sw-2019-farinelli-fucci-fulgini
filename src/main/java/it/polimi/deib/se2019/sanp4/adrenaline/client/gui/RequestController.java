package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.ObservableOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.SelectableOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class RequestController<T extends Serializable> extends GUIController implements RequestControllerInterface<T> {

    @FXML
    protected Label message;

    @FXML
    private GridPane overlaysContainer;

    private StringProperty messageProperty;

    List<SelectableOverlay<T>> overlays;

    @FXML
    public void initialize() {
        overlays = new ArrayList<>();
        messageProperty = new SimpleStringProperty();
        if (message != null) {
            message.textProperty().bind(messageProperty);
        }
    }

    /**
     * Prepares the window to perform the provided request
     * and populates the message and available choices
     *
     * @param request The object representing the request
     */
    @Override
    public void setup(ChoiceRequest<T> request) {
        overlays.forEach(ObservableOverlay::clearListeners);
        overlays.clear();
        overlaysContainer.getChildren().clear();
        overlaysContainer.getRowConstraints().clear();


        // Build rows to put cards inside
        for (int i = 0; i < request.getChoices().size() / getColumnsCount(); i++) {
            RowConstraints rowConstraints = new RowConstraints();
            overlaysContainer.getRowConstraints().add(rowConstraints);
        }

        messageProperty.set(request.getMessage());

        createOverlays(request);

        overlaysContainer.getChildren().addAll(overlays);
        clientView.setSelectionHandler(new SelectionHandler<T>(overlays, stage)
        );
    }

    /**
     * Retrieves the number of columns the grid is divided into
     *
     * @return The number of columns
     */
    public abstract int getColumnsCount();

    /**
     * Create the overlays of the needed type according the list of choices provided by the request
     */
    public abstract void createOverlays(ChoiceRequest<T> request);
}

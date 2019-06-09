package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.ObservableOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.SkullSelectionOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ChoiceResponse;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class SkullsRequestController extends GUIController implements RequestController<Integer> {

    private List<SkullSelectionOverlay> skulls;

    private IntegerProperty selectedSkulls;

    @FXML
    public HBox skullsContainer;

    @FXML
    public Button submitBtn;

    @FXML
    public void initialize() {
        skulls = new ArrayList<>();
        selectedSkulls = new SimpleIntegerProperty(0);
        submitBtn.disableProperty().bind(selectedSkulls.isEqualTo(0));
    }

    private void onSkullHovered(ObservableOverlay overlay) {
        if (overlay.isHover()) {
            int i = ((SkullSelectionOverlay) overlay).getCount();
            selectedSkulls.setValue(i);
            skulls.forEach(s -> s.getStyleClass().remove("focused"));
            skulls.stream().filter(s -> s.getCount() <= i).forEach(s -> s.getStyleClass().add("focused"));
        }
    }

    /**
     * Prepares the window to perform the provided request
     * and populates the message and available choices
     *
     * @param request The object representing the request
     */
    @Override
    public void setup(ChoiceRequest<Integer> request) {
        skulls.clear();
        for (int i = 0; i < request.getChoices().size(); i++) {
            skulls.add(new SkullSelectionOverlay(i + 1));
        }
        skullsContainer.getChildren().clear();
        skullsContainer.getChildren().addAll(skulls);
        skulls.forEach(skull -> {
            skull.enable();
            skull.addListener(this::onSkullHovered);
        });
    }

    public void submitSkulls() {
        // Create the response according to the selected count of skulls
        ChoiceResponse<Integer> skullsCountResponse = new ChoiceResponse<>(clientView.getUsername(), clientView.getCurrentRequest().getUuid(), selectedSkulls.get());
        clientView.notifyObservers(skullsCountResponse);
        clientView.onRequestCompleted();
    }
}
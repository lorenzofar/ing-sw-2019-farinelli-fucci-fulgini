package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.BoardSelectionOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.ObservableOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;
import java.util.List;

public class BoardRequestController extends GUIController implements RequestController<Integer> {
    @FXML
    public GridPane boardsContainer;

    private List<ObservableOverlay> boardSelectionOverlays;

    @FXML
    public void initialize() {
        boardSelectionOverlays = new ArrayList<>();
    }

    /**
     * Prepares the window to perform the provided request
     * and populates the message and available choices
     *
     * @param request The object representing the request
     */
    @Override
    public void setup(ChoiceRequest<Integer> request) {
        boardSelectionOverlays.forEach(ObservableOverlay::clearListeners);
        boardSelectionOverlays.clear();
        boardsContainer.getChildren().clear();
        boardsContainer.getRowConstraints().clear();

        for (int i = 0; i < request.getChoices().size() / 2; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            boardsContainer.getRowConstraints().add(rowConstraints);
        }

        request.getChoices().forEach(i -> {
            BoardSelectionOverlay overlay = new BoardSelectionOverlay();
            overlay.setBoardId(i);
            overlay.enable();
            boardSelectionOverlays.add(overlay);
            GridPane.setColumnIndex(overlay, i % 2);
            GridPane.setRowIndex(overlay, i / 2);
        });

        clientView.setSelectionHandler(new SelectionHandler<Integer>(clientView, boardSelectionOverlays, overlay -> ((BoardSelectionOverlay) overlay).getBoardId(), stage));

        boardsContainer.getChildren().addAll(boardSelectionOverlays);
    }
}
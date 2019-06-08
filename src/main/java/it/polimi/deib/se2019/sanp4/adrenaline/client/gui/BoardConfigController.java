package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.BoardSelectionOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.ObservableOverlay;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;
import java.util.List;

public class BoardConfigController extends GUIController {
    @FXML
    public GridPane boardsContainer;

    private List<BoardSelectionOverlay> boardSelectionOverlays;

    @FXML
    public void initialize() {
        boardSelectionOverlays = new ArrayList<>();
    }

    void setBoards(List<Integer> boards) {
        SelectionHandler<Integer> selectionHandler = new SelectionHandler<>(clientView, overlay -> ((BoardSelectionOverlay) overlay).getBoardId());
        boardSelectionOverlays.forEach(ObservableOverlay::clearListeners);
        boardSelectionOverlays.clear();
        boardsContainer.getChildren().clear();
        boardsContainer.getRowConstraints().clear();

        for (int i = 0; i < boards.size() / 2; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            boardsContainer.getRowConstraints().add(rowConstraints);
        }

        boards.forEach(i -> {
            BoardSelectionOverlay overlay = new BoardSelectionOverlay();
            overlay.setBoardId(i);
            overlay.addListener(selectionHandler);
            overlay.setSelectable(true);
            boardSelectionOverlays.add(overlay);
            GridPane.setColumnIndex(overlay, i % 2);
            GridPane.setRowIndex(overlay, i / 2);
        });
        boardsContainer.getChildren().addAll(boardSelectionOverlays);
    }
}

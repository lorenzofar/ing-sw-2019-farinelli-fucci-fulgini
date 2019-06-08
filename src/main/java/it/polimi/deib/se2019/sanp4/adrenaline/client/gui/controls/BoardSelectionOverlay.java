package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * A class describing an overlay used to select the board configuration
 */
public class BoardSelectionOverlay extends SelectableOverlay {

    private int boardId;

    @FXML
    private Button overlayRoot;

    public BoardSelectionOverlay() {
        super("/fxml/controls/BoardSelectionOverlay.fxml");
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
        // Set the board image as the graphic of the button
        Image boardImage = new Image(String.format("/assets/boards/board_%d.png", boardId), 200, 200, true, true);
        overlayRoot.setGraphic(new ImageView(boardImage));
    }

    public int getBoardId() {
        return boardId;
    }


}

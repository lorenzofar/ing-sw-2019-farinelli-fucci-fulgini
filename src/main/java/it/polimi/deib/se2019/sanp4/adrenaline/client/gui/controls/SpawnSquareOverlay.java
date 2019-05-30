package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SpawnSquareOverlay extends SquareOverlay {

    @FXML
    private Button overlayRoot;

    public SpawnSquareOverlay() {
        super("/fxml/controls/spawnSquareOverlay.fxml");
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
    }
}

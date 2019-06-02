package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * A class describing an overlay representing an ammo square
 */
public class AmmoSquareOverlay extends SquareOverlay {

    @FXML
    private Button overlayRoot;

    public AmmoSquareOverlay(CoordPair location) {
        super("/fxml/controls/ammoSquareOverlay.fxml", location);
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
    }
}

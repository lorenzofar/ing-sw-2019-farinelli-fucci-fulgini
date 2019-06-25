package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.Map;

/**
 * A class describing an overlay representing a spawn square
 */
public class SpawnSquareOverlay extends SquareOverlay {

    @FXML
    private Button overlayRoot;

    @FXML
    private GridPane squareContent;

    SpawnSquareOverlay(CoordPair location) {
        super(location);
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
    }

    @Override
    public void updateContent(Map<String, ColoredObject> players) {
        squareContent.getChildren().clear();
        fillPlayers(players, squareContent);
        overlayRoot.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        overlayRoot.setGraphic(squareContent);
        overlayRoot.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
    }
}

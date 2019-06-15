package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class PlayerSelectionOverlay extends SelectableOverlay<String> {

    @FXML
    private Circle playerPic;

    @FXML
    private Label playerName;

    @FXML
    private Button overlayRoot;

    @FXML
    private VBox buttonGraphics;

    public PlayerSelectionOverlay() {
        super("/fxml/controls/playerSelectionOverlay.fxml");
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
    }

    public void setPlayer(String playerName, PlayerColor playerColor) {
        this.setData(playerName);
        this.playerName.textProperty().set(playerName);
        // Load the asset and set as image source
        // TODO: Handle null player for optional requests
        Image characterImage = new Image(
                String.format("/images/players/%s.png", playerColor.name().toLowerCase()),
                150, 150, true, true);
        playerPic.setFill(new ImagePattern(characterImage));
        overlayRoot.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        overlayRoot.setGraphic(buttonGraphics);
    }
}

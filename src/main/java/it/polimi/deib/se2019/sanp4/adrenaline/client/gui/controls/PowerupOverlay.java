package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PowerupOverlay extends SelectableOverlay {

    private PowerupCard powerupCard;

    @FXML
    private Button overlayRoot;

    public PowerupOverlay() {
        super("/fxml/controls/powerupOverlay.fxml");
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
    }

    public PowerupCard getPowerupCard() {
        return powerupCard;
    }

    public void setPowerupCard(PowerupCard powerupCard) {
        this.powerupCard = powerupCard;
        String filePath = String.format("/assets/powerups/%s_%s.png",
                powerupCard.getType().name().toLowerCase(),
                powerupCard.getCubeColor().name().substring(0, 1).toLowerCase());
        Image powerupImage = new javafx.scene.image.Image(filePath, 200, 200, true, true);
        overlayRoot.setGraphic(new ImageView(powerupImage));
    }
}

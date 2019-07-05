package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * An overlay used to select a powerup, showing the graphical representation of its card
 *
 * @author Lorenzo Farinelli
 */
public class PowerupOverlay extends SelectableOverlay<PowerupCard> {

    @FXML
    private Button overlayRoot;

    public PowerupOverlay() {
        super("/fxml/controls/cardOverlay.fxml");
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
    }

    /**
     * Sets the powerup card associated to the overlay.
     * If a null object is provided, an empty card is shown, to be used when the used can choose not to select any card
     *
     * @param powerupCard The object representing the powerup card
     */
    public void setPowerupCard(PowerupCard powerupCard) {
        this.setData(powerupCard);
        String filePath = String.format("/images/powerups/%s", powerupCard == null ?
                "null_powerup.png" :
                String.format("%s_%s.png",
                        powerupCard.getType().name().toLowerCase(),
                        powerupCard.getCubeColor().name().toLowerCase()));
        Image powerupImage = new javafx.scene.image.Image(filePath, 200, 200, true, true);
        overlayRoot.setGraphic(new ImageView(powerupImage));
    }
}

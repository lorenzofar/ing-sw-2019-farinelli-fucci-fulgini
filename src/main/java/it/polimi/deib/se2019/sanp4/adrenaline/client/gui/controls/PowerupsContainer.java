package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import java.util.List;

/**
 * A custom control extending an HBox container that contains images of powerup cards
 */
public class PowerupsContainer extends HBox {

    private static final double POWERUP_RATIO = 169.0 / 264.0;

    public PowerupsContainer() {
        super();
        this.setSpacing(8);
        this.setAlignment(Pos.BOTTOM_LEFT);
    }

    /**
     * Sets the powerup cards that have to be shown
     *
     * @param powerups The list of objects representing the powerup cards
     */
    public void setPowerups(List<PowerupCard> powerups) {
        // First we remove all the children
        this.getChildren().clear();
        // Then for each of the powerups we create an image, binding its height to the height of the parent
        powerups.forEach(powerup -> {
            OrientableImage powerupImage = new OrientableImage();
            powerupImage.setImage(
                    String.format("/images/powerups/%s_%s.png",
                            powerup.getType().name().toLowerCase(),
                            powerup.getCubeColor().name().toLowerCase()));
            powerupImage.prefHeightProperty().bind(this.heightProperty().multiply(0.9));
            powerupImage.prefWidthProperty().bind(this.heightProperty().multiply(POWERUP_RATIO));
            this.getChildren().add(powerupImage);
        });
    }
}

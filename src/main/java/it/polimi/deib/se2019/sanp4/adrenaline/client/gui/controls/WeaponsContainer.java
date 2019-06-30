package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.function.Consumer;

/**
 * A custom control extending an HBox container that contains images of weapon cards
 */
public class WeaponsContainer extends HBox {

    /**
     * The aspect ratio of weapon card images
     */
    private static final double WEAPON_RATIO = 240.0 / 406.0;

    /**
     * A consumer that accepts the id of the weapon and is invoked each time a weapon card is clicked
     */
    private Consumer<String> weaponsConsumer;

    public WeaponsContainer() {
        super();
        this.setSpacing(4);
        this.setAlignment(Pos.BOTTOM_LEFT);
    }

    /**
     * Sets the weapon cards that have to be shown
     *
     * @param weapons The list of objects representing the weapon cards
     */
    public void setWeapons(List<WeaponCard> weapons) {
        // First we remove all the children
        this.getChildren().clear();
        // Then for each of the weapon cards we create an image, binding its height to the height of the parent
        weapons.forEach(weapon -> {
            // We first create a container to put the information into
            VBox weaponBox = new VBox(8);
            weaponBox.setAlignment(Pos.CENTER);
            weaponBox.prefHeightProperty().bind(this.heightProperty());
            weaponBox.prefWidthProperty().bind(this.heightProperty().multiply(WEAPON_RATIO));
            // We then create an image containing the weapon card
            OrientableImage weaponImage = new OrientableImage();
            weaponImage.setImage(
                    String.format("/images/weapons/%s.png",
                            weapon.getId()));
            weaponImage.prefHeightProperty().bind(weaponBox.heightProperty().multiply(0.9));
            weaponImage.prefWidthProperty().bind(weaponBox.heightProperty().multiply(WEAPON_RATIO));
            // We create a colored rectangle to show the usability status of the weapon
            Rectangle weaponUsabilityIndicator = new Rectangle();
            weaponUsabilityIndicator.widthProperty().bind(this.heightProperty().multiply(WEAPON_RATIO).subtract(24));
            weaponUsabilityIndicator.setHeight(5);
            weaponUsabilityIndicator.setFill(Color.web(weapon.getState().isUsable() ? GUIRenderer.HEX_GREEN : GUIRenderer.HEX_RED));
            weaponUsabilityIndicator.getStyleClass().add("shadowed");
            weaponBox.getChildren().add(weaponImage);
            weaponBox.getChildren().add(weaponUsabilityIndicator);
            // We then attach the weapon consumer
            if (weaponsConsumer != null) {
                weaponImage.setOnMouseClicked(action -> weaponsConsumer.accept(weapon.getId()));
            }
            this.getChildren().add(weaponBox);
        });
    }

    /**
     * Sets the weapons consumer used by the control
     *
     * @param weaponsConsumer The consumer of weapon IDs
     */
    public void setWeaponsConsumer(Consumer<String> weaponsConsumer) {
        this.weaponsConsumer = weaponsConsumer;
    }
}

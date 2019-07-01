package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * An overlay used to select a weapon, showing the graphical representation of its card
 */
public class WeaponCardOverlay extends SelectableOverlay<WeaponCard> {

    @FXML
    private Button overlayRoot;

    public WeaponCardOverlay() {
        super("/fxml/controls/cardOverlay.fxml");
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
    }

    /**
     * Sets the weapon card associated to the overlay.
     * If a null object is provided, an empty card is shown, to be used when the user can choose not to select any card
     *
     * @param weaponCard The object representing the weapon card
     */
    public void setWeaponCard(WeaponCard weaponCard) {
        this.setData(weaponCard);
        String filePath = String.format("/images/weapons/%s", weaponCard == null ? "null_weapon.png" : String.format("%s.png", weaponCard.getId().toLowerCase()));
        Image weaponImage = new javafx.scene.image.Image(filePath, 200, 200, true, true);
        overlayRoot.setGraphic(new ImageView(weaponImage));
    }
}

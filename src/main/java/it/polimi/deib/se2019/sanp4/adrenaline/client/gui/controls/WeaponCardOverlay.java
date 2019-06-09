package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

    public void setWeaponCard(WeaponCard weaponCard) {
        this.setData(weaponCard);
        String filePath = String.format("/assets/weapons/%s.png", weaponCard.getId().toLowerCase());
        Image powerupImage = new javafx.scene.image.Image(filePath, 200, 200, true, true);
        overlayRoot.setGraphic(new ImageView(powerupImage));
    }
}

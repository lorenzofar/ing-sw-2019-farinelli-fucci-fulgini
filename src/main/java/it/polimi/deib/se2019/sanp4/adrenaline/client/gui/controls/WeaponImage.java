package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCreator;
import javafx.scene.control.Tooltip;

import java.io.IOException;

public class WeaponImage extends OrientableImage {

    public void setWeapon(String weaponId) {
        super.setImage(String.format("/images/weapons/%s.png", weaponId));
        // Then create a tooltip to show the name of the card when hovering
        try {
            Tooltip weaponIdTooltip = new Tooltip(WeaponCreator.createWeaponCard(weaponId).getName());
            Tooltip.install(this, weaponIdTooltip);
        } catch (
                IOException ignore) {
            // If the card does not exist we ignore errors and not attach a tooltip
        }
    }
}

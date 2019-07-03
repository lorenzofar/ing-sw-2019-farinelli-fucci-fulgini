package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCreator;
import javafx.application.Platform;
import javafx.scene.control.Tooltip;

import java.io.IOException;

/**
 * A specialized {@link OrientableImage} that shows the card image of a weapon, along with a tooltip
 * telling its name. If a {@code null} object is provided, it will show an empty card and an empty tooltip
 */
public class WeaponImage extends OrientableImage {

    /**
     * Sets the weapon whose card has to be shown
     *
     * @param weaponId The identifier of the weapons
     */
    public void setWeapon(String weaponId) {
        Platform.runLater(() -> {
            super.setImage(String.format("/images/weapons/%s.png", weaponId == null ? "null_weapon" : weaponId));
            // Then create a tooltip to show the name of the card when hovering
            String tooltipContent = "";
            if (weaponId != null) {
                try {
                    tooltipContent = WeaponCreator.createWeaponCard(weaponId).getName();
                } catch (IOException ignore) {
                    // If the card does not exist we ignore errors and attach an empty tooltip
                }
            }
            Tooltip weaponIdTooltip = new Tooltip(tooltipContent);
            Tooltip.install(this, weaponIdTooltip);
        });
    }
}

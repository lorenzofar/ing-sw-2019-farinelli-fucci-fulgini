package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;

public class WeaponImage extends OrientableImage {

    public void setWeapon(WeaponCard weaponCard) {
        super.setImage(String.format("/assets/weapons/%s.png", weaponCard.getId()));
    }
}

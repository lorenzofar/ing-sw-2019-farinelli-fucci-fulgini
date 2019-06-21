package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

public class WeaponImage extends OrientableImage {

    public void setWeapon(String weaponId) {
        super.setImage(String.format("/images/weapons/%s.png", weaponId));
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;

public class AmmoSquare extends Square {

    private AmmoCard ammoCard;
    private int id;

    AmmoSquare(int id){
        this.id = id;
    }

    public AmmoCard grabAmmo() {
        return null;
    }
    public void insertAmmo(AmmoCard ammo){
        this.ammoCard = ammo;
    }
}

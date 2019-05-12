package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;

/** A specialized class representing a square containing ammo cards */
public class AmmoSquare extends Square {

    /** The ammo card contained in the square */
    private AmmoCard ammoCard;

    /** Default constructor only to be used by Jackson */
    protected AmmoSquare(){
        super();
        this.ammoCard = null;
    }

    /**
     * Creates a new ammo square at the specified location
     * @param location The cartesian coordinates of the location
     */
    AmmoSquare(CoordPair location){
        super(location);
        this.ammoCard = null;
    }



    /**
     * Takes the ammo card that is currently placed in the square
     * @return The object representing the ammo card
     * @throws IllegalStateException if there is no ammo on the square
     */
    public AmmoCard grabAmmo() {
        if(this.ammoCard != null) {
            AmmoCard picked = this.ammoCard;
            this.ammoCard = null;
            return picked;
        } else {
            throw new IllegalStateException("Currently no ammo on this square");
        }
    }

    public AmmoCard getAmmoCard() {
        return ammoCard;
    }

    /**
     * Puts an ammo card on the square
     * @param ammo The object representing the ammo card, not null
     */
    public void insertAmmo(AmmoCard ammo){
        if(ammo == null){
            throw new NullPointerException("Ammo cannot be null");
        }
        this.ammoCard = ammo;
    }
}

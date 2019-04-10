package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;

/** A specialized class representing a square containing ammo cards */
public class AmmoSquare extends Square {

    /** A unique identifier of the square */
    private int id;

    /** The ammo card contained in the square */
    private AmmoCard ammoCard;

    /**
     * Creates a new ammo square at the specified location
     * @param id The unique id of the square
     * @param location The cartesian coordinates of the location
     */
    AmmoSquare(int id, CoordPair location){
        super(location);
        this.id = id;
        this.ammoCard = null; //TODO: determine whether to insert the ammo card when constructing the square or in another time
    }

    /**
     * Retrieves the identifier of the square
     * @return The id of the square
     */
    public int getId() {
        return id;
    }

    /**
     * Takes the ammo card that is currently placed in the square
     * @return The object representing the ammo card
     */
    public AmmoCard grabAmmo() {
        //FIXME: Internal ammo card reference should be set to null once the ammo is grabbed
        return this.ammoCard;
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

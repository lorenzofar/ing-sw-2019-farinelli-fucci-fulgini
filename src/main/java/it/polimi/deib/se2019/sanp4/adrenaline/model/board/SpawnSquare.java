package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.Weapon;

import java.util.ArrayList;
import java.util.List;

/** A specialized class representing a square available as spawn points and that contain weapon cards*/
public class SpawnSquare extends Square {

    /** A unique identifier of the square */
    private int id;

    /** The list of weapons contained in the square */
    private List<Weapon> weapons;

    /**
     * Creates a new spawn square at the specified location
     * @param id The unique id of the square
     * @param location The cartesian coordinates of the location
     */
    SpawnSquare(int id, CoordPair location){
        super(location);
        this.id = id;
        this.weapons = new ArrayList<>(3);
    }

    /**
     * Retrieves the identifier of the square
     * @return The id of the square
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves all the weapons contained in the square
     * @return A list of objects representing the weapons
     */
    public List<Weapon> getWeapons() {
        return new ArrayList<>(this.weapons);
    }

    /**
     * Takes a weapon from the square
     * @param index The index of the weapon
     * @return The object representing the weapon
     */
    public Weapon grabWeapon(int index) {
        return this.weapons.get(index);
    }

    /**
     * Puts a weapon on the square
     * @param weapon The object representing the weapon
     */
    public void insertWeapon(Weapon weapon){
        //TODO: Implement this method
        this.weapons.add(weapon);
    }
}

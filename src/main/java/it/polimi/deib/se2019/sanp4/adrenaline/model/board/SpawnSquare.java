package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.Weapon;

import java.util.ArrayList;
import java.util.List;

/** A specialized class representing a square available as spawn points and that contain weapon cards*/
public class SpawnSquare extends Square {

    /** The list of weapons contained in the square */
    private List<Weapon> weapons;

    /**
     * Creates a new spawn square at the specified location
     * @param location The cartesian coordinates of the location
     */
    SpawnSquare(CoordPair location){
        super(location);
        this.weapons = new ArrayList<>(3);
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
     * @param index The index of the weapon, must be positive and not outside the weapons' list capacity
     * @return The object representing the weapon
     */
    public Weapon grabWeapon(int index) {
        if(index < 0){
            throw new IllegalArgumentException("Index must be positive");
        }
        if(index >= weapons.size()){
            throw new IndexOutOfBoundsException("Index is outside the bounds of the weapon collection");
        }

        return this.weapons.get(index);
    }

    /**
     * Puts a weapon on the square
     * @param weapon The object representing the weapon
     */
    public void insertWeapon(Weapon weapon){
        if(weapon == null){
            throw new NullPointerException("Weapon cannot be null");
        }
        //TODO: Implement this method
        this.weapons.add(weapon);
    }
}

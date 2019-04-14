package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** A specialized class representing a square available as spawn points and that contain weapon cards*/
public class SpawnSquare extends Square {

    private static final int MAX_WEAPON_CARDS = 3;


    /** The list of weapons contained in the square */
    private List<WeaponCard> weapons;

    /**
     * Creates a new spawn square at the specified location
     * @param location The cartesian coordinates of the location
     */
    SpawnSquare(CoordPair location){
        super(location);
        this.weapons = new ArrayList<>(3);
    }

    /**
     * Retrieves all the weapon cards contained in the square
     * @return A list of objects representing the weapon cards
     */
    public List<WeaponCard> getWeaponCards() {
        return new ArrayList<>(this.weapons);
    }

    /**
     * Takes a weapon from the square, if it is available
     * @param weaponId identifier of the weapon you want to grab
     * @return The object representing the weapon
     * @throws CardNotFoundException if the requested card is not in available in this square
     */
    public WeaponCard grabWeaponCard(String weaponId) throws CardNotFoundException {
        if (weaponId == null) {
            throw new NullPointerException("Weapon id cannot be null");
        }

        /* Get the card from the collection, if it exists */
        Optional<WeaponCard> card = weapons.stream().filter(w -> w.getId().equals(weaponId)).findFirst();
        /* If it does not exist, signal it */
        if (!card.isPresent()) {
            throw new CardNotFoundException("Cannot grab card " + weaponId);
        }
        /* If it exists, remove it and return it */
        weapons.remove(card.get());
        return card.get();
    }

    /**
     * Puts a weapon on the square, if it is full
     * @param weapon The object representing the weapon
     * @throws FullCapacityException If the square contains the maximum number of weapons
     */
    public void insertWeaponCard(WeaponCard weapon) throws FullCapacityException{
        if(weapon == null) {
            throw new NullPointerException("Weapon cannot be null");
        }
        if(weapons.size() >= MAX_WEAPON_CARDS){
            throw new FullCapacityException(MAX_WEAPON_CARDS);
        }
        this.weapons.add(weapon);
    }
}

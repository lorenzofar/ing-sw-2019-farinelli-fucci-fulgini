package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.server.ServerProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** A specialized class representing a square available as spawn points and that contain weapon cards*/
public class SpawnSquare extends Square {

    /**
     * Load the maximum number of weapons a spawn square can hold
     * Fall back to a default value of 3 weapons if none is set
     */
    public static final int MAX_WEAPON_CARDS = (int) ServerProperties.getProperties()
            .getOrDefault("adrenaline.maxspawnweapons", 3);

    /** The list of weapon cards contained in the square */
    private List<WeaponCard> weaponCards;

    /** Default constructor only to be used by Jackson */
    protected SpawnSquare(){
        super();
        this.weaponCards = new ArrayList<>(MAX_WEAPON_CARDS);
    }

    /**
     * Creates a new spawn square at the specified location
     * @param location The cartesian coordinates of the location
     */
    public SpawnSquare(CoordPair location){
        super(location);
        this.weaponCards = new ArrayList<>(MAX_WEAPON_CARDS);
    }

    /**
     * Retrieves all the weapon cards contained in the square
     * @return An unmodifiable list of weapon cards
     */
    public List<WeaponCard> getWeaponCards() {
        return Collections.unmodifiableList(weaponCards);
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
        Optional<WeaponCard> card = weaponCards.stream().filter(w -> w.getId().equals(weaponId)).findFirst();
        /* If it does not exist, signal it */
        if (!card.isPresent()) {
            throw new CardNotFoundException("Cannot grab card " + weaponId);
        }
        /* If it exists, remove it and return it */
        weaponCards.remove(card.get());
        return card.get();
    }

    /**
     * Puts a weapon on the square, if it is not full
     * @param weapon The object representing the weapon
     * @throws FullCapacityException If the square contains the maximum number of weapons
     */
    public void insertWeaponCard(WeaponCard weapon) throws FullCapacityException{
        if(weapon == null) {
            throw new NullPointerException("Weapon cannot be null");
        }

        if(weaponCards.contains(weapon)){
            throw new IllegalStateException("Weapon is already on the square");
        }

        if(weaponCards.size() >= MAX_WEAPON_CARDS){
            throw new FullCapacityException(MAX_WEAPON_CARDS);
        }
        this.weaponCards.add(weapon);
    }
}

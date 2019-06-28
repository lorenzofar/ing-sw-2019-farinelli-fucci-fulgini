package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.SpawnSquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.SquareUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.CardStack;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A specialized class representing a square available as spawn points and that contain weapon cards
 */
public class SpawnSquare extends Square {

    /**
     * Load the maximum number of weapons a spawn square can hold
     * Fall back to a default value of 3 weapons if none is set
     */
    public static final int MAX_WEAPON_CARDS = 3;

    /**
     * The list of weapon cards contained in the square
     */
    private List<WeaponCard> weaponCards;

    /**
     * Default constructor only to be used by Jackson
     */
    protected SpawnSquare() {
        super();
        this.weaponCards = new ArrayList<>(MAX_WEAPON_CARDS);
    }

    /**
     * Creates a new spawn square at the specified location
     *
     * @param location The cartesian coordinates of the location
     */
    public SpawnSquare(CoordPair location) {
        super(location);
        this.weaponCards = new ArrayList<>(MAX_WEAPON_CARDS);
    }

    /**
     * Retrieves all the weapon cards contained in the square
     *
     * @return An unmodifiable list of weapon cards
     */
    public List<WeaponCard> getWeaponCards() {
        return new ArrayList<>(weaponCards);
    }

    /**
     * Takes a weapon from the square, if it is available
     *
     * @param weaponId identifier of the weapon you want to grab
     * @return The object representing the weapon
     * @throws CardNotFoundException if the requested card is not in available in this square
     */
    public WeaponCard grabWeaponCard(String weaponId) {
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
        this.notifyObservers(new SquareUpdate(this.generateView()));
        return card.get();
    }

    /**
     * Puts a weapon on the square, if it is not full
     *
     * @param weapon The object representing the weapon
     * @throws FullCapacityException If the square contains the maximum number of weapons
     */
    public void insertWeaponCard(WeaponCard weapon) throws FullCapacityException {
        if (weapon == null) {
            throw new NullPointerException("Weapon cannot be null");
        }

        if (weaponCards.contains(weapon)) {
            throw new IllegalStateException("Weapon is already on the square");
        }

        if (isFull()) {
            throw new FullCapacityException(MAX_WEAPON_CARDS);
        }
        this.weaponCards.add(weapon);
        this.notifyObservers(new SquareUpdate(this.generateView()));
    }

    /**
     * Checks whether this square has the maximum number of weapon cards
     *
     * @return {@code true} if the maximum number of weapon cards is reached, {@code false} if there are empty spaces
     */
    public boolean isFull() {
        return weaponCards.size() >= MAX_WEAPON_CARDS;
    }

    /**
     * Refills the weapon cards in the square from the weapon card stack in the match.
     * If the square is already full, nothing happens
     *
     * @param match instance of the match this square belongs to, not null
     */
    @Override
    public void refill(Match match) {
        /* Get the stack of weapon cards */
        CardStack<WeaponCard> stack = match.getWeaponStack();

        /* Draw the cards until the square is full */
        try {
            while (!isFull()) {
                // We bypass the check for duplicates:
                // if the card is already on this square, it is certainly not in the stack
                weaponCards.add(stack.draw());
            }
        } catch (EmptyStackException e) {
            /* No more weapons to draw, totally fine */
        }

        this.notifyObservers(new SquareUpdate(this.generateView()));
    }

    /**
     * Accepts to be visited by a {@link SquareVisitor}.
     *
     * @param visitor The visitor who wants to visit this square
     */
    @Override
    public void accept(SquareVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Generates the {@link SpawnSquareView} of the spawn square.
     *
     * @return the spawn square view.
     */
    @Override
    public SpawnSquareView generateView() {
        /* Assign a random color if room is null in test cases */
        RoomColor color = getRoom() == null ? RoomColor.BLUE : getRoom().getColor();
        SpawnSquareView view = new SpawnSquareView(this.getLocation(), color);
        view.setPlayers(this.getPlayers()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toSet()));
        /* From AdjacentMap, create a Map in which the entry is the same and the value is the
        SquareConnectionType contained by the SquareConnection */
        Map<CardinalDirection, SquareConnectionType> adjacentMap =
                getAdjacentSquares().entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getConnectionType()));
        view.setAdjacentMap(adjacentMap);
        /* Create a list of ids of the weapon cards contained in it */
        view.setWeapons(weaponCards.stream().map(WeaponCard::getId).collect(Collectors.toList()));
        return view;
    }
}

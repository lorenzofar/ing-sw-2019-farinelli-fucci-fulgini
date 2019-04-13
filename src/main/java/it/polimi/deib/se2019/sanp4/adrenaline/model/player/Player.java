package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.NotEnoughAmmoException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerUpCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;

import java.util.*;

/**
 * Represents a player of the match.
 */
public class Player{
    public static final int INITIAL_AMMO = 1;
    public static final int MAX_WEAPONS = 3;
    public static final int MAX_POWERUPS = 3;

    /** Nickname of the player, must be unique on the whole server */
    private final String name;

    /** The "points" the user has */
    private int score;

    /** Number of times this player overkilled someone else during the match */
    private int performedOverkills;

    /** Number of times this player performed a killshot (overkills are excluded) */
    private int performedKillshots;

    /** Shared object containing actions that the player can perform */
    private ActionCard actionCard;

    /** Board with damage and marks */
    private final PlayerBoard playerBoard;

    /** Weapon cards in player's hands */
    private List<WeaponCard> weapons;

    /** Powerup cards in player's hands */
    private List<PowerUpCard> powerups;

    /** Ammo cubes for each color */
    private Map<AmmoCube, Integer> ammo;

    /** Game character chosen by this player */
    private final PlayerCharacter character;

    /** Board square this player is currently in */
    private Square currentSquare;

    /** Player's operational state */
    private PlayerState state;

    /**
     * Constructs a player who is ready to play: sets counters to zero and sets initial ammo cubes.
     * @param name server-unique nickname
     * @param actionCard pre-built action card, may be shared with other players
     * @param character chosen game character
     */
    Player(String name, ActionCard actionCard, PlayerCharacter character){
        if (actionCard == null || character == null || name == null) {
            throw new NullPointerException("Found null parameters!");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty!");
        }

        this.name = name;
        this.actionCard = actionCard;
        this.character = character;

        /* Initialise counters */
        score = performedKillshots = performedOverkills = 0;

        /* Create empty player board */
        playerBoard = new PlayerBoard(this);

        /* Item lists */
        weapons = new ArrayList<>();
        powerups = new ArrayList<>();

        /* Ammo */
        ammo = new EnumMap<>(AmmoCube.class);
        ammo.replaceAll((color, amount) -> INITIAL_AMMO);

        /* Set default state */
        state = PlayerState.ONLINE;
    }

    /**
     * Converts a map containing generic cubes (gray) to a map containg only payable cubes
     * It takes the first available cube from the player to suffice for the generic cost
     * @param cubesList The list of objects representing the cubes to convert
     * @return The map representing the converted cubes
     * @throws NotEnoughAmmoException If player's ammo is not sufficient to cover the cost
     */
    private Map<AmmoCube, Integer> convertAmmoCubeCost(List<AmmoCubeCost> cubesList) throws NotEnoughAmmoException{
        // Load count of cubes to convert
        int genericCount = (int)cubesList.stream().filter(cube -> cube == AmmoCubeCost.ANY).count();
        Map<AmmoCube, Integer> convertedAmmo = new EnumMap<>(AmmoCube.class);

        cubesList.stream().filter(cube -> cube != AmmoCubeCost.ANY).forEach(cube -> {
            AmmoCube convertedCube = cube.getCorrespondingCube();
            Integer previousAmmo = convertedAmmo.get(convertedCube);
            convertedAmmo.put(convertedCube, previousAmmo == null ? 1 : previousAmmo + 1);
        });

        // If there are no cubes to convert, do nothing
        if(genericCount == 0){
            return convertedAmmo;
        }
        // Repeat this for all the cubes that need conversion:
        while(genericCount > 0) {
            /* Here we search for a cube in the player wallet
            We first filter out colors with no cubes,
            then we retrieve a cube from the remaining ones*/
            Optional<Map.Entry<AmmoCube, Integer>> availableAmmo = this.ammo.entrySet()
                    .stream()
                    .filter(entry -> {
                        Integer userAmmo = entry.getValue();
                        return userAmmo != null && userAmmo == 0;
                    })
                    .findAny();
            // We check whether that cube is really present, if not we throw an exception
            if(!availableAmmo.isPresent()){
                throw new NotEnoughAmmoException();
            }
            // The user can pay the cost, so we increase the corresponding counter
            convertedAmmo.put(availableAmmo.get().getKey(), availableAmmo.get().getValue() + 1);
            genericCount--;
        }
        // Here we succesfully converted the cubes and the player is able to pay the cost
        return convertedAmmo;
    }

    /**
     * Returns this player's name.
     * @return player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Add given points to player's score.
     * @param points points to be added to current score, must be positive
     * @throws IllegalArgumentException if points is negative
     */
    public synchronized void addScorePoints(int points){
        if (points < 0) {
            throw new IllegalArgumentException("Score cannot be incremented by negative amount");
        }
        score += points;
    }

    /**
     * Returns player's score
     * @return player's score points
     */
    public int getScore() {
        return score;
    }

    /**
     * Increments inflicted killshots by one.
     */
    public void addPerformedKillshot() {
        performedKillshots += 1;
    }

    /**
     * Returns number of killshots performed by this player.
     * @return number of killshots performed by this player
     */
    public int getPerformedKillshots() {
        return performedKillshots;
    }

    /**
     * Increments inflicted overkills by one.
     */
    public void addPerformedOverkill() {
        performedOverkills += 1;
    }

    /**
     * Returns number of overkills performed by this player.
     * @return number of overkills performed by this player
     */
    public int getPerformedOverkills() {
        return performedOverkills;
    }

    /**
     * Returns the current action card.
     * @return the action card (not null)
     */
    public ActionCard getActionCard() {
        return actionCard;
    }

    /**
     * Sets the action card.
     * @param card action card, not null
     * @throws NullPointerException if parameter is null
     */
    public void setActionCard(ActionCard card){
        if (card == null) {
            throw new NullPointerException("ActionCard cannot be null for player");
        }
        this.actionCard = card;
    }

    /**
     * Getter for {@link #playerBoard}.
     * @return player board
     */
    public PlayerBoard getPlayerBoard() {
        return playerBoard;
    }

    /**
     * Determines whether the player owns the specified weapon card
     * @param weaponId The identifier of the weapon card
     * @return {@code true} if the player owns the card, {@code false} otherwise
     */
    public boolean hasWeaponCard(String weaponId){
        return weapons.stream().map(WeaponCard::getId).anyMatch(id -> id.equals(weaponId));
    }

    /**
     * Add a weapon card in player's hands, also resets its state.
     * Also takes care of the fact that you cannot have more than more than {@link #MAX_WEAPONS} cards.
     * @param weapon weapon card that should be added, not null
     * @throws NullPointerException if weapon is null
     * @throws FullCapacityException if the weapon limit has been reached
     */
    public void addWeapon(WeaponCard weapon) throws FullCapacityException {
        if (weapon == null) {
            throw new NullPointerException("cannot add empty weapon to player");
        }

        if (weapons.size() >= MAX_WEAPONS) {
            throw new FullCapacityException(MAX_WEAPONS);
        }

        /*TODO: Reset weapon's state*/
        weapons.add(weapon);
    }

    /**
     * Removes a weapon card from player's hands and resets it.
     * @param weaponId identifier of the weapon, not null and not an empty string
     * @return weapon card drawn from player
     * @throws CardNotFoundException if the requested card is not in available in this square
     */
    public WeaponCard removeWeapon(String weaponId) throws CardNotFoundException {
        if (weaponId == null) {
            throw new NullPointerException("Weapon id cannot be null");
        }

        if(weapons.stream().map(WeaponCard::getId).noneMatch(id -> id.equals(weaponId))){
            throw new CardNotFoundException(String.format("The weapon \"%s\" does not belong to the user", weaponId));
        }
        /*TODO: Implement this method */
        return null;
    }

    /**
     * Removes a weapon card from player's hands and resets it.
     * @param weapon weapon card to be removed
     * @return weapon card drawn from player
     * @throws CardNotFoundException if the requested card is not in available in this square
     */
    public WeaponCard removeWeapon(WeaponCard weapon) throws CardNotFoundException {
        if(weapon == null){
            throw new NullPointerException("Weapon cannot be null");
        }

        if(!(weapons.contains(weapon))){
            throw new CardNotFoundException(String.format("The weapon \"%s\" does not belong to the user", weapon.getId()));
        }
        weapons.remove(weapon);
        //TODO: Reset weapon's state
        return weapon;
    }

    /**
     * Adds powerup card to player's hands.
     * Also takes care of the fact that you cannot have more than more than {@link #MAX_POWERUPS} cards.
     * @param powerup powerup card to be added, not null
     * @throws FullCapacityException if the powerups limit has been reached
     */
    public void addPowerup(PowerUpCard powerup) throws FullCapacityException{
        if(powerup == null){
            throw new NullPointerException("Powerup card cannot be null");
        }
        if(powerups.size() >= MAX_POWERUPS){
            throw new FullCapacityException(MAX_POWERUPS);
        }
        powerups.add(powerup);
    }

    /**
     * Removes a powerup card from player's hands
     * @param powerup powerup card to be removed
     * @return removed powerup card
     * @throws IllegalStateException if the powerup does not belong to the user
     */
    public PowerUpCard removePowerup(PowerUpCard powerup) {
        if(powerup == null){
            throw new NullPointerException("Powerup card cannot be null");
        }
        if(this.powerups.isEmpty()){
            throw new IllegalStateException("User has no powerup cards");
        }
        if(!(powerups.contains(powerup))){
            throw new IllegalStateException("User does not have the powerup card");
        }
        powerups.remove(powerup);
        return powerup;
    }

    /**
     * Adds ammo cubes to the current player. If given ammo exceed capacity, they are simply discarded.
     * @param ammo a map containing the quantity of ammo cubes to add for each color, unspecified keys
     *             are treated as zero, not null and not containing negative values
     */
    public void addAmmo(Map<AmmoCube, Integer> ammo){
        if(ammo == null){
            throw new NullPointerException("Cubes map cannot be null");
        }
        if(ammo.entrySet().stream().anyMatch(entry -> entry.getValue() < 0)){
            throw new IllegalArgumentException("Cubes amounts cannot be negative");
        }
        ammo.forEach((key, value) -> {
            Integer playerAmmo = this.ammo.get(key);
            playerAmmo = playerAmmo == null ? 0 : playerAmmo;
            playerAmmo += value;
            //TODO: Check whether the ammo exceeds capacity
            this.ammo.put(key, playerAmmo);
        });
    }

    /**
     * Removes ammo cubes from current player.
     * @param ammo a map containing the quantity of ammo cubes to remove for each color, unspecified keys
     *             are treated as zero, not null and not containing negative values
     * @throws NotEnoughAmmoException if the player does not have enough ammo to pay the specified amount
     */
    public void payAmmo(Map<AmmoCube, Integer> ammo) throws NotEnoughAmmoException {
        if(ammo == null){
            throw new NullPointerException("Cubes map cannot be null");
        }
        if(ammo.entrySet().stream().anyMatch(entry -> entry.getValue() < 0)) {
            throw new IllegalArgumentException("Cubes amounts cannot be negative");
        }
        ammo.forEach((key, value) -> {
            Integer playerAmmo = this.ammo.get(key);
            playerAmmo = playerAmmo == null ? 0 : playerAmmo;
            playerAmmo -= value;
            if (playerAmmo < 0) {
                //TODO: Check how to throw the exception
            }
            this.ammo.put(key, playerAmmo);
        });
    }

    /**
     * Removes ammo cubes from current player.
     * @param ammo a map containing the quantity of ammo cubes to remove for each color, unspecified keys
     *             are treated as zero, not null and not containing negative values
     * @throws NotEnoughAmmoException if the player does not have enough ammo to pay the specified amount
     */
    public void payAmmo(List<AmmoCubeCost> ammo) throws NotEnoughAmmoException{
        payAmmo(convertAmmoCubeCost(ammo));
    }

    /**
     * Returns shared object representing the game character this player is associated to.
     * @return player's character
     */
    public PlayerCharacter getCharacter() {
        return character;
    }

    /**
     * Returns the square this player is actually in.
     * @return square the player is in, null if the player has not spawned yet
     */
    public Square getCurrentSquare() {
        return currentSquare;
    }

    /**
     * Sets the square this player is actually in.
     * @param currentSquare square where to set the player
     */
    public void setCurrentSquare(Square currentSquare) {
        if(currentSquare == null){
            throw new NullPointerException("Square cannot be null");
        }
        this.currentSquare = currentSquare;
    }

    /**
     * Returns player's operational state.
     * @return player's state
     */
    public PlayerState getState() {
        return state;
    }

    /**
     * Sets player's operational state.
     * @param state
     */
    public void setState(PlayerState state) {
        if(state == null){
            throw new NullPointerException("State cannot be null");
        }
        this.state = state;
    }
}
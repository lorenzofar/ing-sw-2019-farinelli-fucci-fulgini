package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.NotEnoughAmmoException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.common.AdrenalineProperties;

import java.util.*;

/**
 * Represents a player of the match.
 */
public class Player{

    /**
     * Load the inital cubes of each color a player has at the beginning of a match
     * Fall back to a default value of 1 cube per color if none is set
     */
    public static final int INITIAL_AMMO = (int) AdrenalineProperties.getProperties().getOrDefault("adrenaline.initialplayerammo", 1);

    /**
     * Load the maximum number of cubes for each color a player can have
     * Fall back to a default value of 3 cubes per color if none is set
     */
    public static final int MAX_AMMO_CUBES = (int) AdrenalineProperties.getProperties().getOrDefault("adrenaline.maxplayerammocubes", 3);

    /**
     * Load the maximum number of weapons cards a player can hold
     * Fall back to a default value of 3 weapon cards if none is set
     */
    public static final int MAX_WEAPONS = (int) AdrenalineProperties.getProperties().getOrDefault("adrenaline.maxplayerweapons", 3);

    /**
     * Load the maximum number of powerup cards a player can hold
     * Fall back to a default value of 3 powerup cards if none is set
     */
    public static final int MAX_POWERUPS = (int) AdrenalineProperties.getProperties().getOrDefault("adrenaline.maxplayerpowerups", 3);

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
    private List<PowerupCard> powerups;

    /** Ammo cubes for each color */
    private Map<AmmoCube, Integer> ammo;

    /** Color of the game character chosen by this player */
    private final PlayerColor color;

    /** Board square this player is currently in */
    private Square currentSquare;

    /** Player's operational state */
    private PlayerState state;

    /**
     * Constructs a player who is ready to play: sets counters to zero and sets initial ammo cubes.
     * @param name server-unique nickname
     * @param actionCard pre-built action card, may be shared with other players
     * @param color color of the chosen character
     */
    public Player(String name, ActionCard actionCard, PlayerColor color){
        if (actionCard == null || color == null || name == null) {
            throw new NullPointerException("Found null parameters!");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty!");
        }

        this.name = name;
        this.actionCard = actionCard;
        this.color = color;

        /* Initialise counters */
        score = performedKillshots = performedOverkills = 0;

        /* Create empty player board */
        playerBoard = new PlayerBoard(this);

        /* Item lists */
        weapons = new ArrayList<>();
        powerups = new ArrayList<>();

        /* Ammo */
        ammo = new EnumMap<>(AmmoCube.class);
        for(int i= 0; i<AmmoCube.values().length; i++){
            ammo.put(AmmoCube.values()[i], INITIAL_AMMO);
        }

        /* Set default state */
        state = PlayerState.ONLINE;

        /* Initially, the player is not on any square, hence it's null */
        currentSquare = null;
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

        cubesList.stream()
                .filter(cube -> cube != AmmoCubeCost.ANY)
                .forEach(cube ->
                    convertedAmmo.merge(cube.getCorrespondingCube(), 1, Integer::sum)
                );

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
                        Integer pendingAmmo = convertedAmmo.get(entry.getKey());
                        pendingAmmo = pendingAmmo != null ? pendingAmmo : 0;
                        // Here we check that the player has enough ammo to pay for the pending ammo and for the generic cube
                        return userAmmo != null && userAmmo != 0 && userAmmo - pendingAmmo > 0;
                    })
                    .findAny();
            // We check whether that cube is really present, if not we throw an exception
            if(!availableAmmo.isPresent()){
                throw new NotEnoughAmmoException();
            }
            // The user can pay the cost, so we increase the corresponding counter
            convertedAmmo.merge(availableAmmo.get().getKey(), 1, Integer::sum);
            genericCount--;
        }
        // Here we successfully converted the cubes and the player is able to pay the cost
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
     * Retrieves the weapon cards owned by the player
     * @return The list of objects representing the weapon cards
     */
    public List<WeaponCard> getWeapons(){
        return new ArrayList<>(weapons);
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

        if(hasWeaponCard(weapon.getId())){
            throw new IllegalStateException("The player already owns the card");
        }

        if (weapons.size() >= MAX_WEAPONS) {
            throw new FullCapacityException(MAX_WEAPONS);
        }
        weapon.getState().reset(weapon); // Resets weapon state
        weapons.add(weapon);
    }

    /**
     * Removes a weapon card from player's hands and resets it.
     * @param weaponId identifier of the weapon, not null and not an empty string
     * @return weapon card drawn from player
     * @throws CardNotFoundException if the requested card is not owned by the player
     */
    public WeaponCard removeWeapon(String weaponId) throws CardNotFoundException {
        if (weaponId == null) {
            throw new NullPointerException("Weapon id cannot be null");
        }

        Optional<WeaponCard> weaponCard = weapons.stream().filter(w -> w.getId().equals(weaponId)).findFirst();
        if(!weaponCard.isPresent()){
            throw new CardNotFoundException(String.format("The weapon \"%s\" does not belong to the user", weaponId));
        }

        weapons.remove(weaponCard.get());
        weaponCard.get().getState().reset(weaponCard.get()); // Resets the weapon card
        return weaponCard.get();
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
        weapon.getState().reset(weapon);
        return weapon;
    }

    /**
     * Retrieves the powerup cards owned by the player
     * @return The list of objects representing the powerup cards
     */
    public List<PowerupCard> getPowerups(){
        return new ArrayList<>(powerups);
    }

    /**
     * Adds powerup card to player's hands.
     * Also takes care of the fact that you cannot have more than more than {@link #MAX_POWERUPS} cards.
     * @param powerup powerup card to be added, not null
     * @throws FullCapacityException if the powerups limit has been reached
     */
    public void addPowerup(PowerupCard powerup) throws FullCapacityException{
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
    public PowerupCard removePowerup(PowerupCard powerup) {
        if(powerup == null){
            throw new NullPointerException("Powerup card cannot be null");
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
            playerAmmo = playerAmmo + value > MAX_AMMO_CUBES ? MAX_AMMO_CUBES : playerAmmo + value; // Check whether the final amount exceeds maximum capacity
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
        for (Map.Entry<AmmoCube, Integer> entry : ammo.entrySet()) {
            AmmoCube key = entry.getKey();
            Integer value = entry.getValue();
            Integer playerAmmo = this.ammo.get(key);
            playerAmmo = playerAmmo == null ? 0 : playerAmmo;
            if (playerAmmo - value < 0) {
                throw new NotEnoughAmmoException();
            }
            this.ammo.put(key, playerAmmo - value);
        }
    }

    /**
     * Removes ammo cubes from current player.
     * @param ammo a map containing the quantity of ammo cubes to remove for each color, unspecified keys
     *             are treated as zero, not null and not containing negative values
     * @throws NotEnoughAmmoException if the player does not have enough ammo to pay the specified amount
     */
    public void payAmmo(List<AmmoCubeCost> ammo) throws NotEnoughAmmoException{
        if(ammo == null){
            throw new NullPointerException("Cubes list cannot be null");
        }
        payAmmo(convertAmmoCubeCost(ammo));
    }

    /**
     * Retrieves the ammo cubes belonging to the player
     * @return A map containing the count of each ammo cube
     */
    public Map<AmmoCube, Integer> getAmmo(){
        return new EnumMap<>(ammo);
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
     * @param state The object representing the state
     */
    public void setState(PlayerState state) {
        if(state == null){
            throw new NullPointerException("State cannot be null");
        }
        this.state = state;
    }

    /**
     * Returns the color of the character chosen by the player
     * @return the color of the character chosen by the player
     */
    public PlayerColor getColor() {
        return color;
    }
}
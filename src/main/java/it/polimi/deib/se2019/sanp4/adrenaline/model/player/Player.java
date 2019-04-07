package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerUpCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.Weapon;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
    /*TODO: Substitute with WeaponCard */
    private List<Weapon> weapons;

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
     * Add a weapon card in player's hands, also resets its state.
     * Also takes care of the fact that you cannot have more than more than {@link #MAX_WEAPONS} cards.
     * @param weapon weapon card that should be added, not null
     * @throws NullPointerException if weapon is null
     * @throws FullCapacityException if the weapon limit has been reached
     */
    public void addWeapon(Weapon weapon) throws FullCapacityException {
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
     * @param weapon name of the weapon
     * @return weapon card drawn from player
     */
    public Weapon removeWeapon(String weapon) {
        /*TODO: Implement this method */
        return null;
    }

    /**
     * Removes a weapon card from player's hands and resets it.
     * @param weapon weapon card to be removed
     * @return weapon card drawn from player
     */
    public Weapon removeWeapon(Weapon weapon) {
        /*TODO: Implement this method */
        return null;
    }

    /**
     * Adds powerup card to player's hands.
     * Also takes care of the fact that you cannot have more than more than {@link #MAX_POWERUPS} cards.
     * @param powerup powerup card to be added
     */
    public void addPowerup(PowerUpCard powerup) {
        /*TODO: Implement this method */
    }

    /**
     * Removes a powerup card from player's hands
     * @param powerup powerup card to be removed
     * @return removed powerup card
     */
    public PowerUpCard removePowerup(PowerUpCard powerup) {
        /*TODO: Implement this method*/
        return null;
    }

    /**
     * Adds ammo cubes to the current player. If given ammo exceed capacity, they are simply discarded.
     * @param ammo a map containing the quantity of ammo cubes to add for each color, unspecified keys
     *             are treated as zero
     */
    public void addAmmo(Map<AmmoCube, Integer> ammo){
        /*TODO: Implement this method*/
    }

    /**
     * Removes ammo cubes from current player.
     * @param ammo a map containing the quantity of ammo cubes to remove for each color, unspecified keys
     *             are treated as zero
     */
    public void payAmmo(Map<AmmoCube, Integer> ammo) {
        /*TODO: Implement this method*/
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
        this.state = state;
    }
}
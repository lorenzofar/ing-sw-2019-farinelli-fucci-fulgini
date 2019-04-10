package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerUpCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.Weapon;

import java.util.Collections;
import java.util.List;

/**
 * Hold the representation of the match.
 * It contains information about:
 * <ul>
 *     <li>The current turn and the player which is performing it</li>
 *     <li>The game board</li>
 *     <li>The stacks of cards players can draw, namely:
 *      <ul>
 *          <li>Ammo tiles</li>
 *          <li>Weapon cards</li>
 *          <li>Powerup cards</li>
 *      </ul>
 *     </li>
 *     <li>The number of remaining skulls in the killshots track</li>
 *     <li>Whether the match is in frenzy mode or not</li>
 * </ul>
 */
public class Match {

    /** The turn of the player which is currently playing */
    private PlayerTurn currentTurn;

    /** The game board */
    private Board board;

    /** Stack of all the ammo tiles */
    private CardStack<AmmoCard> ammoStack;

    /** Stack of all the weapon cards */
    private CardStack<WeaponCard> weaponStack;

    /** Stack of all the powerup cards */
    private CardStack<PowerUpCard> powerupStack;

    /** Number of remaining skulls in the killshots track */
    private int skulls;

    /** A flag indicating whether the match is in frenzy mode or not */
    private boolean frenzy;


    /**
     * Creates a new match for the provided players.
     * It initializes the card stacks using the provided ones.
     * It also sets the number of skulls according to the corresponding parameter.
     * @param players The players playing the match, not null
     * @param ammoStack The stack of ammo tiles, not null
     * @param weaponStack The stack of weapon cards, not null
     * @param powerupStack The stack of powerup cards, not null
     * @param skulls The number of skulls in the killshots track, must be positive
     */
    Match(List<Player> players, CardStack<AmmoCard> ammoStack, CardStack<WeaponCard> weaponStack, CardStack<PowerUpCard> powerupStack, int skulls){
        if(players == null || ammoStack == null || weaponStack == null || powerupStack == null){
            throw new NullPointerException("Found null parameters");
        }
        if(skulls < 0){
            throw new IllegalArgumentException("Skulls count cannot be negative");
        }
        this.ammoStack = ammoStack;
        this.weaponStack = weaponStack;
        this.powerupStack = powerupStack;
        this.skulls = skulls;
        //TODO: Create players according to provided usernames
    }

    /**
     * Determines whether the current turn belongs to the provided player.
     * @param player The username of the player, not null
     * @return {@code true} if the turn belongs to the player, {@code false} otherwise
     */
    public boolean isPlayerTurn(String player){
        if(player == null){
            throw new NullPointerException("Player cannot be null");
        }
        return this.currentTurn.getTurnOwner().getName().equals(player);
    }

    /**
     * Determines whether the current turn belongs to the provided player.
     * @param player The object representing the player, not null
     *      * @return {@code true} if the turn belongs to the player, {@code false} otherwise
     */
    public boolean isPlayerTurn(Player player){
        if(player == null){
            throw new NullPointerException("Player cannot be null");
        }
        return this.currentTurn.getTurnOwner().equals(player);
    }

    /**
     * Sets a player as suspended
     * @param player The username of the player, not null
     */
    public void suspendPlayer(String player){
        if(player == null){
            throw new NullPointerException("Player cannot be null");
        }
        //TODO: Implement this method
    }

    /**
     * Removes a player from the match
     * @param player The username of the player, not null
     */
    public void removePlayer(String player){
        if(player == null){
            throw new NullPointerException("Player cannot be null");
        }
        //TODO: Implement this method
    }

    /**
     * Retrieves the current turn
     * @return The object representing the current turn
     */
    public PlayerTurn getCurrentTurn(){
        return currentTurn;
    }

    /**
     * Ends the current turn
     */
    public void endCurrentTurn(){
        //TODO: Implement this method
    }


    /**
     * Retrieves a list of the players participating in the match
     * @return The list of objects representing the players
     */
    public List<Player> getPlayers(){
        //TODO: Implement this method
        return Collections.emptyList();
    }

    /**
     * Determines whether the match is in frenzy mode or not
     * @return True if the nmatch is in frenzy mode, false if not
     */
    public boolean isFrenzy(){
        return this.frenzy;
    }

    /**
     * Sets the match to be in frenzy mode
     */
    public void goFrenzy(){
        //TODO: Implement this method
    }

    /* ===== GETTERS ===== */

    /**
     * Retrieves the number of skulls left on the killshots track
     * @return The count of remaining skulls
     */
    public int getSkulls() {
        return skulls;
    }

    /**
     * Retrieves the game board
     * @return The object representing the game board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Retrieves the stack of ammo cards
     * @return The stack of objects representing the ammo cards
     */
    public CardStack<AmmoCard> getAmmoStack() {
        //TODO: Implement this method
        return ammoStack;
    }

    /**
     * Retrieves the stack of weapon cards
     * @return The stack of objects representing the weapon cards
     */
    public CardStack<WeaponCard> getWeaponStack() {
        //TODO: Implement this method
        return weaponStack;
    }

    /**
     * Retrieves the stack of powerup cards
     * @return The stack of objects representing the powerup cards
     */
    public CardStack<PowerUpCard> getPowerupStack() {
        //TODO: Implement this method
        return powerupStack;
    }
}

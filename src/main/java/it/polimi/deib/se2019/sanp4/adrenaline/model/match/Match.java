package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerUpCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.Weapon;

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
    private CardStack<Weapon> weaponStack;

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
     * @param players The players playing the match
     * @param ammoStack The stack of ammo tiles
     * @param weaponStack The stack of weapon cards
     * @param powerupStack The stack of powerup cards
     * @param skulls The number of skulls in the killshots track
     */
    Match(List<Player> players, CardStack<AmmoCard> ammoStack, CardStack<Weapon> weaponStack, CardStack<PowerUpCard> powerupStack, int skulls){
        this.ammoStack = ammoStack;
        this.weaponStack = weaponStack;
        this.powerupStack = powerupStack;
        this.skulls = skulls;
        //TODO: Create players according to provided usernames
    }

    /**
     * Determines whether the current turn belongs to the provided player.
     * @param player The username of the player
     * @return True if the turn belongs to the player, false if not.
     */
    public boolean isPlayerTurn(String player){
        return false;
    }

    /**
     * Determines whether the current turn belongs to the provided player.
     * @param player The object representing the player
     * @return True if the turn belongs to the player, false if not.
     */
    public boolean isPlayerTurn(Player player){
        return false;
    }

    /**
     * Sets a player as suspended
     * @param player The username of the player
     */
    public void suspendPlayer(String player){};

    /**
     * Removes a player from the match
     * @param player The username of the player
     */
    public void removePlayer(String player){};

    /**
     * Retrieves the current turn
     * @return The object representing the current turn
     */
    public PlayerTurn getCurrentTurn(){
        return null;
    };

    /**
     * Ends the current turn
     */
    public void endCurrentTurn(){};


    /**
     * Retrieves a list of the players participating in the match
     * @return The list of objects representing the players
     */
    public List<Player> getPlayers(){
        return null;
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
    public void goFrenzy(){};

    /* ===== GETTERS ===== */

    /**
     * Retrieves the number of skulls left on the killshots track
     * @return The count of remaining skulls
     */
    public int getSkulls() {
        return skulls;
    }
}

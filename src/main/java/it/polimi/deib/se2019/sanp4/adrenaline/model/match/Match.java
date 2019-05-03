package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerUpCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerState;

import java.util.*;

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

    private static final String NULL_PLAYER_ERROR = "Player cannot be null";

    public static final int MAX_PLAYERS = 5;
    private final int skulls;

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

    /** List of players participating in the match */
    private List<Player> players;

    /** The killshots track */
    private List<Player> killshotsTrack;

    /** A flag indicating whether the match is in frenzy mode or not */
    private boolean frenzy;

    /**
     * Creates a new match for the provided players.
     * It initializes the card stacks using the provided ones.
     * It also sets the number of skulls according to the corresponding parameter.
     * @param players The players playing the match, not null and not containing null values
     * @param ammoStack The stack of ammo tiles, not null
     * @param weaponStack The stack of weapon cards, not null
     * @param powerupStack The stack of powerup cards, not null
     * @param skulls The number of skulls in the killshots track, must be positive
     */
    Match(List<Player> players, CardStack<AmmoCard> ammoStack, CardStack<WeaponCard> weaponStack, CardStack<PowerUpCard> powerupStack, int skulls){
        if(players == null || ammoStack == null || weaponStack == null || powerupStack == null){
            throw new NullPointerException("Found null parameters");
        }
        if(players.contains(null)){
            throw new NullPointerException("Players list cannot contain null values");
        }
        if(skulls < 0){
            throw new IllegalArgumentException("Skulls count cannot be negative");
        }
        this.players = players;
        this.ammoStack = ammoStack;
        this.weaponStack = weaponStack;
        this.powerupStack = powerupStack;
        this.killshotsTrack = new ArrayList<>();
        this.skulls = skulls;
    }

    /**
     * Retrieves a player by using the specified name
     * @param playerName The name of the player, not null and not empty
     * @return The object representing the player, null if the player does not exist
     */
    private Player getPlayerByName(String playerName){
        if(playerName == null){
            throw new NullPointerException("Player name cannot be null");
        }
        if(playerName.isEmpty()){
            throw new IllegalArgumentException("Player name cannot be empty");
        }
        Optional<Player> player = players.stream().filter(p -> p.getName().equals(playerName)).findFirst();
        return player.isPresent() ? player.get() : null;
    }

    /**
     * Determines whether the current turn belongs to the provided player.
     * @param player The username of the player, not null
     * @return {@code true} if the turn belongs to the player, {@code false} otherwise
     */
    public boolean isPlayerTurn(String player){
        if(player == null){
            throw new NullPointerException(NULL_PLAYER_ERROR);
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
            throw new NullPointerException(NULL_PLAYER_ERROR);
        }
        return this.currentTurn.getTurnOwner().equals(player);
    }

    /**
     * Sets a player as suspended
     * @param player The username of the player, not null
     * @throws IllegalStateException If the player is not present
     */
    public void suspendPlayer(String player){
        if(player == null){
            throw new NullPointerException(NULL_PLAYER_ERROR);
        }
        Player suspendedPlayer = getPlayerByName(player);
        if(suspendedPlayer == null){
            throw new IllegalStateException("Player does not exist in the match");
        }
        suspendedPlayer.setState(PlayerState.SUSPENDED);
    }

    /**
     * Removes a player from the match
     * @param player The username of the player, not null
     * @throws IllegalStateException If the player is not present
     */
    public void removePlayer(String player){
        if(player == null){
            throw new NullPointerException(NULL_PLAYER_ERROR);
        }
        Player removedPlayer = getPlayerByName(player);
        if(removedPlayer == null){
            throw new IllegalStateException("Player does not exist in the match");
        }
        removedPlayer.setState(PlayerState.LEFT);
        players.remove(removedPlayer);
    }

    /**
     * Retrieves the current turn
     * @return The object representing the current turn
     */
    public PlayerTurn getCurrentTurn(){
        return currentTurn;
    }

    /**
     * Sets the current turn
     * @param currentTurn The object representing the current turn
     */
    public void setCurrentTurn(PlayerTurn currentTurn){
        if(currentTurn == null){
            throw new NullPointerException("Current turn cannot be null");
        }
        this.currentTurn = currentTurn;
    }


    /**
     * Ends the current turn
     */
    public void endCurrentTurn(){
        // Update the state of the current turn
        currentTurn.setTurnState(PlayerTurnState.OVER);
        //TODO: Finish implementing this method
    }


    /**
     * Retrieves a list of the players participating in the match
     * @return The list of objects representing the players
     */
    public List<Player> getPlayers(){
        return new ArrayList<>(players);
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
        if(frenzy){
            throw new IllegalStateException("Match is already in frenzy mode");
        }
        frenzy = true;
        //TODO: Finish implementing this method
    }

    /* ===== GETTERS ===== */

    /**
     * Retrieves the number of skulls left on the killshots track
     * @return The count of remaining skulls
     */
    public int getSkulls() {
        return skulls - killshotsTrack.size();
    }

    /**
     * Retrieves the killshots track
     * @return The list of players, in chronological order, that performed killshots
     */
    public List<Player> getKillshotsTrack(){
        return Collections.unmodifiableList(killshotsTrack);
    }

    /**
     * Adds a killshot to the killshots track from the provided player
     * @param player The object representing the player, not null
     * @throws FullCapacityException If the killshots track is full
     */
    public void addKillshot(Player player) throws FullCapacityException{
        if(player == null){
            throw new NullPointerException(NULL_PLAYER_ERROR);
        }
        if(killshotsTrack.size() >= skulls){
            throw new FullCapacityException(skulls);
        }
        killshotsTrack.add(player);
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

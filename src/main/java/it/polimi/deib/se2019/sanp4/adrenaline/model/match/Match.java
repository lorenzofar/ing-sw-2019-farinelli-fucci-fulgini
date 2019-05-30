package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.MatchView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observable;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
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
public class Match extends Observable {

    private static final String NULL_PLAYER_ERROR = "Player cannot be null";
    private static final String NULL_CARD_STACK = "Cannot set card stack to null";

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
    private CardStack<PowerupCard> powerupStack;

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
     * @param skulls The number of skulls in the killshots track, must be positive
     * @throws IllegalArgumentException if the skulls are negative
     */
    Match(int skulls){
        if(skulls < 0){
            throw new IllegalArgumentException("Skulls count cannot be negative");
        }
        this.killshotsTrack = new ArrayList<>();
        this.skulls = skulls;
    }

    /**
     * Generates the {@link MatchView} of the match
     * @return the match view
     */
    public MatchView generateView(){
        MatchView view = new MatchView();
        view.setKillshotsCount(getKillshotsTrack().size());
        view.setTotalSkulls(skulls);
        view.setFrenzy(frenzy);
        return view;
    }

    /* ===== TURN METHODS ===== */

    /**
     * Determines whether the current turn belongs to the provided player.
     * If the player is null or there is no active turn, returns {@code false}.
     * @param player The username of the player
     * @return {@code true} if the turn belongs to the player, {@code false} otherwise
     */
    public boolean isPlayerTurn(String player){
        if(currentTurn == null) return false;
        return currentTurn.getTurnOwner().getName().equals(player);
    }

    /**
     * Determines whether the current turn belongs to the provided player.
     * If the player is null or there is no active turn, returns {@code false}.
     * @param player The object representing the player
     * @return {@code true} if the turn belongs to the player, {@code false} otherwise
     */
    public boolean isPlayerTurn(Player player){
        if(currentTurn == null) return false;
        return currentTurn.getTurnOwner().equals(player);
    }

    /**
     * Checks whether the given player is the last player who has the right to play.
     * This is true if the match is in frenzy mode and he's the last one on the killshot track
     * @param player the player
     * @return if this is the last player who has to play
     */
    public boolean isFinalPlayer(Player player) {
        if (player == null) return false;
        if (killshotsTrack.size() == skulls) {
            return killshotsTrack.get(skulls - 1).equals(player);
        } else {
            return false;
        }
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
     * Sets a player state to online, whether this was suspended or not doesn't count
     * @param player The username of the player, not null
     * @throws IllegalStateException If the player is not present
     */
    public void unsuspendPlayer(String player) {
        if(player == null){
            throw new NullPointerException(NULL_PLAYER_ERROR);
        }
        Player suspendedPlayer = getPlayerByName(player);
        if(suspendedPlayer == null){
            throw new IllegalStateException("Player does not exist in the match");
        }
        suspendedPlayer.setState(PlayerState.ONLINE);
    }


    /**
     * Ends the current turn, if the owner of the turn was the last one, also ends the match
     */
    public void endCurrentTurn(){
        // Update the state of the current turn
        currentTurn.setTurnState(PlayerTurnState.OVER);
        //TODO: Finish implementing this method
    }

    /* ===== FRENZY METHODS ===== */

    /**
     * Determines whether the match is in frenzy mode or not
     * @return True if the match is in frenzy mode, false if not
     */
    public boolean isFrenzy(){
        return this.frenzy;
    }

    /**
     * Sets the match to be in frenzy mode, has no effect if it is already in frenzy mode
     */
    public void goFrenzy(){
        frenzy = true;
    }

    /**
     * Adds a killshot to the killshots track from the provided player
     * If the minimum number of skulls (0) is reached, this has no effect
     * @param player The object representing the player, not null
     */
    public void addKillshot(Player player) {
        if(player == null){
            throw new NullPointerException(NULL_PLAYER_ERROR);
        }
        if(killshotsTrack.size() < skulls){
            killshotsTrack.add(player);
        }
    }

    /**
     * Refills all the squares in the board with their items (e.g. weapons, powerups)
     * drawn from the stacks. Calls {@link Square#refill(Match)} on each square of the board
     */
    public void refillBoard() {
        Collection<Square> squares = board.getSquares();
        for (Square sq : squares) {
            sq.refill(this);
        }
    }

    /* ===== GETTERS ===== */

    /**
     * Retrieves an unmodifiable list of the players participating in the match
     * @return an unmodifiable list of players
     */
    public List<Player> getPlayers(){
        return Collections.unmodifiableList(players);
    }

    /**
     * Retrieves a player by using the specified name
     * @param playerName The name of the player
     * @return The object representing the player, null if the player does not exist
     */
    public Player getPlayerByName(String playerName){
        if(playerName == null){
            return null;
        }
        Optional<Player> player = players.stream().filter(p -> p.getName().equals(playerName)).findFirst();
        return player.orElse(null);
    }

    /**
     * Retrieves the current turn
     * @return The object representing the current turn
     */
    public PlayerTurn getCurrentTurn(){
        return currentTurn;
    }

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
        return ammoStack;
    }

    /**
     * Retrieves the stack of weapon cards
     * @return The stack of objects representing the weapon cards
     */
    public CardStack<WeaponCard> getWeaponStack() {
        return weaponStack;
    }

    /**
     * Retrieves the stack of powerup cards
     * @return The stack of objects representing the powerup cards
     */
    public CardStack<PowerupCard> getPowerupStack() {
        return powerupStack;
    }

    /* ===== SETTERS ===== */

    /**
     * Sets the current turn.
     * A null value will reset the turn sequence, i.e. the first player will be the
     * next selected player when selecting the next turn, so a null value
     * should be passed only for test purposes
     * @param currentTurn The object representing the current turn
     */
    public void setCurrentTurn(PlayerTurn currentTurn){
        this.currentTurn = currentTurn;
    }

    /* The following setters must only be used by MatchCreator when creating the match */

    void setBoard(Board board) {
        if (board == null) throw new NullPointerException("Cannot set board to null");
        this.board = board;
    }

    void setAmmoStack(CardStack<AmmoCard> ammoStack) {
        if (ammoStack == null) throw new NullPointerException();
        this.ammoStack = ammoStack;
    }

    void setWeaponStack(CardStack<WeaponCard> weaponStack) {
        if (weaponStack == null) throw new NullPointerException(NULL_CARD_STACK);
        this.weaponStack = weaponStack;
    }

    void setPowerupStack(CardStack<PowerupCard> powerupStack) {
        if (powerupStack == null) throw new NullPointerException(NULL_CARD_STACK);
        this.powerupStack = powerupStack;
    }

    void setPlayers(List<Player> players) {
        if (players == null) throw new NullPointerException(NULL_CARD_STACK);
        this.players = players;
    }

    public void setKillshotsTrack(List<Player> killshotsTrack) {
        if (killshotsTrack == null) throw new NullPointerException();
        this.killshotsTrack = killshotsTrack;
    }
}

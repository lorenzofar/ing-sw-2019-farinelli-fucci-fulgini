package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds damage, marks and deaths of a centain player.
 * It is also provides a method to calculate each player's scores when
 * the board needs to be scored (e.g. killshot), based on the state (regular or frenzy).
 */
public class PlayerBoard{
    /** Maximum marks each other player can have on this board */
    static public final int MAX_MARKS_PER_PLAYER = 3;
    /** Number of damage tokens to perform a killshot */
    static public final int KILLSHOT_DAMAGE = 11;
    /** Number of damage tokens to perform an overkill */
    static public final int OVERKILL_DAMAGE = KILLSHOT_DAMAGE + 1;
    /** Number of maximum damage tokens */
    static public final int MAX_DAMAGES = OVERKILL_DAMAGE;

    /** Damages received by other players */
    private List<Player> damages;
    /** Number of marks received by each player */
    private Map<Player, Integer> marks;
    /** Number of times the player died, either by killshot or overkill */
    private int deaths;
    /** Owner of the player board */
    private final Player player;
    /** State of the board for scoring purposes */
    private PlayerBoardState state;

    /**
     * Constructs a player board with no damages, marks or deaths and in regular state,
     * suitable for the start of the match.
     * @param player owner of the player board, not null
     */
    protected PlayerBoard(Player player){
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        damages = new ArrayList<>();
        marks = new HashMap<>();
        deaths = 0;
        this.player = player;
        state = new RegularPlayerBoardState();
    }

    /**
     * Returns the number of times this player died, either by killshot or overkill.
     * @return number of deaths
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * Increment deaths by one.
     */
    public void addDeath() {
        deaths += 1;
    }

    /**
     * Returns the owner of this board.
     * @return owner of this board
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns a duplicate of the internal list of damages.
     * @return a list of players representing the suffered damages, in chronological order
     */
    public List<Player> getDamages() {
        return new ArrayList<>(damages);
    }

    /**
     * Returns the number of damage tokens on this board.
     * @return number of damage tokens on this board
     */
    public int getDamageCount(){
        /* TODO: Implement this method */
        return 0;
    }

    /**
     * Adds damage by given player. If damage exceeds capacity, it is simply discarded.
     * @param shooter player who did the damage, not the owner of the player board, not null
     * @param count number of damage tokens, must be positive
     */
    public void addDamage(Player shooter, int count){
        /* TODO: Implement this method */
    }

    /**
     * Adds marks by given player. If tokens exceed maximum number per player, they are simply discarded.
     * @param shooter player who sent the marks, not the owner of the player board, not null
     * @param count number of marks to add, must be positive
     */
    public void addMark(Player shooter, int count){
        /* TODO: Implement this method */
    }

    /**
     * Returns the number of marks on the board delivered by a certain player.
     * If there are no marks by that player, it will simply return 0.
     * @param player player who sent the marks, not null
     * @return number of marks, in range {@code 0..MAX_MARKS_PER_PLAYER}
     */
    public int getMarksByPlayer(Player player){
        Integer markCount = marks.get(player);
        return markCount == null ? 0 : markCount;
    }

    /**
     * Returns the player who performed the killshot, if any.
     * @return player who performed the killshot, null otherwise
     */
    public Player getKillshot(){
        /* TODO: Implement this method */
        return null;
    }

    /**
     * Returns the player who performed the overkill, if any.
     * Note: overkill implies killshot.
     * @return player who performed the overkill, null otherwise
     */
    public Player getOverkill(){
        /* TODO: Implement this method */
        return null;
    }

    /**
     * Calculates and returns in a map the scores for all the players who did at least one
     * damage to the owner of the player board.
     * Does not reset the board after calculation.
     * Players who delivered no damage won't be in the map.
     * @return {@code map<player, score>} with each player who got points
     */
    public Map<Player, Integer> getPlayerScores(){
        /* TODO: Implement this method */
        return null;
    }

    /**
     * Checks whether the player is dead or not, then increments the number of deaths accordingly and
     * resets all damage on the board.
     * @throws PlayerException if the player is not dead
     */
    public void updateDeathsAndReset() throws PlayerException {
        /* TODO: Implement this method */
    }

    /**
     * Turn the playerboard to the frenzy state and deaths are removed as they don't count for scoring.
     * This can only be done if there are no damages. Marks are left untouched.
     * @throws PlayerException if there are damages on the board
     */
    public void turnFrenzy() throws  PlayerException {
        /* TODO: Implement this method */
    }

    /**
     * Returns whether the player is dead or not.
     * @return {@code true} if the player is dead, {@code false} otherwise
     */
    public boolean isDead(){
        /* TODO: Implement this method */
        return false;
    }
}
package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

/**
 * Holds damage, marks and deaths of a centain player.
 * It is also provides a method to calculate each player's scores when
 * the board needs to be scored (e.g. killshot), based on the state (regular or frenzy).
 */
public class PlayerBoard{
    /** Maximum marks each other player can have on this board */
    public static final int MAX_MARKS_PER_PLAYER = 3;
    /** Number of damage tokens to perform a killshot */
    public static final int KILLSHOT_DAMAGE = 11;
    /** Number of damage tokens to perform an overkill */
    public static final int OVERKILL_DAMAGE = KILLSHOT_DAMAGE + 1;
    /** Number of maximum damage tokens */
    public static final int MAX_DAMAGES = OVERKILL_DAMAGE;

    /** Damages received by other players */
    private List<Player> damages;
    /** Number of marks received by each player */
    private Map<Player, Integer> marks;
    /** Number of revenge marks received by other players*/
    private Map<Player, Integer> revengeMarks;
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
        revengeMarks = new HashMap<>();
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
        return damages.size();
    }

    /**
     * Adds damage by given player. If damage exceeds capacity, it is simply discarded.
     * @param shooter player who did the damage, not the owner of the player board, not null
     * @param count number of damage tokens, must be positive
     */
    public void addDamage(Player shooter, int count){
        if(shooter == null){
            throw new NullPointerException("Player cannot be null");
        }
        if(count < 0){
            throw new IllegalArgumentException("Number of damage tokens cannot be negative");
        }
        damages.addAll(Collections.nCopies(count, shooter));
    }

    /**
     * Adds marks by given player. If tokens exceed maximum number per player, they are simply discarded.
     * @param shooter player who sent the marks, not the owner of the player board, not null
     * @param count number of marks to add, must be positive
     */
    public void addMark(Player shooter, int count){
        if(shooter == null){
            throw new NullPointerException("Player cannot be null");
        }
        if(count < 0){
            throw new IllegalArgumentException("Number of marks cannot be negative");
        }
        int playerMarks = getMarksByPlayer(shooter) + count;
        // If I reached the maximum amount of marks, cap the value to it
        playerMarks = playerMarks > MAX_MARKS_PER_PLAYER ? MAX_MARKS_PER_PLAYER : playerMarks;
        marks.put(shooter, playerMarks);
    }

    /**
     * Adds one revenge mark from the given player.
     * @param shooter The player who sent the mark, not the owner of the player board, not null
     */
    public void addRevengeMark(Player shooter){
        if(shooter == null){
            throw new NullPointerException("Player cannot be null");
        }
        if(shooter.getPlayerBoard() == this){
            throw new IllegalArgumentException("The player cannot add a revenge mark to himself");
        }
        // Increase the count by 1
        revengeMarks.merge(player, 1, Integer::sum);
    }

    /**
     * Returns the number of marks on the board delivered by a certain player.
     * If there are no marks by that player, it will simply return 0.
     * @param player player who sent the marks, not null
     * @return number of marks, in range {@code 0..MAX_MARKS_PER_PLAYER}
     */
    public int getMarksByPlayer(Player player){
        if(player == null){
            throw new NullPointerException("Player cannot be null");
        }
        Integer markCount = marks.get(player);
        return markCount == null ? 0 : markCount;
    }

    /**
     * Returns the player who performed the killshot, if any.
     * @return player who performed the killshot, null otherwise
     */
    public Player getKillshot(){
        if(damages.size() < KILLSHOT_DAMAGE){
            return null;
        }
        return damages.get(KILLSHOT_DAMAGE - 1);
    }

    /**
     * Returns the player who performed the overkill, if any.
     * Note: overkill implies killshot.
     * @return player who performed the overkill, null otherwise
     */
    public Player getOverkill(){
        if(damages.size() < OVERKILL_DAMAGE){
            return null;
        }
        return damages.get(OVERKILL_DAMAGE - 1);
    }

    /**
     * Calculates and returns in a map the scores for all the players who did at least one
     * damage to the owner of the player board, automatically resolving draws.
     * Does not reset the board after calculation.
     * Players who delivered no damage won't be in the map.
     * @return {@code map<player, score>} with each player who got points
     */
    public Map<Player, Integer> getPlayerScores() {
        // We create a map holding the count of performed damages
        Map<Player, Integer> damageCounts = new HashMap<>();
        // We create a map holding the score of each player
        Map<Player, Integer> playerScores = new HashMap<>();
        // For each shooter, increase its damage count by 1
        damages.forEach(shooter -> damageCounts.merge(shooter, 1, Integer::sum));

        /* ===== DRAWS MANAGEMENT ===== */
        // We have to determine whether there are draws and resolve them
        damageCounts.values().forEach(count -> {
            List<Player> playersWithSameDamageCount = damageCounts.entrySet()
                    .stream()
                    // Get all the players with the same count of damages
                    .filter(entry -> entry.getValue().equals(count))
                    // We keep only the player object of each entry
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            // Check whether there are two or more players with the same amount of damages
            if(playersWithSameDamageCount.size() > 1){
                // Determine which one shoot first (it always exists)
                Player firstShooter = damages.stream().filter(playersWithSameDamageCount::contains).findFirst().get();
                // We remove the first shooter from the list
                playersWithSameDamageCount.remove(firstShooter);
                // We remove players in the list from the map
                playersWithSameDamageCount.forEach(damageCounts::remove);
            }
        });

        /* ===== SCORES ASSIGNMENT ===== */
        // We get the iterator to assign scores
        Iterator<Integer> scores = state.getDamageScores(this);
        // We sort damages by value in decreasing order
        // Then for each entry we retrieve the points and put in the output map
        damageCounts.entrySet()
                .stream()
                .sorted(comparingByValue())
                .forEachOrdered(playerDamageEntry -> playerScores.put(playerDamageEntry.getKey(), scores.next()));

        // Then assign a point for first blood
        Player firstBloodShooter = damages.get(0);
        playerScores.put(firstBloodShooter, playerScores.get(firstBloodShooter) + 1);
        return playerScores;
    }

    /**
     * Checks whether the player is dead or not, then increments the number of deaths accordingly and
     * resets all damage on the board.
     * @throws PlayerException if the player is not dead
     */
    public void updateDeathsAndReset() throws PlayerException {

        if(damages.size() < KILLSHOT_DAMAGE){
            throw new PlayerException("The player is not dead");
        }

        addDeath();
        damages = new ArrayList<>();
        marks = new HashMap<>();
        //TODO: Check whether we have to reset also the revenge marks
    }

    /**
     * Turn the playerboard to the frenzy state and deaths are removed as they don't count for scoring.
     * This can only be done if there are no damages. Marks are left untouched.
     * @throws PlayerException if there are damages on the board
     */
    public void turnFrenzy() throws  PlayerException {
        if(damages.isEmpty()) {
            throw new PlayerException("The player is damaged");
        }
        damages.clear(); // Clear the list of damages
        state = new FrenzyPlayerBoardState(); // Change player board
    }

    /**
     * Returns whether the player is dead or not.
     * @return {@code true} if the player is dead, {@code false} otherwise
     */
    public boolean isDead(){
        return damages.size() >= KILLSHOT_DAMAGE;
    }
}
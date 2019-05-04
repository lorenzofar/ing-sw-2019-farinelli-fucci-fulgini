package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

/**
 * Holds damage, marks and deaths of a certain player.
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

    private static final String NULL_PLAYER_ERROR = "Player cannot be null";

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
            throw new NullPointerException(NULL_PLAYER_ERROR);
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
            throw new NullPointerException(NULL_PLAYER_ERROR);
        }
        if(shooter.equals(player)){
            throw new IllegalArgumentException("Shooter cannot be the board owner");
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
            throw new NullPointerException(NULL_PLAYER_ERROR);
        }
        if(shooter.equals(player)){
            throw new IllegalArgumentException("Shooter cannot be the board owner");
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
            throw new NullPointerException(NULL_PLAYER_ERROR);
        }
        if(shooter.getPlayerBoard() == this){
            throw new IllegalArgumentException("The player cannot add a revenge mark to himself");
        }
        // Increase the count by 1
        revengeMarks.merge(shooter, 1, Integer::sum);
    }

    /**
     * Removes a revenge mark from the provided player
     * It automatically removev the player from the map if he does not have marks remaining
     * @param shooter The object representing the mark shooter, not null
     */
    public void removeRevengeMark(Player shooter){
        if(shooter == null){
            throw new NullPointerException(NULL_PLAYER_ERROR);
        }
        Integer shooterRevengeMarks = revengeMarks.get(shooter);
        if(shooterRevengeMarks==null) {
            return;
        }
        shooterRevengeMarks--;
        if(shooterRevengeMarks == 0){
            revengeMarks.remove(shooter);
        }
        else{
            revengeMarks.put(shooter, shooterRevengeMarks);
        }
    }

    /**
     * Determine whether the provided player has a pending revenge mark on the board owner
     * @param shooter The object representing the player
     * @return {@code true} if there is a revenge mark, {@code false} otherwise
     */
    public boolean hasRevengeMark(Player shooter){
        if(shooter == null){
            throw new NullPointerException(NULL_PLAYER_ERROR);
        }
        return revengeMarks.get(shooter) != null;
    }

    /**
     * Returns the number of marks on the board delivered by a certain player.
     * If there are no marks by that player, it will simply return 0.
     * @param player player who sent the marks, not null
     * @return number of marks, in range {@code 0..MAX_MARKS_PER_PLAYER}
     */
    public int getMarksByPlayer(Player player){
        if(player == null){
            throw new NullPointerException(NULL_PLAYER_ERROR);
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

        if(damages.isEmpty()){
            return playerScores;
        }

        // For each shooter, increase its damage count by 1
        damages.forEach(shooter -> damageCounts.merge(shooter, 1, Integer::sum));

        Set<Player> playersToKeep = new HashSet<>();

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

            // We determine which one shoot first (it always exists)
            // If there are no draws, we just get the only shooter
            Optional<Player> firstShooter = damages.stream().filter(playersWithSameDamageCount::contains).findFirst();

            // We add the first shooter to the list of players to keep
            firstShooter.ifPresent(playersToKeep::add);
        });

        // Then we keep only the selected players
        damageCounts.entrySet().removeIf(damageEntry -> !(playersToKeep.contains(damageEntry.getKey())));

        /* ===== SCORES ASSIGNMENT ===== */
        // We get the iterator to assign scores
        Iterator<Integer> scores = state.getDamageScores(this);
        // We sort damages by value in decreasing order
        // Then for each entry we retrieve the points and put in the output map
        damageCounts.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(comparingByValue()))
                .forEachOrdered(playerDamageEntry -> playerScores.put(playerDamageEntry.getKey(), scores.next()));

        // Then determine who performed the first damage
        Player firstBloodShooter = damages.get(0);
        // And assign an extra point for first blood
        playerScores.merge(firstBloodShooter, 1, Integer::sum);
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
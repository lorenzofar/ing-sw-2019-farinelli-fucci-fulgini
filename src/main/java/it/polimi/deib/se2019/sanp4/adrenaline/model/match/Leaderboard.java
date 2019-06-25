package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds an overview of the information about scores and other metric at the end of the match.
 * <p>
 * The information, for each player, include: scores, performed killshots, performed overkills and number of deaths
 * </p>
 */
public class Leaderboard implements Serializable {

    private static final long serialVersionUID = -9221421253024769111L;

    private final List<Entry> entries;

    /**
     * Holds information about final score, performed killshots, performed overkills and number of deaths
     * for a single player.
     */
    public static class Entry implements Serializable {

        private static final long serialVersionUID = -7560197655885663902L;

        private final String name;

        private final int score;

        private final int performedKillshots;

        private final int performedOverkills;

        private final int deaths;

        /**
         * Creates a new leaderboard entry with given parameters
         *
         * @param name               The name of the player, not null
         * @param score              The score of the player, not negative
         * @param performedKillshots The number of killshots performed by the player, not negative
         * @param performedOverkills The number of overkills performed by the player, not negative
         * @param deaths             The number of times the player died, not negative
         */
        @JsonCreator
        public Entry(
                @JsonProperty("name") String name,
                @JsonProperty("score") int score,
                @JsonProperty("performedKillshots") int performedKillshots,
                @JsonProperty("performedOverkills") int performedOverkills,
                @JsonProperty("deaths") int deaths) {
            this.name = name;
            this.score = score;
            this.performedKillshots = performedKillshots;
            this.performedOverkills = performedOverkills;
            this.deaths = deaths;
        }

        /**
         * Generates a leaderboard entry for given player
         * @param player The player from which to get the data, not null
         * @return The leaderboard entry
         */
        public static Entry generate(Player player) {
            return new Entry(
                    player.getName(),
                    player.getScore(),
                    player.getPerformedKillshots(),
                    player.getPerformedOverkills(),
                    player.getPlayerBoard().getDeaths());
        }

        /**
         * Returns the name of the player
         *
         * @return The name of the player
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the score of the player
         *
         * @return The score of the player
         */
        public int getScore() {
            return score;
        }

        /**
         * Returns the number of times the player performed a killshot
         *
         * @return The number of times the player performed a killshot
         */
        public int getPerformedKillshots() {
            return performedKillshots;
        }

        /**
         * Returns the number of times the player performed an overkill
         *
         * @return The number of times the player performed an overkill
         */
        public int getPerformedOverkills() {
            return performedOverkills;
        }

        /**
         * Returns the number of times the player died
         *
         * @return The number of times the player died
         */
        public int getDeaths() {
            return deaths;
        }
    }

    /**
     * Creates a new leaderboard with given entries
     *
     * @param entries The list of entries, not null
     */
    @JsonCreator
    public Leaderboard(@JsonProperty("entries") List<Entry> entries) {
        this.entries = entries;
    }

    /**
     * Returns an unmodifiable view of the entries in this leaderboard, sorted in descending order for score
     *
     * @return The list of entries
     */
    public List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Generates the leaderboard for the given players
     *
     * @param players The list of players which will be in the leaderboard, not null
     * @return The leaderboard
     */
    public static Leaderboard generate(List<Player> players) {
        /* Generate the list of entries */
        List<Entry> entries = players.stream()
                .map(Entry::generate) /* Generate entry for each player */
                .sorted(Comparator.comparingInt(Entry::getScore).reversed()) /* Sort in descending order by score */
                .collect(Collectors.toList());

        /* Create the leaderboard instance */
        return new Leaderboard(entries);
    }
}

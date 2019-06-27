package it.polimi.deib.se2019.sanp4.adrenaline.controller.match;

import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.ScoresIterator;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

/**
 * A specialized class describing an object that is responsible of assigning points to users
 * It represents the standard scoring mechanism of the game, as described in the game manual
 */
public class StandardScoreManager implements ScoreManager {

    private static final int[] POINTS = {8, 6, 4, 2, 1, 1};

    /**
     * Assign scores to players according to the damage boards of the provided players
     * Is also responsible of adding revenge marks to overkill shooters
     * and updating the counters of performed killshots and overkills
     * @param players The list of players whose damage boards have to be considered
     */
    private void assignScores(List<Player> players){
        players.forEach(player -> {
            // Get the scores to assign to each player
            Map<Player, Integer> shootersScores = player.getPlayerBoard().getPlayerScores();
            // Assign scores to the players
            shootersScores.forEach(Player::addScorePoints);
            // Update the counter of killshots on the killer
            Player killer = player.getPlayerBoard().getKillshot();
            killer.addPerformedKillshot();
            // Get the player who performed overkill (if present)
            Player overKiller = player.getPlayerBoard().getOverkill();
            if(overKiller != null){
                // Add a revenge mark to him from the current player
                overKiller.getPlayerBoard().addMark(player, 1);
                // Update the counter
                overKiller.addPerformedOverkill();
            }
        });
    }

    /**
     * Assign extra points to players that performed more than one killshot,
     * according to the damage boards of the provided players,
     * and update the killshot track of the match
     * @param players The list of players whose damage boards have to be considered
     * @param match The object representing the match
     */
    private void manageKillshotsAndAssignExtraPoints(List<Player> players, Match match){
        Map<Player, Integer> killshotsCount = new HashMap<>();
        players.forEach(player -> {
            // Update the count of killshots
            killshotsCount.merge(player.getPlayerBoard().getKillshot(), 1, Integer::sum);
            // Update the killshots track
            match.addKillshot(player.getPlayerBoard().getKillshot());
        });

        // Give extra points to players that performed more than one killshot
        killshotsCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 1) // We filter those who performed more than one killshot
                .forEach(entry -> entry.getKey().addScorePoints(1)); // We give them one extra point
    }


    /**
     * Perform turn scoring of the turn on the provided match
     * Updates:
     * <ul>
     *     <li>Scores of the players</li>
     *     <li>Counters of killshots and overkills on players</li>
     *     <li>Killshot track</li>
     *     <li>Revenge marks due to killshots</li>
     * </ul>
     * @param match The object representing the match, not null
     */
    @Override
    public void scoreTurn(Match match) {
        if(match == null){
            throw new NullPointerException("Match cannot be null");
        }

        // Retrieve all players that are dead
        // These are just the new deaths that occurred in the current turn
        List<Player> deadPlayers = match.getPlayers().stream()
                .filter(player -> player.getPlayerBoard().isDead())
                .collect(Collectors.toList());

        assignScores(deadPlayers);
        manageKillshotsAndAssignExtraPoints(deadPlayers, match);
    }

    /**
     * Perform final scoring on the provided match
     * assuming that the scoring of the turn has already been performed
     * @param match The object representing the match, not null
     * @return A map of the scores got from the killshot track by the players
     */
    @Override
    public Map<Player, Integer> scoreFinal(Match match) {
        if(match == null){
            throw new NullPointerException("Match cannot be null");
        }

        List<Player> killshotsTrack = match.getKillshotsTrack();

        // Count how many tokens each player has on the killshot track
        Map<Player, Integer> tokenCount = match.getPlayers().stream()
                .filter(killshotsTrack::contains)
                .collect(Collectors.toMap(
                        p -> p, /* Key is the player itself */
                        /* Value is the sum of tokens */
                        p -> p.getPerformedKillshots() + p.getPerformedOverkills()));

        // Resolve ties by giving points to the first player performing the kill,
        // the others get removed from tokensCount
        // Create a set with the distinct number ok tokens
        Set<Integer> distinctTokensCount = tokenCount.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        distinctTokensCount.forEach(count -> {
            List<Player> playersWithSameCount = tokenCount.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().equals(count))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if(playersWithSameCount.size() > 1){
                /* Determine the player who got the earlier killshot */
                Player firstKillshotShooter = match.getKillshotsTrack().stream()
                        .filter(playersWithSameCount::contains)
                        .findFirst().get();
                /* Remove from killshots count all the players except the first */
                playersWithSameCount.remove(firstKillshotShooter);
                playersWithSameCount.forEach(tokenCount::remove);
            }
        });

        Map<Player, Integer> killshotTrackScores = new HashMap<>();

        // Compute extra points for performed kills
        ScoresIterator scoresIterator = new ScoresIterator(POINTS, 0);
        tokenCount.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(comparingByValue()))
                .map(Map.Entry::getKey)
                .forEachOrdered(player -> killshotTrackScores.put(player, scoresIterator.next()));

        // Add extra points to the players
        killshotTrackScores.forEach(Player::addScorePoints);

        return killshotTrackScores;
    }
}

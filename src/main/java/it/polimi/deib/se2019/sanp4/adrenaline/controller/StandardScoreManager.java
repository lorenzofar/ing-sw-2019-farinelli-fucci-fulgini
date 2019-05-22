package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.ScoresIterator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

/**
 * A specialized class describing an object that is responsible of assigning points to users
 * It represents the standard scoring mechanism of the game, as described in the game manual
 */
public class StandardScoreManager implements ScoreManager {

    private static final int[] POINTS = {8, 6, 4, 2, 1, 1};
    private static final ScoresIterator scoresIterator = new ScoresIterator(POINTS, 0);

    /**
     * Assign scores to players according to the damage boards of the provided players
     * Is also responsible of adding revenge marks to overkill shooters
     * @param players The list of players whose damage boards have to be considered
     */
    private void assignScores(List<Player> players){
        players.forEach(player -> {
            // Get the scores to assign to each player
            Map<Player, Integer> shootersScores = player.getPlayerBoard().getPlayerScores();
            // Assign scores to the players
            shootersScores.forEach(Player::addScorePoints);
            // Get the player who performed overkill (if present)
            Player overkillShooter = player.getPlayerBoard().getOverkill();
            if(overkillShooter != null){
                // Add a revenge mark to him from the current player
                overkillShooter.getPlayerBoard().addMark(player, 1);
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

    @Override
    public Map<Player, Integer> scoreFinal(Match match) {
        if(match == null){
            throw new NullPointerException("Match cannot be null");
        }

        // Retrieve all the players that have damages on their boards
        List<Player> playersToConsider = match.getPlayers().stream()
                .filter(player -> player.getPlayerBoard().getDamages().isEmpty())
                .collect(Collectors.toList());

        // Assign scores according to their boards
        assignScores(playersToConsider);

        // Then consider the killshot track
        Map<Player, Integer> killshotsCount = new HashMap<>();
        // Count how many killshots has each player performed
        match.getKillshotsTrack().forEach(player -> killshotsCount.merge(player, 1, Integer::sum));

        // Resolve draws by giving points to the first player performing the killshot
        killshotsCount.values().forEach(count -> {
            List<Player> playersWithSameCount = killshotsCount.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().equals(count))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if(playersWithSameCount.size() > 1){
                Player firstKillshotShooter = match.getKillshotsTrack().stream().filter(playersWithSameCount::contains).findFirst().get();
                playersWithSameCount.remove(firstKillshotShooter);
                playersWithSameCount.forEach(killshotsCount::remove);
            }
        });

        Map<Player, Integer> killshotTrackScores = new HashMap<>();

        // Compute extra points for performed killshots
        killshotsCount.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(comparingByValue()))
                .map(Map.Entry::getKey)
                .forEachOrdered(player -> killshotTrackScores.put(player, scoresIterator.next()));

        // Add extra points to the players
        killshotTrackScores.forEach(Player::addScorePoints);

        return killshotTrackScores;
    }
}

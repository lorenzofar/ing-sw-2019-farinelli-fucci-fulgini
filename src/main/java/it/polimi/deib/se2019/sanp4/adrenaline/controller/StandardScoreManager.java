package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A specialized class describing an object that is responsible of assigning points to users
 * It represents the standard scoring mechanism of the game, as described in the game manual
 */
public class StandardScoreManager extends ScoreManager {
    @Override
    public void scoreTurn(Match match) {
        if(match == null){
            throw new NullPointerException("Match cannot be null");
        }
        // Store the number of performed killshots for each player
        Map<Player, Integer> killshotsCount = new HashMap<>();
        // Retrieve all players that are dead
        // These are just the new deaths that occurred in the current turn
        List<Player> deadPlayers = match.getPlayers().stream()
                .filter(player -> player.getPlayerBoard().isDead())
                .collect(Collectors.toList());
        deadPlayers.forEach(player -> {
            // Get the scores to assign to each player
            Map<Player, Integer> shootersScores = player.getPlayerBoard().getPlayerScores();
            // Assign scores to the players
            shootersScores.forEach(Player::addScorePoints);
            // Get the player who performed overkill (if present)
            Player overkillShooter = player.getPlayerBoard().getOverkill();
            if(overkillShooter != null){
                // Add a revenge mark to him from the current player
                overkillShooter.getPlayerBoard().addRevengeMark(player);
            }
            // Update the count of killshots
            killshotsCount.merge(player.getPlayerBoard().getKillshot(), 1, Integer::sum);
            // Update the killshots track
            try {
                match.addKillshot(player.getPlayerBoard().getKillshot());
            } catch (FullCapacityException e) {
                e.printStackTrace();
            }
        });
        // We now finished to assign scores
        // No we consider the players that performed more than one killshot in the turn
        killshotsCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 1) // We filter those who performed more than one killshot
                .forEach(entry -> entry.getKey().addScorePoints(1)); // We give them one extra point
    }

    @Override
    public void scoreFinal(Match match) {
        if(match == null){
            throw new NullPointerException("Match cannot be null");
        }
        //TODO: Implement this method
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.Map;

/**
 * An abstract class representing an object that is responsible of assigning points to users
 * according to the current state of the game
 */
public interface ScoreManager {

    /**
     * Perform turn scoring on the provided match
     * @param match The object representing the match, not null
     */
    void scoreTurn(Match match);

    /**
     * Perform final scoring on the provided match
     * @param match The object representing the match, not null
     * @return A map of the scores got from the killshot track by the players
     */
    Map<Player, Integer> scoreFinal(Match match);
}

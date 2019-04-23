package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;

/**
 * An abstract class representing an object that is responsible of assigning points to users
 * according to the current state of the game
 */
public abstract class ScoreManager {

    /**
     * Perform turn scoring on the provided match
     * @param match The object representing the match, not null
     */
    public abstract void scoreTurn(Match match);

    /**
     * Perform final scoring on the provided match
     * @param match The object representing the match, not null
     */
    public abstract void scoreFinal(Match match);
}

package it.polimi.deib.se2019.sanp4.adrenaline.model;

import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;

/**
 * Top-level class for the model package.
 * It provides access to the match and holds the players before match creation.
 */
public interface Model {
    /**
     * Returns match instance associated to this model.
     * @return match instance
     */
    Match getMatch();

    /**
     * Sets the match instance associated to this model.
     * @param match math instance to be associated
     */
    void setMatch(Match match);
}

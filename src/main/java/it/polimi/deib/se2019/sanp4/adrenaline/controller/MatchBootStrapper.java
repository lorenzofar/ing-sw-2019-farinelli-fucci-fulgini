package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;

/**
 * This is responsible for asking a player the initial configuration of the match.
 * If the request expires, a random configuration is chosen
 */
public class MatchBootStrapper {

    private static final int NUMBER_OF_BOARDS = 4;

    /**
     * Asks give player for the match configuration with a timeout.
     * If the request expires a random board and a default number of skulls is chosen
     * @param view view of the player
     * @return the match configuration, not null
     */
    private static MatchConfiguration askForMatchConfiguration(PersistentView view) {
        /* TODO: Implement this method */
        return null;
    }
}

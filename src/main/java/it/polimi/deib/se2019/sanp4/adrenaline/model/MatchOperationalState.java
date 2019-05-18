package it.polimi.deib.se2019.sanp4.adrenaline.model;

/**
 * Represents the operational state of the match in the model
 */
public enum MatchOperationalState {
    /** The match is not started, but it's waiting for the minimum number of players to start */
    WAITING_PLAYERS,
    /** The match has started and the players are actually playing */
    ACTIVE,
    /** The match is finished, either because connected players fell below the minimum or because it
     * terminated normally
     */
    FINISHED
}

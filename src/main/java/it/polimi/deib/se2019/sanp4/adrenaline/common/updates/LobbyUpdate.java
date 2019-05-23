package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

/**
 * This update is sent in broadcast by the Lobby to notify the players who are waiting
 * for a match to start.
 * It contains the names of the waiting players
 */
public class LobbyUpdate extends ModelUpdate {

    private static final long serialVersionUID = -7294810016274866426L;

    private Collection<String> waitingPlayers;

    /**
     * Creates the lobby update with the names of the players waiting for the match to start
     * @param waitingPlayers a collection with the names of the waiting players, not null
     * @throws NullPointerException if the collection is null
     */
    @JsonCreator
    public LobbyUpdate(@JsonProperty("waitingPlayers") Collection<String> waitingPlayers) {
        if (waitingPlayers == null) throw new NullPointerException();
        this.waitingPlayers = waitingPlayers;
    }

    public Collection<String> getWaitingPlayers() {
        return waitingPlayers;
    }
}

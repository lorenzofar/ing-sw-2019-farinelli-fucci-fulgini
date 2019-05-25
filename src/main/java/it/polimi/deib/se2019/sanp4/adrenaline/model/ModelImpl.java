package it.polimi.deib.se2019.sanp4.adrenaline.model;

import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteRoutingObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;

public class ModelImpl extends RemoteRoutingObservable<ModelUpdate> implements Model {

    /** The match associated to the model */
    private Match match;

    /** The operational state of the match */
    private MatchOperationalState operationalState;

    /**
     * Creates an empty model.
     * The initial operational state is {@link MatchOperationalState#WAITING_PLAYERS}
     */
    public ModelImpl(){
        operationalState = MatchOperationalState.WAITING_PLAYERS;
    }

    /**
     * Returns match instance associated to this model.
     *
     * @return match instance
     */
    @Override
    public Match getMatch() {
        return match;
    }

    /**
     * Sets the internal instance to the match
     *
     * @param match the match to be set, not null
     */
    @Override
    public void setMatch(Match match) {
        this.match = match;
    }

    /**
     * Suspends a player if the player exists and the match has been created,
     * otherwise it does nothing
     *
     * @param username the username of the player to be suspended
     */
    @Override
    public void suspendPlayer(String username) {
        if (match != null && username != null) {
            match.suspendPlayer(username);
        }
    }

    /**
     * Returns the operational state of the match
     *
     * @return the operational state of the match
     */
    @Override
    public MatchOperationalState getOperationalState() {
        return operationalState;
    }

    /**
     * Sets the operational state of the match
     *
     * @param state the operational state
     */
    @Override
    public void setOperationalState(MatchOperationalState state) {
        this.operationalState = state;
    }

    /**
     * Send an update/event from an {@link Observable} object.
     *
     * @param event event/update to be sent
     */
    @Override
    public void update(ModelUpdate event) {
        /* Receive updates from the other classes of the model */
        /* TODO: Implement this method */
    }
}

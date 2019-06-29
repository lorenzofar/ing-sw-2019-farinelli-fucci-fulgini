package it.polimi.deib.se2019.sanp4.adrenaline.model;

import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteRoutingObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.InitialUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.MatchOperationalStateUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;

import java.util.Set;

/**
 * Concrete implementation of {@link Model}.
 */
public class ModelImpl extends RemoteRoutingObservable<ModelUpdate> implements Model, Observer<ModelUpdate> {

    /**
     * The match associated to the model
     */
    private Match match;

    /**
     * The operational state of the match
     */
    private MatchOperationalState operationalState;

    /**
     * Creates an empty model.
     * The initial operational state is {@link MatchOperationalState#WAITING_PLAYERS}
     */
    public ModelImpl() {
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
        if (this.match != null) {
            this.match.removeObserver(this);
        }
        this.match = match;
        this.match.addObserver(this);
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
     * Unsuspends a player if it has been suspended
     * If the player does not exist or if it wasn't suspended, nothing happens
     *
     * @param username the username of the player to be suspended
     */
    @Override
    public void unsuspendPlayer(String username) {
        if (match != null && username != null) {
            match.unsuspendPlayer(username);
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
        MatchOperationalState oldState = this.operationalState;
        this.operationalState = state;
        if(oldState != state) {
            this.notifyObservers(new MatchOperationalStateUpdate(this.operationalState));
        }
    }

    /**
     * Sends the model update with all the match status (aka initial update)
     * to the specified player
     *
     * @param username username of the player
     */
    @Override
    public void sendInitialUpdate(String username) {
        if (match == null) return;
        InitialUpdate update = match.generateUpdate();
        notifyObservers(username, update);
    }

    /**
     * Send an event from an {@link Observable} object.
     *
     * @param update update to be sent
     */
    @Override
    public void update(ModelUpdate update) {
        /* Receive updates from the other classes of the model */
        Set<String> recipients = update.getRecipients();
        if (recipients == null) {
            notifyObservers(update); /* Send in broadcast */
        } else {
            notifyObservers(update.getRecipients(), update);
        }
    }
}

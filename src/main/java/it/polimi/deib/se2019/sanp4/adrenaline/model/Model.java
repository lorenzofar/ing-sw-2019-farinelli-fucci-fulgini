package it.polimi.deib.se2019.sanp4.adrenaline.model;

import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;

import java.util.Collection;

/**
 * Top-level class for the model package.
 * It provides access to the match and holds the players before match creation.
 * The model observes the match for {@link ModelUpdate}s and so it receives
 * all the updates, which then get sent (routed) to the correct views
 */
public interface Model extends Observer<ModelUpdate> {

    /* ======= MATCH OPERATION ========= */

    /**
     * Sets the internal instance to the match
     * @param match the match to be set
     */
    void setMatch(Match match);

    /**
     * Returns match instance associated to this model.
     * @return match instance
     */
    Match getMatch();

    /**
     * Suspends a player if the player exists and the match has been created,
     * otherwise it does nothing
     * @param username the username of the player to be suspended
     */
    void suspendPlayer(String username);

    /**
     * Returns the operational state of the match
     * @return the operational state of the match
     */
    MatchOperationalState getOperationalState();

    /**
     * Sets the operational state of the match
     * @param state the operational state
     */
    void setOperationalState(MatchOperationalState state);

    /* ======== OBSERVABLE ======== */

    /**
     * Subscribe given observer to all events addressed to the specified username.
     * @param username username for routing
     * @param observer observer to be subscribed
     */
    void addObserver(String username, RemoteObserver<ModelUpdate> observer);

    /**
     * Unsubscribe given observer from events addressed to given username.
     * If the given pair does not exist, nothing happens.
     * @param username username for routing
     * @param observer observer to be unsubscribed
     */
    void removeObserver(String username, RemoteObserver observer);

    /**
     * Unsubscribe all observers for a specific username.
     * If an observer is subscribed to different usernames, it will be kept on the other usernames.
     * If the username does not exist, nothing happens.
     * @param username username whose observers must be removed
     */
    void removeAllObservers(String username);

    /**
     * Sends the event to the observers of the username (i.e. calls {@link Observer#update(Object)} on them).
     * @param username username for routing
     * @param event event to be sent
     */
    void notifyObservers(String username, ModelUpdate event);

    /**
     * Sends the event to the observers of the recipients (i.e. calls {@link Observer#update(Object)} on them).
     * If an observers is subscribed to more than one username, it will only be notified once.
     * @param recipients collection of usernames whose observers will received the event
     * @param event event to be sent
     */
    void notifyObservers(Collection<String> recipients, ModelUpdate event);

    /**
     * Sends the event to all the observers, regardless of the usernames they subscribed to.
     * If an observers is subscribed to more than one username, it will only be notified once.
     * @param event event to be sent
     */
    void notifyObservers(ModelUpdate event);
}

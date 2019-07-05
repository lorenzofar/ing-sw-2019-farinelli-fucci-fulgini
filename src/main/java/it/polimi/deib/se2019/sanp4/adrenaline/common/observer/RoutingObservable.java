package it.polimi.deib.se2019.sanp4.adrenaline.common.observer;

import java.util.*;

/**
 * Observable class where observers subscribe with a username.
 * <p>
 * This class handles the case where the dispatched events/updates must be sent to only a subset of the observers.
 * Each observer can subscribe with a username, when an event needs to be dispatched it is routed based on
 * a provided list of recipients.
 * </p>
 * <p>
 * This abstract class provides two methods to notify observers: one sends the event to only given observers
 * and the other one sends them to all observers, regardless of the username they subscribed with
 * </p>
 *
 * @param <T> The type of events dispatched by this class
 * @author Alessandro Fulgini, Tiziano Fucci
 * @see Observable for a version of this without routing
 * @see Observer
 */
public abstract class RoutingObservable<T> {
    private Map<String, Set<Observer<T>>> observersMap = new HashMap<>();

    /**
     * Subscribe given observer to all events addressed to the specified username.
     *
     * @param username username for routing
     * @param observer observer to be subscribed
     */
    public void addObserver(String username, Observer<T> observer) {
        /* If there is no set of observers for given username an empty set is created, then the observer is added */
        observersMap.computeIfAbsent(username, name -> new HashSet<>()).add(observer);
    }

    /**
     * Unsubscribe given observer from events addressed to given username.
     * If the given pair does not exist, nothing happens.
     *
     * @param username username for routing
     * @param observer observer to be unsubscribed
     */
    public void removeObserver(String username, Observer observer) {
        Set<Observer<T>> observers = observersMap.get(username);

        if (observers != null) {
            observers.remove(observer);
            /* If the username has no observers left, remove its entry */
            if (observers.isEmpty()) observersMap.remove(username);
        }
    }

    /**
     * Unsubscribe all observers for a specific username.
     * If an observer is subscribed to different usernames, it will be kept on the other usernames.
     * If the username does not exist, nothing happens.
     *
     * @param username username whose observers must be removed
     */
    public void removeAllObservers(String username) {
        observersMap.remove(username);
    }

    /**
     * Sends the event to the observers of the username (i.e. calls {@link Observer#update(Object)} on them).
     *
     * @param username username for routing
     * @param event    event to be sent
     */
    public void notifyObservers(String username, T event) {
        Set<Observer<T>> observers = observersMap.get(username);
        if (observers != null) {
            observers.forEach(obs -> obs.update(event));
        }
    }

    /**
     * Sends the event to the observers of the recipients (i.e. calls {@link Observer#update(Object)} on them).
     *
     * @param recipients collection of usernames whose observers will received the event
     * @param event      event to be sent
     */
    public void notifyObservers(Collection<String> recipients, T event) {
        if (recipients == null) return;
        recipients.forEach(user -> notifyObservers(user, event));
    }

    /**
     * Sends the event to all the observers, regardless of the usernames they subscribed to.
     * If an observers is subscribed to more than one username, it will only be notified once.
     *
     * @param event event to be sent
     */
    public void notifyObservers(T event) {
        observersMap.values().stream()
                /* Flat map to all the observers */
                .flatMap(Set::stream)
                /* Remove duplicates */
                .distinct()
                /* Notify all of them */
                .forEach(o -> o.update(event));
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.observer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Observable in the <i>observer pattern</i>.
 * {@link Observable} objects can subscribe to this to receive events/updates.
 * Classes extending this interface should use the {@link #notifyObservers(Object)} method to send updates to their
 * observers.
 * @param <T> The type of events dispatched by this class
 * @see Observer
 */
public abstract class Observable<T> {

    private Set<Observer<T>> observers = new HashSet<>();

    /**
     * Subscribes given observer for events.
     * In case the observer was already subscribed nothing happens (i.e. it won't get double events).
     * @param observer observer to subscribe
     */
    public void addObserver(Observer<T> observer){
        observers.add(observer);
    }

    /**
     * Unsubscribe observer for events.
     * In case the observer was not subscribed nothing happens.
     * @param observer observer to be unsubscribed
     */
    public void removeObserver(Observer<T> observer){
        observers.remove(observer);
    }

    /**
     * Sends the given event to all subscribed observers (i.e. calls {@link Observer#update(Object)} on them).
     * @param event event to be sent to observers
     */
    protected void notifyObservers(T event){
        observers.forEach(observer -> observer.update(event));
    }
}

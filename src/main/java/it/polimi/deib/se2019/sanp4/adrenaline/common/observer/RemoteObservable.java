package it.polimi.deib.se2019.sanp4.adrenaline.common.observer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Behaves like {@link Observable}, but it also accepts {@link RemoteObserver}s.
 * In case calling {@code update()} on a remote observer throws an {@code IOException} the exception is ignored.
 * This means that subclasses must be aware that their dispatched events may not get to their observers when
 * communication is interrupted.
 * @param <T> The type of events dispatched by this class
 * @see Observable
 * @see RemoteObserver
 */
public abstract class RemoteObservable<T> {

    private Set<RemoteObserver<T>> observers = new HashSet<>();

    /**
     * Subscribes given observer for events.
     * In case the observer was already subscribed nothing happens (i.e. it won't get double events).
     * @param observer observer to subscribe
     */
    public void addObserver(RemoteObserver<T> observer) {
        observers.add(observer);
    }

    /**
     * Unsubscribe observer for events.
     * In case the observer was not subscribed nothing happens.
     * @param observer observer to be unsubscribed
     */
    public void removeObserver(RemoteObserver<T> observer) {
        observers.remove(observer);
    }

    /**
     * Sends the given event to all subscribed observers (i.e. calls {@link Observer#update(Object)} on them).
     * If calling {@code update()} throws an {@code IOException}, the exception is simply ignored.
     * @param event event to be sent to observers
     */
    protected void notifyObservers(T event) {
        for (RemoteObserver<T> observer : observers) {
            try {
                observer.update(event);
            } catch (IOException ignore) {
                /* Ignore the exception: this observer is not able to get the update */
            }
        }
    }
}

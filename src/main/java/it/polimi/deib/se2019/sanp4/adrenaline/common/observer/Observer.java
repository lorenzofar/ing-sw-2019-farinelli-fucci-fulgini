package it.polimi.deib.se2019.sanp4.adrenaline.common.observer;

/**
 * Observer in the <i>observer pattern</i>.
 * A class implementing this interface can subscribe to an {@link Observable} and receive updates/events.
 *
 * @param <T> The type of events this interface subscribes to
 * @author Alessandro Fulgini, Tiziano Fucci
 * @see Observable
 */
public interface Observer<T> extends RemoteObserver<T> {
    /**
     * Send an update/event from an {@link Observable} object.
     *
     * @param event event/update to be sent
     */
    void update(T event);
}

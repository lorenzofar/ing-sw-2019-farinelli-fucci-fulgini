package it.polimi.deib.se2019.sanp4.adrenaline.common.observer;

import java.io.IOException;
import java.rmi.Remote;

/**
 * Represents an observer (possibly) communicating through the network.
 * Behaves like {@link Observer}, but it can throw IOExceptions.
 * A class implementing this interface can subscribe to a {@link RemoteObservable} and receive updates/events.
 * @param <T> The type of events this interface subscribes to
 * @see RemoteObservable
 * @see Observer for a version of this that does not throw IOException
 */
public interface RemoteObserver<T> extends Remote {
    /**
     * Send an update/event from a {@link RemoteObservable} object.
     * @param event event/update to be sent
     * @throws IOException if the remote call fails
     */
    void update(T event) throws IOException;
}

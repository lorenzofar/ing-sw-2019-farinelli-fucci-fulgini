package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;

/**
 * Represents the target on which a {@link SocketServerCommand} is applied.
 * The target is normally a {@code SocketRemoteView} which resides on the server
 */
public interface SocketServerCommandTarget {

    /**
     * Returns the server
     * @return the server instance
     */
    SocketServer getServer();

    /**
     * Returns an object which can be used as a stub for the view
     * @return an object which can be used as a stub for the view
     */
    RemoteView getRemoteView();

    /**
     * Notifies subscribed observers with given event
     * @param event the event to notify to observers
     */
    void notifyEvent(ViewEvent event);

    /**
     * Sends a command to the client attached to this target,
     * usually as a response to the execution of this command
     * @param command the command that has to be sent
     */
    void sendCommand(SocketClientCommand command);
}
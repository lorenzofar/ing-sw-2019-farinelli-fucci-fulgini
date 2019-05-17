package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;

import java.io.IOException;

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
     * Returns the username of the player associated with this view.
     * It returns {@code null} if the username has not already been set
     * (prior to login)
     * @return username of the player, if it has been set, {@code null} otherwise
     */
    String getUsername();

    /**
     * Set the username of the remote view
     * @param username name of the user
     */
    void setUsername(String username);

    /**
     * Sends a command to the client attached to this target,
     * usually as a response to the execution of this command
     * @param command the command that has to be sent
     * @throws IOException if the command cannot be sent due to network problems
     */
    void sendCommand(SocketClientCommand command) throws IOException;
}

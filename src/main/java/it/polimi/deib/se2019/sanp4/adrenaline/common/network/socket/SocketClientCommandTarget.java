package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;

import java.io.IOException;

/**
 * Represents the target on which a {@link SocketClientCommand} is applied.
 * The target is normally a {@code ServerConnection} which resides on the client
 */
public interface SocketClientCommandTarget {

    /**
     * Returns the client view which uses the connection
     * @return client view
     */
    ClientView getClientView();

    /**
     * Sends a command to the remote server attached to this target
     * Does not guarantee that the command is sent if there are network problems
     * @param command the command that has to be sent
     * @throws IOException If the command can't be sent due to network problems
     */
    void sendCommand(SocketServerCommand command) throws IOException;
}

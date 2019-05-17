package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;

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
}

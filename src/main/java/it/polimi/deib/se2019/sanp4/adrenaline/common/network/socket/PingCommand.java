package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * This is a no-op command which can be used by the client and the server to check that
 * the connection is still up.
 */
public class PingCommand implements SocketServerCommand, SocketClientCommand {

    /**
     * Creates a new no-op ping command
     */
    @JsonCreator
    public PingCommand() {
        /* Nothing to do */
    }

    /**
     * Applies the command to given target, namely it is a {@code SocketServerConnection}
     *
     * @param target target of the command
     */
    @Override
    public void applyOn(SocketClientCommandTarget target) {
        /* Nothing to do */
    }

    /**
     * Applies the command to given target, namely it is the {@code SocketRemoteView}
     *
     * @param target target of the command
     */
    @Override
    public void applyOn(SocketServerCommandTarget target) {
        /* Nothing to do */
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Sent by client to log out from the server
 */
public class LogoutCommand implements SocketServerCommand {

    /**
     * Creates a new logout command
     */
    @JsonCreator
    public LogoutCommand() {
        /* Nothing to do here */
    }

    /**
     * Applies the command to given target, namely it is the {@code SocketRemoteView}
     *
     * @param target target of the command
     */
    @Override
    public void applyOn(SocketServerCommandTarget target) {
        SocketServer server = target.getServer();
        String username = target.getUsername();

        /* Call logout on the server */
        if (username != null) {
            server.playerLogout(username);
        }
    }
}

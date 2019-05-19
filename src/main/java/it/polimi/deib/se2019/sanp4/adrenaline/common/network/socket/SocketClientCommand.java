package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents a command that a server wants to issue on a client communicating with Socket.
 * The target of this command is {@link SocketClientCommandTarget}, usually a {@code SocketServerConnection}
 * residing on the client
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        property = "class"
)
public interface SocketClientCommand {

    /**
     * Applies the command to given target, namely it is a {@code SocketServerConnection}
     * @param target target of the command
     */
    void applyOn(SocketClientCommandTarget target);
}

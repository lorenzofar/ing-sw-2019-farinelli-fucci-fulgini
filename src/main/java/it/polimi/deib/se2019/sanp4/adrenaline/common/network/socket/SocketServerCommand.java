package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents a command that a client communicating with Socket wants to issue on the server.
 * The command is serialized, sent to the server, then deserialized and applied on the remote view there,
 * which represents the target of the command, abstracted by {@link SocketServerCommandTarget}
 * @author Alessandro Fulgini
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        property = "class"
)
public interface SocketServerCommand {
    /**
     * Applies the command to given target, namely it is the {@code SocketRemoteView}
     * @param target target of the command
     */
    void applyOn(SocketServerCommandTarget target);
}

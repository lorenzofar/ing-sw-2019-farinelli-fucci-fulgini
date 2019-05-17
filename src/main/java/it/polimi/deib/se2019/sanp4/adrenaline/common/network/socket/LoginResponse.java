package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response to {@link LoginCommand}
 */
public class LoginResponse implements SocketClientCommand {

    private boolean succesful;

    /**
     * Creates a new login response
     * @param succesful indicates whether the login succeeded or failed
     */
    @JsonCreator
    public LoginResponse(@JsonProperty("successful") boolean succesful) {
        this.succesful = succesful;
    }

    /**
     * Applies the command to given target, namely it is a {@code SocketServerConnection}
     *
     * @param target target of the command
     */
    @Override
    public void applyOn(SocketClientCommandTarget target) {
        /* TODO: Check what to do with the response */
    }
}

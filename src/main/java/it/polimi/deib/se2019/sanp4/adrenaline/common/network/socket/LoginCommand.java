package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;

import java.io.IOException;

/**
 * Sent when a user with Socket connection wants to log in
 */
public class LoginCommand implements SocketServerCommand {

    private String username;

    /**
     * Creates a new login command with given username
     * @param username the name of the player who wants to log in
     */
    @JsonCreator
    public LoginCommand(@JsonProperty("username") String username) {
        if (username == null) throw new NullPointerException("Username cannot be null");
        this.username = username;
    }

    /**
     * Returns the username
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Applies the command to given target, namely it is the {@code SocketRemoteView}
     *
     * @param target target of the command
     */
    @Override
    public void applyOn(SocketServerCommandTarget target) {
        SocketServer server = target.getServer();
        RemoteView stub = target.getRemoteView();
        boolean success;

        /* Execute the login on the server */
        try {
            server.playerLogin(username, stub);
            target.setUsername(username);
            success = true; /* Logged in correctly */
        } catch (LoginException e) {
            success = false; /* The name was already taken */
        }

        /* Send the response to the client */
        try {
            target.sendCommand(new LoginResponse(success));
        } catch (IOException e) {
            /* If sending the command fails, do nothing */
        }
    }
}

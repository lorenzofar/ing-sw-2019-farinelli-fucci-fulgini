package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observer;

import java.io.IOException;

/** Describes a connection the client has with the server, regardless of the connection method */
public interface ServerConnection extends Observer {

    /**
     * Determines whether the connection is active or not
     * @return {@code true} if the connection is active, {@code false} otherwise
     */
    boolean isActive();

    /**
     * Connects to the server with provided hostname on the default port
     * @param hostname The hostname to connect to
     * @throws IOException if the connection fails
     */
    void connect(String hostname) throws IOException;


    /**
     * Connects to the server with provided hostname on the provided port
     * @param hostname The hostname to connect to
     * @param port The port to connect to
     * @throws IOException if the connection fails
     */
    void connect(String hostname, int port) throws IOException;

    /**
     * Send a login request to the server
     * @param username The username of the user
     * @throws IOException if the server cannot be reached
     * @throws LoginException if the login fails
     */
    void login(String username) throws IOException, LoginException;

    /**
     * Sends a logout request to the server
     * If the user was not logged in, nothing happens
     * @param username The username of the user
     * @throws IOException if the server cannot be reached
     */
    void logout(String username) throws IOException;

    /**
     * Adds an observer to listen for updates
     * @param observer The object representing the observer, not null
     */
    void addObserver(Observer<ModelUpdate> observer);

    /**
     * Removes an observer from listening for updates
     * If it hasn't previously subscribed, does nothing
     * @param observer The object representing the observer, not null
     */
    void removeObserver(Observer<ModelUpdate> observer);
}

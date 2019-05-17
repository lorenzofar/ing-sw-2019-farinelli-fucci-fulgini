package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.SocketClientCommandTarget;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;

import java.io.IOException;

public class SocketServerConnection implements ServerConnection, SocketClientCommandTarget {

    private final ClientView view;

    public SocketServerConnection() {
        //TODO: Complete constructor
        view = null;
    }

    /**
     * Determines whether the connection is active or not
     *
     * @return {@code true} if the connection is active, {@code false} otherwise
     */
    @Override
    public boolean isActive() {
        /* TODO: Implement this method */
        return false;
    }

    /**
     * Connects to the server with provided hostname on the default port
     *
     * @param hostname The hostname to connect to
     * @throws IOException if the connection fails
     */
    @Override
    public void connect(String hostname) throws IOException {
        /* TODO: Implement this method */
    }

    /**
     * Connects to the server with provided hostname on the provided port
     *
     * @param hostname The hostname to connect to
     * @param port     The port to connect to
     * @throws IOException if the connection fails
     */
    @Override
    public void connect(String hostname, int port) throws IOException {
        /* TODO: Implement this method */
    }

    /**
     * Send a login request to the server
     *
     * @param username The username of the user
     * @throws IOException    if the server cannot be reached
     * @throws LoginException if the login fails
     */
    @Override
    public void login(String username) throws IOException, LoginException {
        /* TODO: Implement this method */
    }

    /**
     * Sends a logout request to the server
     * If the user was not logged in, nothing happens
     *
     * @param username The username of the user
     * @throws IOException if the server cannot be reached
     */
    @Override
    public void logout(String username) throws IOException {
        /* TODO: Implement this method */
    }

    /**
     * Adds an observer to listen for updates
     *
     * @param observer The object representing the observer, not null
     */
    @Override
    public void addObserver(Observer<ModelUpdate> observer) {
        /* TODO: Implement this method */
    }

    /**
     * Removes an observer from listening for updates
     * If it hasn't previously subscribed, does nothing
     *
     * @param observer The object representing the observer, not null
     */
    @Override
    public void removeObserver(Observer<ModelUpdate> observer) {
        /* TODO: Implement this method */
    }

    /**
     * Returns the client view which uses the connection
     *
     * @return client view
     */
    @Override
    public ClientView getClientView() {
        return view;
    }

    /**
     * Send an update/event from an {@link Observable} object.
     *
     * @param event event/update to be sent
     */
    @Override
    public void update(Object event) {
        /* TODO: Implement this method */
    }
}

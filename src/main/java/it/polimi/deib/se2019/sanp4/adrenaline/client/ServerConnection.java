package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.view.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observer;

/** Describes a connection the client has with the server */
public interface ServerConnection {

    /**
     * Determines whether the connection is active or not
     * @return {@code true} if the connection is active, {@code false} otherwise
     */
    boolean isActive();

    /**
     * Connects to the provided hostname
     * @param hostname The hostname to connect to
     */
    void connect(String hostname);


    /**
     * Connects to the provided hostname on the provided port
     * @param hostname The hostname to connect to
     * @param port The port to connect to
     */
    void connect(String hostname, int port);

    /**
     * Send a login request to the server
     * @param username The username of the user
     */
    void login(String username);

    /**
     * Sends a logout request to the server
     * @param username The username of the user
     */
    void logout(String username);

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

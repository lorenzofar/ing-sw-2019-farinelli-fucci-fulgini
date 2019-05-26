package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.model.Model;

/**
 * Represents the Controller of the MVC pattern.
 * It is responsible for running a single instance of the game, in particular:
 * <ul>
 *     <li>Creating the match instance</li>
 *     <li>Managing the player views, including disconnection and reconnection</li>
 *     <li>Manage the Model</li>
 *     <li>Listen for incoming events from the views</li>
 *     <li>Shutting down the match</li>
 * </ul>
 */
public interface Controller extends RemoteObserver<ViewEvent>, Runnable {
    /**
     * Retrieves the model associated to the controller
     * @return The object representing the model
     */
    Model getModel();

    /**
     * Disconnects the remote view of the player with given name.
     *
     * @param username username of the player, not null
     */
    void disconnectRemoteView(String username);

    /**
     * Tries to reconnect the player with given username,
     * if it has been detected that the player is disconnected
     * @param username username of the player
     * @param remote remote view of the player
     * @return {@code true} if the reconnection succeeded, {@code false} otherwise
     */
    boolean reconnectRemoteView(String username, RemoteView remote);

    /**
     * Forces interruption of the running game
     */
    void shutdown();
}

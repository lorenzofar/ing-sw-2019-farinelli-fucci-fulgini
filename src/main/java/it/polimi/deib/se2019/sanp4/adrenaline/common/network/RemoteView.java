package it.polimi.deib.se2019.sanp4.adrenaline.common.network;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

import java.io.IOException;
import java.rmi.Remote;

/** An interface describing the structure of a view */
public interface RemoteView extends RemoteObserver<ModelUpdate>, Remote {

    /**
     * Returns the username of the player associated with this view.
     * It returns {@code null} if the username has not already been set
     * (prior to login)
     * @return username of the player, if it has been set, {@code null} otherwise
     * @throws IOException if the remote call fails
     */
    String getUsername() throws IOException;

    /**
     * Performs the provided request on the view
     * @param request The object representing the request, not null
     * @throws IOException if the remote call fails
     */
    void performRequest(ChoiceRequest request) throws IOException;

    /**
     * Displays the provided message
     * @param text The text of the message, not null
     * @param type The type of the message, not null
     * @throws IOException if the remote call fails
     */
    void showMessage(String text, MessageType type) throws IOException;

    /**
     * Displays the selected scene
     * @param scene The object representing the scene
     * @throws IOException If the remote call fails
     */
    void selectScene(ViewScene scene) throws IOException;

    /**
     * Adds an observer to listen for updates
     * @param observer The object representing the observer, not null
     * @throws IOException if the remote call fails
     */
    void addObserver(RemoteObserver<ViewEvent> observer) throws IOException;

    /**
     * Removes an observer from listening for updates
     * If it hasn't previously subscribed, does nothing
     * @param observer The object representing the observer, not null
     * @throws IOException if the remote call fails
     */
    void removeObserver(RemoteObserver<ViewEvent> observer) throws IOException;

    /**
     * Checks connectivity to the client
     * @throws IOException If there is no connectivity
     */
    void ping() throws IOException;
}

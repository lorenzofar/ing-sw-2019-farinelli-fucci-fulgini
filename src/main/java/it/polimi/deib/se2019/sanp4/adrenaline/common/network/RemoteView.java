package it.polimi.deib.se2019.sanp4.adrenaline.common.network;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ChoiceResponse;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;

/**
 * Interface provided by the view of a remote host.
 * <p>
 * Provides methods to:
 * </p>
 * <ul>
 * <li>Get information about the player owning the view</li>
 * <li>Interact with the user by sending choice requests</li>
 * <li>Show message with various warning levels</li>
 * <li>Select the scene, i.e. the interface that the user sees</li>
 * <li>Check connectivity</li>
 * </ul>
 * <p>
 * Classes implementing this must also accept observers, which will be notified with a
 * {@link ChoiceResponse} whenever a choice is asked with a {@link ChoiceRequest} is performed.
 * The response must have the same UUID as the request.
 * The response is not required anymore if the player owning the view gets suspended, or if the
 * match terminates for any reason.
 * </p>
 */
public interface RemoteView extends RemoteObserver<ModelUpdate>, Remote {

    /**
     * Returns the username of the player associated with this view.
     * It returns {@code null} if the username has not already been set
     * (prior to login)
     *
     * @return username of the player, if it has been set, {@code null} otherwise
     * @throws IOException if the remote call fails
     */
    String getUsername() throws IOException;

    /**
     * Performs the provided request on the view
     *
     * @param request The object representing the request, not null
     * @param <T>     The type of choice
     * @throws IOException if the remote call fails
     */
    <T extends Serializable> void performRequest(ChoiceRequest<T> request) throws IOException;

    /**
     * Displays the provided message
     *
     * @param text The text of the message, not null
     * @param type The type of the message, not null
     * @throws IOException if the remote call fails
     */
    void showMessage(String text, MessageType type) throws IOException;

    /**
     * Displays the selected scene
     *
     * @param scene The object representing the scene
     * @throws IOException If the remote call fails
     */
    void selectScene(ViewScene scene) throws IOException;

    /**
     * Adds an observer to listen for updates
     *
     * @param observer The object representing the observer, not null
     * @throws IOException if the remote call fails
     */
    void addObserver(RemoteObserver<ViewEvent> observer) throws IOException;

    /**
     * Removes an observer from listening for updates
     * If it hasn't previously subscribed, does nothing
     *
     * @param observer The object representing the observer, not null
     * @throws IOException if the remote call fails
     */
    void removeObserver(RemoteObserver<ViewEvent> observer) throws IOException;

    /**
     * Checks connectivity to the client
     *
     * @throws IOException If there is no connectivity
     */
    void ping() throws IOException;
}

package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

import java.io.IOException;

public interface ClientView extends RemoteView {
    /* ===== NETWORK CONNECTION SETUP ====== */

    /**
     * Creates a new socket connection
     *
     * @throws IllegalStateException When the connection is already present
     */
    void setSocketConnection();

    /**
     * Creates a new RMI connection
     *
     * @throws IllegalStateException When the connection is already present
     */
    void setRMIConnection();

    /**
     * Retrieves the server connection used by the client
     *
     * @return The object representing the server connection
     */
    ServerConnection getServerConnection();

    /* ====================================== */

    /**
     * Returns the username of the username associated with this view.
     * It returns {@code null} if the username has not already been set
     * (prior to login)
     *
     * @return username of the username, if it has been set, {@code null} otherwise
     */
    @Override
    String getUsername();

    /**
     * Sets the username of the player owning the view
     *
     * @param username The username of the player
     */
    void setUsername(String username);

    /**
     * Checks connectivity to the client
     *
     * @throws IOException If there is no connectivity
     */
    @Override
    void ping() throws IOException;

    /**
     * Set the rendering engine used by the client     *
     *
     * @param renderer The object representing the rendering engine, not null
     */
    void setRenderer(UIRenderer renderer);

    @Override
    void performRequest(ChoiceRequest request);

    @Override
    void showMessage(String text, MessageType type);

    @Override
    void selectScene(ViewScene scene);

    @Override
    void removeObserver(RemoteObserver<ViewEvent> observer);

    @Override
    void update(ModelUpdate event);
}

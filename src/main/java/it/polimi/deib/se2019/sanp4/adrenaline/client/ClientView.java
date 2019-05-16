package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.io.IOException;

public class ClientView extends RemoteObservable<ViewEvent> implements RemoteView {
    /**
     * The username of the username owning the view
     */
    private String username;
    /**
     * The rendering engine used by the client
     */
    private UIRenderer renderer;
    /**
     * The connection method used to connect to the server
     */
    private ServerConnection serverConnection;

    public ClientView() {
        //TODO: Complete constructor and methods implementation
    }

    /**
     * Set the rendering engine used by the client
     *
     * @param renderer The object representing the rendering engine, not null
     */
    public void setRenderer(UIRenderer renderer) {
        if (renderer == null) {
            throw new NullPointerException("Server connection cannot be null");
        }
        this.renderer = renderer;
    }

    /**
     * Set the connection method used to connect to the server
     *
     * @param serverConnection The object representing the connection, not nu
     */
    public void setServerConnection(ServerConnection serverConnection) {
        if (serverConnection == null) {
            throw new NullPointerException("Server connection cannot be null");
        }
        this.serverConnection = serverConnection;
    }

    /**
     * Retrieves the server connection used by the client
     *
     * @return The object representing the server connection
     */
    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    /**
     * Returns the username of the username associated with this view.
     * It returns {@code null} if the username has not already been set
     * (prior to login)
     *
     * @return username of the username, if it has been set, {@code null} otherwise
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the player owning the view
     *
     * @param username The username of the player
     */
    public void setUsername(String username) {
        if (username == null) {
            throw new NullPointerException("Username cannot be null");
        }
        this.username = username;
    }

    /**
     * Checks connectivity to the client
     *
     * @throws IOException If there is no connectivity
     */
    @Override
    public void ping() throws IOException {
         /* This method actually does nothing: it only exists because if an object with a remote reference to this
        calls it, it would get a RemoteException if there is no connection. */
    }

    @Override
    public void performRequest(ChoiceRequest request) {
        /* TODO: Implement this method */
    }

    @Override
    public void showMessage(String text, MessageType type) {
        /* TODO: Implement this method */
    }

    @Override
    public void removeObserver(RemoteObserver<ViewEvent> observer) {
        /* TODO: Implement this method */
    }

    @Override
    public void update(ModelUpdate event) {
        /* TODO: Implement this method */
    }
}

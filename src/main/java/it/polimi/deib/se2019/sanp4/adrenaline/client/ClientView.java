package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

import java.io.Serializable;

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
    /**
     * The manager of the local model
     */
    private ModelManager modelManager;

    public ClientView() {
        this.modelManager = new ModelManager(this);
    }

    /**
     * Retrieves the manager of the local model
     *
     * @return The object representing the manager
     */
    public ModelManager getModelManager() {
        return modelManager;
    }

    /* ===== NETWORK CONNECTION SETUP ====== */

    /**
     * Creates a new socket connection
     *
     * @throws IllegalStateException When the connection is already present
     */
    public void setSocketConnection() {
        if (this.serverConnection != null) {
            throw new IllegalStateException("Server connection is already set");
        }
        this.serverConnection = new SocketServerConnection(this);
    }

    /**
     * Creates a new RMI connection
     *
     * @throws IllegalStateException When the connection is already present
     */
    public void setRMIConnection() {
        if (this.serverConnection != null) {
            throw new IllegalStateException("Server connection is already set");
        }
        this.serverConnection = new RMIServerConnection(this);
    }

    /**
     * Retrieves the server connection used by the client
     *
     * @return The object representing the server connection
     */
    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    /* ====================================== */

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
     */
    @Override
    public void ping() {
         /* This method actually does nothing: it only exists because if an object with a remote reference to this
        calls it, it would get a RemoteException if there is no connection. */
    }

    /**
     * Retrieves the rendering engine used by the client
     *
     * @return The object representing the rendering engine
     */
    public UIRenderer getRenderer() {
        return renderer;
    }

    /**
     * Set the rendering engine used by the client     *
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
     * Performs the provided request on the view
     *
     * @param request The object representing the request, not null
     */
    @Override
    public <T extends Serializable> void performRequest(ChoiceRequest<T> request) {
        //TODO: Implement this method
    }

    @Override
    public void showMessage(String text, MessageType type) {
        renderer.showMessage(text, type);
    }

    @Override
    public void selectScene(ViewScene scene) {
        if (scene == ViewScene.LOBBY) {
            renderer.showLobby();
        } else if (scene == ViewScene.MATCH_STARTING) {
            renderer.startWaitingMatch();
        } else if (scene == ViewScene.MATCH_START_CANCELLED) {
            renderer.cancelWaitingMatch();
        }
        //TODO: Implement more scenes
    }

    @Override
    public void update(ModelUpdate event) {
        /* TODO: Implement this method */
    }
}

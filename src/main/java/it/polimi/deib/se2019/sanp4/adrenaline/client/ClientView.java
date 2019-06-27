package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.SelectionHandler;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientView extends RemoteObservable<ViewEvent> implements RemoteView {

    private static final Logger logger = Logger.getLogger(ClientView.class.getName());

    /**
     * The username of the player owning the view
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
    /**
     * The class responsible of refreshing the game screen according to received updates
     */
    private RenderingManager renderingManager;
    /**
     * The choice request that is currently being handled
     */
    private ChoiceRequest currentRequest;
    /**
     * The deque of requests that are waiting to be handled
     * It is managed with a FIFO policy
     */
    private Deque<ChoiceRequest> pendingRequests;
    /**
     * The current selection handler
     */
    private SelectionHandler selectionHandler;
    /**
     * The scene the view is currently into
     */
    private ViewScene scene;

    public ClientView() {
        this.modelManager = new ModelManager();
        // Create a new model
        this.renderingManager = new RenderingManager(this);
        this.currentRequest = null;
        pendingRequests = new ArrayDeque<>();
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

    /**
     * Retrieves the current scene of the view
     *
     * @return The object representing the scene
     */
    public ViewScene getScene() {
        return scene;
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
     * If a null request is provided, it is discarded
     *
     * @param request The object representing the request
     */
    @Override
    public <T extends Serializable> void performRequest(ChoiceRequest<T> request) {
        if (request == null) {
            return;
        }
        if (currentRequest == null) {
            // Cancel the current selection if a new request arrives and has to be handled immediately
            renderer.cancelSelection();
            this.currentRequest = request;
            request.accept(renderer);
        } else {
            pendingRequests.push(request);
        }
    }

    /**
     * Method to be called when a request is handled,
     * in order to consider the next pending request (if present)
     */
    public void onRequestCompleted() {
        try {
            performRequest(pendingRequests.pop());
        } catch (NoSuchElementException e) {
            currentRequest = null;
        } finally {
            //TODO: Refresh the rendered match screen
        }
    }

    /**
     * Retrieves the currently pending request
     *
     * @return The object representing the request
     */
    public ChoiceRequest getCurrentRequest() {
        return this.currentRequest;
    }

    @Override
    public void showMessage(String text, MessageType type) {
        renderer.showMessage(text, type);
    }

    @Override
    public void selectScene(ViewScene scene) {
        // We first cancel a pending selection
        renderer.cancelSelection();
        // Then we update our reference to the current scene
        this.scene = scene;
        switch (scene) {
            case LOGIN:
                break;
            case LOBBY:
                renderer.showLobby();
                break;
            case WAITING_REJOIN:
                renderer.showRejoinScreen();
                break;
            case TURN_PLAYING:
                renderer.setActiveScreen();
                break;
            case TURN_IDLE:
                renderer.setIdleScreen();
                break;
            case FINAL_SCORES:
                renderer.showLeaderBoard();
                break;
            case DISCONNECTED:
                renderer.showDisconnectedScreen();
                break;
            default:
                logger.log(Level.SEVERE, "Unexpected scene {0}", scene.name());
        }
    }

    @Override
    public void update(ModelUpdate event) {
        event.accept(modelManager);
        // We prevent the renderer to render updates when we are waiting to rejoin the match
        event.accept(renderingManager);
    }

    /**
     * Sends the given event to all subscribed observers (i.e. calls {@link Observer#update(Object)} on them).
     * If calling {@code update()} throws an {@code IOException}, the exception is simply ignored.
     *
     * @param event event to be sent to observers
     */
    @Override
    public void notifyObservers(ViewEvent event) {
        super.notifyObservers(event);
    }

    /**
     * Retrieves the selection handler that is currently being used to reply to a request
     *
     * @return The object representing the selection handler
     */
    public SelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    /**
     * Sets the selection handler that is currently being used to reply to a request
     * If a selection handler is already set, it is cancelled and replaced with the provided one
     *
     * @param selectionHandler The object representing the selection handler
     */
    public void setSelectionHandler(SelectionHandler selectionHandler) {
        // Check whether there is already a selection handler before setting the new one
        if (this.selectionHandler != null) {
            // If yes, cancel the pending selection prior to asking the new one
            this.selectionHandler.cancel();
        }
        this.selectionHandler = selectionHandler;
        if (this.selectionHandler != null) {
            this.selectionHandler.setClientView(this);
        }
    }
}

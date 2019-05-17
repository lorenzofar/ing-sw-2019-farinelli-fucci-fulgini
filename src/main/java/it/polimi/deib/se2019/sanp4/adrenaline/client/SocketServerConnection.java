package it.polimi.deib.se2019.sanp4.adrenaline.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.LogoutCommand;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.SocketClientCommandTarget;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.SocketServerCommand;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketServerConnection extends Observable<ModelUpdate>
        implements ServerConnection, SocketClientCommandTarget {

    /** The client view which uses this connection */
    private final ClientView view;

    /** The socket to communicate to the server */
    private Socket socket;

    /** Socket output stream */
    private OutputStream out;

    /** Socket input stream */
    private InputStream in;

    /* Commodities */
    private static final ObjectMapper objectMapper = JSONUtils.getObjectMapper();
    private static final Logger logger = Logger.getLogger(SocketServerConnection.class.getName());

    /* TODO: Remove this constructor */
    public SocketServerConnection() {
        view = null;
    }

    public SocketServerConnection(ClientView view) {
        this.view = view;
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
        /* TODO: Load default port from properties */
        connect(hostname, 3000);
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
        socket = new Socket(hostname, port);
        out = new BufferedOutputStream(socket.getOutputStream());
        in = new BufferedInputStream(socket.getInputStream());
        logger.log(Level.INFO, () -> String.format("Successfully connected to %s:%d", hostname, port));
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
        sendCommand(new LogoutCommand());
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
    public void update(ViewEvent event) {
        /* TODO: Implement this method */
    }

    /**
     * Sends a command to the remote server attached to this target
     *
     * @param command the command that has to be sent
     * @throws IOException if the command cannot be sent due to network problems
     */
    @Override
    public void sendCommand(SocketServerCommand command) throws IOException {
        objectMapper.writeValue(out, command);
    }
}

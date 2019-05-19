package it.polimi.deib.se2019.sanp4.adrenaline.client;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.*;
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
     * Determines whether the connection is active or not.
     * If the connection was considered to be active, but it results inactive,
     * also closes the socket
     *
     * @return {@code true} if the connection is active, {@code false} otherwise
     */
    @Override
    public boolean isActive() {
        /* If it has already been detected that the connection is closed, return false */
        if (isClosed()) return false;

        /* Try to send a ping command to check connectivity */
        try {
            sendCommand(new PingCommand());
            /* The command arrived to the server */
            return true;
        } catch (IOException e) {
            /* The command did not make it to the server */
            close(); /* Explicitly close the connection */
            return false;
        }
    }

    /**
     * Checks if the socket is null or closed.
     * Note that if this methods returns {@code false} it does not mean that
     * the connection is active (calling {@link #isActive()} may return {@code false}, too)
     * @return {@code true} if the socket is null or closed
     */
    private boolean isClosed() {
        return socket == null || socket.isClosed();
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
        /* Check if the connection is active */
        if (isActive()) throw new IOException("The socket is already connected");

        /* If not, create a new socket */
        socket = new Socket(hostname, port);
        out = new BufferedOutputStream(socket.getOutputStream());
        in = new BufferedInputStream(socket.getInputStream());
        logger.log(Level.INFO, () -> String.format("Successfully connected to %s:%d", hostname, port));
    }

    /**
     * Attempts to close the connection with the server.
     * If the connection was active, it will be closed,
     * if it was already inactive or closed, nothing will happen.
     * Subsequent calls to {@link #isActive()} will return {@code false}
     * and calls to {@link #isClosed()} will return {@code true}
     */
    @Override
    public void close() {
        if (!isClosed()) {
            logger.log(Level.INFO, "Closing socket connection");
            try {
                socket.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not close socket connection", e);
            }
        }
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
     * @throws IOException if the remote call fails
     */
    @Override
    public void update(ViewEvent event) throws IOException {
        /* Notify the event received from the ClientView to the observers of the RemoteView */
       sendCommand(new NotifyEventCommand(event));
    }

    /**
     * Sends a command to the remote server attached to this target.
     * It also detects if there is a connection problem and closes the socket
     *
     * @param command the command that has to be sent
     * @throws IOException if the command cannot be sent due to network problems
     */
    @Override
    public void sendCommand(SocketServerCommand command) throws IOException {
        try {
            /* Try to send the command */
            objectMapper.writeValue(out, command);
        } catch (JsonGenerationException | JsonMappingException e){
            /* This is just a problem with Jackson, not with the connection */
            logger.log(Level.SEVERE, "Could not serialize command");
            throw e;
        } catch (IOException e) {
            /* This is a problem with the connection */
            close(); /* Explicitly close the socket */
            throw e;
        }
    }
}

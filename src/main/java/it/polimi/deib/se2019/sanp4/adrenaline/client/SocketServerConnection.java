package it.polimi.deib.se2019.sanp4.adrenaline.client;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.AdrenalineProperties;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.*;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a connection to the server implemented by using TCP sockets.
 * <p>
 * The socket stream is used in a line-based fashion.
 * The data transmitted via the socket is serialized in JSON format (by Jackson).
 * </p>
 * This connection executes in two threads:
 * <ul>
 * <li>A thread that listens for incoming commands for the client</li>
 * <li>A thread that executes the command</li>
 * </ul>
 */
public class SocketServerConnection extends RemoteObservable<ModelUpdate>
        implements ServerConnection, SocketClientCommandTarget {

    /**
     * The client view which uses this connection
     */
    private final ClientView view;

    /**
     * The socket to communicate to the server
     */
    private Socket socket;

    /**
     * Socket output stream
     */
    private OutputStream out;

    /**
     * Scanner for reading from input stream
     */
    private Scanner scanner;

    /**
     * Executor for incoming commands
     */
    private ExecutorService commandExecutor = Executors.newSingleThreadExecutor();

    /**
     * Future task representing the listener of incoming commands
     */
    private Thread listener;

    /* Commodities */
    private static final ObjectMapper objectMapper = JSONUtils.getNetworkObjectMapper();
    private static final Logger logger = Logger.getLogger(SocketServerConnection.class.getName());

    /**
     * Creates a new SocketServerConnection bound to the given view.
     * This means that commands coming from the server will be applied
     * to this view
     *
     * @param view the view which uses this connection
     */
    public SocketServerConnection(ClientView view) {
        this.view = view; /* Set the view to use */
        this.addObserver(view); /* The view observes this for incoming updates from the model */
        view.addObserver(this); /* This observes the view to get events which will be sent to the controller */
    }

    /* ======= NETWORK METHODS =========== */

    /**
     * Determines whether the connection is active or not.
     * If the connection was considered to be active, but it results inactive,
     * also closes the socket
     *
     * @return {@code true} if the connection is active, {@code false} otherwise
     */
    @Override
    public boolean isActive() {
        /* Try to send a ping command to check connectivity */
        try {
            sendCommand(new PingCommand());
            /* The command arrived to the server */
            return true;
        } catch (IOException e) {
            /* The command did not make it to the server */
            return false;
        }
    }

    /**
     * Checks if the socket is null or closed.
     * Note that if this methods returns {@code false} it does not mean that
     * the connection is active (calling {@link #isActive()} may return {@code false}, too)
     *
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
        int port = Integer.parseInt((String) AdrenalineProperties.getProperties()
                .getOrDefault("adrenaline.socketport", "3000"));
        connect(hostname, port);
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
        /* Bind the streams */
        out = new BufferedOutputStream(socket.getOutputStream());
        InputStream in = new BufferedInputStream(socket.getInputStream());
        /* Create scanner from the stream */
        scanner = new Scanner(in);
        logger.log(Level.INFO, () -> String.format("Successfully connected to %s:%d", hostname, port));

        /* Setup the listener for incoming commands */
        listener = new Thread(() -> {
            /* Listens for incoming commands and executes them */
            SocketClientCommand nextCommand;
            do {
                nextCommand = receiveCommand();
                if (nextCommand != null) {
                    executeAsync(nextCommand);
                }
            } while (nextCommand != null);
            logger.log(Level.FINE, "Closing input listener");
        });
    }

    /**
     * Attempts to close the connection with the server.
     * If the connection was active, it will be closed,
     * if it was already inactive or closed, nothing will happen.
     * Subsequent calls to {@link #isActive()} will return {@code false}
     * and calls to {@link #isClosed()} will return {@code true}
     * <p>
     * It both closes the socket and stops the listener for incoming commands
     */
    @Override
    public void close() {
        if (socket == null) return;
        logger.log(Level.INFO, "Closing socket connection");
        /* Interrupt the listener for incoming commands */
        listener.interrupt();
        /* Close the socket */
        try {
            socket.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not close socket connection", e);
        }
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
        if (!isClosed()) {
            try {
                /* Serialize the command to a line */
                String line = objectMapper.writeValueAsString(command) + "\n";
                logger.log(Level.FINER, "Sending command: {0}", line);
                /* Try to send the command */
                out.write(line.getBytes());
                out.flush();
            } catch (JsonGenerationException | JsonMappingException e) {
                /* This is just a problem with Jackson, not with the connection */
                logger.log(Level.SEVERE, "Could not serialize command: ", e);
            } catch (IOException e) {
                /* This is a problem with the connection */
                close();
                throw e; /* Rethrow exceptions so the caller of the command knows of the dead connection */
            }
        } else {
            throw new IOException("Connection is closed");
        }
    }

    /**
     * Receive the next command coming from the server, read from socket input.
     * Note that this call is blocking.
     * As a side effect, this method also closes the connection if it detects
     * network problems while reading from the network stream
     *
     * @return the command from the server or {@code null} if the connection failed
     */
    private SocketClientCommand receiveCommand() {
        if (!isClosed()) {
            try {
                String s = scanner.nextLine();
                logger.log(Level.FINER, () -> String.format("Received command: %s", s));
                return objectMapper.readValue(s, SocketClientCommand.class);
            } catch (JsonProcessingException e) {
                logger.log(Level.WARNING, "Jackson could not deserialize command", e);
            } catch (IOException | NoSuchElementException | IllegalStateException e) {
                /* This is a problem with the connection */
                /* In either case, we interpret the connection as closed */
                close(); /* Explicitly close the socket */
            }
        }
        return null;
    }

    /**
     * Executes given command in a separate thread
     *
     * @param command the command to be executed
     */
    private void executeAsync(SocketClientCommand command) {
        commandExecutor.submit(() -> command.applyOn(this));
    }

    /* ======= SERVER METHODS ======== */

    /**
     * Send a login request to the server
     *
     * @param username The username of the user
     * @throws IOException    if the server cannot be reached
     * @throws LoginException if the login fails
     */
    @Override
    public void login(String username) throws IOException, LoginException {
        if (username == null) throw new LoginException("Please specify an username");

        /* Send the login command */
        sendCommand(new LoginCommand(username));

        /* Wait for the response */
        LoginResponse response = null;
        do {
            SocketClientCommand nextCommand = receiveCommand();
            /* If null report connection closed */
            if (nextCommand == null) {
                throw new IOException("Connection closed");
            }

            if (nextCommand instanceof LoginResponse) {
                /* If we get the response we can handle it */
                response = (LoginResponse) nextCommand;
            } else {
                /* If we get other commands (e.g. ping), we can execute them asynchronously */
                executeAsync(nextCommand);
            }
        } while (response == null);

        /* Handle the response */
        if (!response.isSuccesful()) {
            throw new LoginException("Login failed, please try again");
        } else {
            /* Start listening for server commands in a separate thread */
            listener.start();
        }
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
}

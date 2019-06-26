package it.polimi.deib.se2019.sanp4.adrenaline.server;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.*;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class representing a remote view connected via a socket connection
 */
public class SocketRemoteView extends RemoteObservable<ViewEvent>
        implements RemoteView, Runnable, SocketServerCommandTarget {
    /** The socket to communicate to the client */
    private Socket socket;

    /** The server who accepted the connection */
    private SocketServer server;

    /** Socket output stream */
    private OutputStream out;

    /** Scanner for reading from input stream */
    private Scanner scanner;

    /** Username of the player this view belongs to */
    private String username;

    /** Executor for incoming commands */
    private ExecutorService commandExecutor = Executors.newSingleThreadExecutor();

    /* Commodities */
    private static final ObjectMapper objectMapper = JSONUtils.getNetworkObjectMapper();
    private static final Logger logger = Logger.getLogger(SocketRemoteView.class.getName());

    /**
     * Creates a stub of the view communicating via socket
     *
     * @param socket the socket to communicate with the client
     * @param server the server who accepted the connection
     */
    SocketRemoteView(Socket socket, SocketServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        /* Bind the streams */
        out = new BufferedOutputStream(socket.getOutputStream());
        InputStream in = new BufferedInputStream(socket.getInputStream());
        /* Create scanner from the stream */
        scanner = new Scanner(in);
    }

    /**
     * Running the connection consists in the following loop:
     * <ol>
     * <li>wait for a command from the client</li>
     * <li>deserialize the command</li>
     * <li>execute the command</li>
     * <li>wait for the next command</li>
     * </ol>
     * The loop interrupts when the connection closes
     * Then there is a loop which runs on another thread to keep alive the connection
     */
    @Override
    public void run() {
        /* NOTE: The server has just accepted the connection */
        try {
            /* Set TCP keepalive option, this way we don't have to send the ping command manually */
            socket.setKeepAlive(true);
            while (!socket.isClosed()) {
                listenLoop();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, String.format("Connection problems on client \"%s\"", username), e);
        }
        /* When finished close the connection */
        closeConnection();
        logger.log(Level.FINER, "End of connection thread");
    }

    /** See {@link #run()} */
    private void listenLoop() throws IOException {
        try {
            /* Wait for incoming command as string */
            String s = scanner.nextLine();
            logger.log(Level.FINER, () -> String.format("Received command: %s", s));
            /* Wait for the incoming command and deserialize it */
            SocketServerCommand command = objectMapper.readValue(s, SocketServerCommand.class);

            /* Execute the command in a separate thread */
            executeAsync(command);

            /* Go on with another iteration of the cycle */
        } catch (JsonParseException e) {
            logger.log(Level.WARNING, "Could not parse incoming JSON", e);
        } catch (JsonMappingException e) {
            logger.log(Level.WARNING, "Could not unmarshall incoming command", e);
        } catch (NullPointerException e) {
            logger.log(Level.WARNING, "Cannot execute null command", e);
        } catch (NoSuchElementException | IllegalStateException e) {
            /* This is a problem when reading from the input stream */
            closeConnection();
        }
    }

    /**
     * Executes the given command in a new thread
     * @param command the command to be executed
     */
    private synchronized void executeAsync(SocketServerCommand command) {
        commandExecutor.submit(() -> command.applyOn(this));
    }

    /**
     * Returns the username of the player associated with this view.
     * It returns {@code null} if the username has not already been set
     * (prior to login)
     *
     * @return username of the player, if it has been set, {@code null} otherwise
     */
    @Override
    public synchronized String getUsername() {
        return username;
    }

    /**
     * Set the username of the remote view
     *
     * @param username name of the user
     */
    @Override
    public synchronized void setUsername(String username) {
        this.username = username;
    }

    /**
     * Performs the provided request on the view
     *
     * @param request The object representing the request, not null
     * @throws IOException if the remote call fails
     */
    @Override
    public <T extends Serializable> void performRequest(ChoiceRequest<T> request) throws IOException {
        sendCommand(new PerformRequestCommand(request));
    }

    /**
     * Shows given message on the client
     *
     * @param text The text of the message, not null
     * @param type The type of the message, not null
     * @throws IOException if the remote call fails
     */
    @Override
    public void showMessage(String text, MessageType type) throws IOException {
        sendCommand(new ShowMessageCommand(text, type));
    }

    @Override
    public void selectScene(ViewScene scene) throws IOException {
        sendCommand(new SelectSceneCommand(scene));
    }

    /**
     * Sends given update to the client
     *
     * @param update update to be sent
     */
    @Override
    public void update(ModelUpdate update) throws IOException {
        sendCommand(new UpdateCommand(update));
    }

    /**
     * Notifies subscribed observers with given event
     *
     * @param event the event to notify to observers
     */
    @Override
    public void notifyEvent(ViewEvent event) {
        notifyObservers(event);
    }

    /**
     * Checks connectivity to the client
     *
     * @throws IOException If there is no connectivity
     */
    @Override
    public void ping() throws IOException {
        sendCommand(new PingCommand());
    }

    /**
     * Sends a command to the client attached to this target,
     * usually as a response to the execution of this command
     * If network problems are detected, this call will also close the underlying socket connection
     *
     * @param command the command that has to be sent
     */
    @Override
    public synchronized void sendCommand(SocketClientCommand command) throws IOException {
        try {
            /* Write the command as a line in the output stream */
            String line = objectMapper.writeValueAsString(command) + "\n";
            logger.log(Level.FINER, "Sending command: {0}", line);
            out.write(line.getBytes());
            out.flush();
        } catch (JsonProcessingException e) {
            logger.log(Level.WARNING, "Jackson could not serialize command", e);
        } catch (IOException e) {
            /* This is a problem with the connection */
            closeConnection();
            throw e; /* Rethrow exceptions so the caller of the command knows of the dead connection */
        }
    }

    /**
     * Returns an object which can be used as a stub for the view
     *
     * @return an object which can be used as a stub for the view
     */
    @Override
    public RemoteView getRemoteView() {
        return this;
    }

    /**
     * Returns the server
     *
     * @return the server instance
     */
    @Override
    public SocketServer getServer() {
        return server;
    }

    /**
     * Closes the socket connection
     */
    private synchronized void closeConnection() {
        if (!socket.isClosed()) {
            try {
                logger.log(Level.FINE, "Closing connection for player \"{0}\"", username);
                socket.close();
            } catch (IOException e) {
                logger.log(Level.FINE, "Could not close connection", e);
            }
        }
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.server;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.*;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

import java.io.*;
import java.net.Socket;
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

    /** Socket input stream */
    private InputStream in;

    /** Username of the player this view belongs to */
    private String username;

    /* Commodities */
    private static final ObjectMapper objectMapper = JSONUtils.getObjectMapper();
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
        in = new BufferedInputStream(socket.getInputStream());
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
     */
    @Override
    public void run() {
        /* NOTE: The server has just accepted the connection */
        try {
            while (!socket.isClosed()) {
                commandLoop();
            }
        } catch (IOException e) {
            /* TODO: Handle disconnection */
        }
    }

    /** See {@link #run()} */
    private void commandLoop() throws IOException {
        try {
            /* Wait for the incoming command and deserialize it */
            SocketServerCommand command = objectMapper.readValue(in, SocketServerCommand.class);

            /* Now apply the command */
            command.applyOn(this);

            /* Go on with another iteration of the cycle */
        } catch (JsonParseException e) {
            logger.log(Level.WARNING, "Could not parse incoming JSON", e);
        } catch (JsonMappingException e) {
            logger.log(Level.WARNING, "Could not unmarshall incoming command", e);
        } catch (NullPointerException e) {
            logger.log(Level.WARNING, "Cannot execute null command", e);
        }
    }

    /**
     * Returns the username of the player associated with this view.
     * It returns {@code null} if the username has not already been set
     * (prior to login)
     *
     * @return username of the player, if it has been set, {@code null} otherwise
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Set the username of the remote view
     *
     * @param username name of the user
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Performs the provided request on the client
     *
     * @param request The object representing the request, not null
     */
    @Override
    public void performRequest(ChoiceRequest request) throws IOException {
        sendCommand(new PerformRequestCommand(request));
    }

    /**
     * Shows given message on the client
     *
     * @param text The text of the message, not null
     * @param type The type of the message, not null
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
     * Sends a command to the client attached to this target,
     * usually as a response to the execution of this command
     *
     * @param command the command that has to be sent
     * @throws IOException if the command cannot be sent due to network problems
     */
    @Override
    public void sendCommand(SocketClientCommand command) throws IOException {
        objectMapper.writeValue(out, command);
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
        /* TODO: Implement this method */
    }
}

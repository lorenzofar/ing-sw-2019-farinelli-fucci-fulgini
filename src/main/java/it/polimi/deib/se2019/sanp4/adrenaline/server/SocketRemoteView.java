package it.polimi.deib.se2019.sanp4.adrenaline.server;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.SocketClientCommand;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.SocketServer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.SocketServerCommandTarget;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.net.Socket;

/**
 * A class representing a remote view connected via a socket connection
 */
public class SocketRemoteView extends RemoteObservable<ViewEvent>
        implements RemoteView, Runnable, SocketServerCommandTarget {

    /** The socket to communicate to the client */
    private Socket socket;

    /** The server who accepted the connection */
    private SocketServer server;

    /**
     * Creates a stub of the view communicating via socket
     * @param socket the socket to communicate with the client
     * @param server the server who accepted the connection
     */
    SocketRemoteView(Socket socket, SocketServer server){
        this.socket = socket;
        this.server = server;
        //TODO: Complete constructor and method implementation
    }

    /**
     * Runs the connection after it has been established.
     */
    @Override
    public void run() {
        /* TODO: Implement this method */
    }

    /**
     * Performs the provided request on the client
     * @param request The object representing the request, not null
     */
    @Override
    public void performRequest(ChoiceRequest request) {
        /* TODO: Implement this method */
    }

    /**
     * Shows given message on the client
     * @param text The text of the message, not null
     * @param type The type of the message, not null
     */
    @Override
    public void showMessage(String text, MessageType type) {
        /* TODO: Implement this method */
    }

    /**
     * Sends given update to the client
     * @param update update to be sent
     */
    @Override
    public void update(ModelUpdate update) {
        /* TODO: Implement this method */
    }

    /**
     * Sends a command to the client attached to this target,
     * usually as a response to the execution of this command
     *
     * @param command the command that has to be sent
     */
    @Override
    public void sendCommand(SocketClientCommand command) {
        /* TODO: Implement this method */
        /* QUESTION: Should this be async? */
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
}

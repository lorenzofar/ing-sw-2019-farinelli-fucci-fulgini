package it.polimi.deib.se2019.sanp4.adrenaline.server;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.SocketServer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.Request;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.io.IOException;
import java.net.Socket;

/**
 * A class representing a remote view connected via a socket connection
 */
public class SocketRemoteView extends RemoteObservable<ViewEvent> implements RemoteView, Runnable {

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

    @Override
    public void performRequest(Request request) {
        /* TODO: Implement this method */
    }

    @Override
    public void showMessage(String text, MessageType type) {
        /* TODO: Implement this method */
    }

    @Override
    public void update(ModelUpdate event) throws IOException {
        /* TODO: Implement this method */
    }
}

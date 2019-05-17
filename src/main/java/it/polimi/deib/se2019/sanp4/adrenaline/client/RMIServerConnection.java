package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteServer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMIServerConnection extends Observable<ModelUpdate> implements ServerConnection {
    private RemoteServer server;
    private ClientView view;
    private RemoteView viewStub;

    private static final Logger logger = Logger.getLogger(RMIServerConnection.class.getName());

    /**
     * Creates an disconnected RMI connection
     * @param view The view using this connection
     */
    public RMIServerConnection(ClientView view){
        this.view = view;
        //TODO: Complete constructor and finish implementation of methods
    }

    @Override
    public boolean isActive() {
        if (server == null) return false;
        try {
            server.ping();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void connect(String hostname) throws IOException {
        /* TODO: Load a default port from configuration */
        int defaultPort = 1099;
        connect(hostname, defaultPort);
    }

    @Override
    public void connect(String hostname, int port) throws IOException {
        if (isActive()) return; /* No need to connect if the connection is active */

        /* Base url of the remote registry */
        String registryURL = String.format("rmi://%s:%d/", hostname, port);

        logger.log(Level.FINE, "Trying to connect to {0}", registryURL);
        try {
            /* Attempt to retrieve the server */
            server = (RemoteServer) Naming.lookup(registryURL + "adrenaline/server");
            logger.log(Level.INFO, "Successfully connected to RMI server at {0}", registryURL);
            /* Export the view and save it in a local attribute, it will be later sent to the server */
            viewStub = (RemoteView) UnicastRemoteObject.exportObject(view, 0);
        } catch (NotBoundException e) {
            throw new IOException("Could connect to the remote registry, but the server is not bound", e);
        }
    }

    @Override
    public void login(String username) throws IOException, LoginException {
        if (server == null) throw new IOException("Connection is not active");

        /* Attempt to log in with given username */
        server.playerLogin(username, viewStub);
    }

    @Override
    public void logout(String username) throws IOException {
        if (server == null) throw new IOException("Connection is not active");

        /* Attempt to log out with given username */
        server.playerLogout(username);
    }

    @Override
    public void update(ViewEvent event) {
        /* TODO: Handle updates coming from ClientView, if we need to */
    }
}

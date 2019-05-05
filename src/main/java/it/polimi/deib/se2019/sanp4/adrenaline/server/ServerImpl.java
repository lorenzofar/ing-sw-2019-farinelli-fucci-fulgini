package it.polimi.deib.se2019.sanp4.adrenaline.server;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteServer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.SocketServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class representing the implementation of a server
 * it implements both the server and remote server interfaces
 */
public class ServerImpl implements SocketServer, RemoteServer, Runnable {
    private static final int DEFAULT_SOCKET_THREADS = 64;

    /** Used to accept Socket connections */
    private ServerSocket serverSocket;

    /** Used to manage Socket connections in separate threads */
    private ExecutorService executor = Executors.newFixedThreadPool(DEFAULT_SOCKET_THREADS);

    /** Map with view for each player */
    private ConcurrentMap<String, RemoteView> playerViews;

    private static final Logger logger = Logger.getLogger(ServerImpl.class.getName());

    public ServerImpl(){
        playerViews = new ConcurrentHashMap<>();
        //TODO: Complete constructor and methods implementation
    }

    /**
     * Runs the server, after it has been created and properly set up.
     * The server starts listening for both RMI and Socket connections
     */
    @Override
    public void run() {
        /* TODO: Load ports from external config */
        startRMI(1099);
        startSocket(3000);
    }

    /**
     * Creates an RMI registry on this host and binds the server on {@code adrenaline/server} so it can
     * be retrieved by remote clients.
     * @param port the port to start the registry on
     */
    private void startRMI(int port) {
        try {
            /* Get the IP address of the server */
            /* TODO: Find a better way */
            String hostname = InetAddress.getLocalHost().getHostAddress();
            System.setProperty("java.rmi.server.hostname", hostname);

            /* Create RMI registry */
            logger.log(Level.INFO, () -> String.format("Creating RMI registry on %s:%d", hostname, port));
            Registry registry = LocateRegistry.createRegistry(port);

            /* Crete the stub and register it */
            RemoteServer stub = (RemoteServer) UnicastRemoteObject.exportObject(this, 0);
            registry.rebind("adrenaline/server", stub);
            logger.log(Level.FINE, "Successfully published server on RMI registry");
        } catch (RemoteException e) {
            logger.log(Level.SEVERE, "Cannot start RMI registry", e.detail);
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "Cannot get local host", e);
        }
    }

    /**
     * Creates a server socket and accepts incoming connections.
     * @param port the port on which to listen for incoming connections
     */
    private void startSocket(int port) {
        try {
            serverSocket = new ServerSocket(port);
            logger.log(Level.INFO, "Listening for socket connections on port {0}", port);
            while (!serverSocket.isClosed()) acceptConnection();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Cannot create socket on port {0}", port);
        }
    }

    /**
     * Accepts incoming connection on socket and runs it in a separate thread using the executor.
     */
    private void acceptConnection(){
        try {
            Socket client = serverSocket.accept();
            logger.log(Level.FINE, "Accepting connection from {0}", client.getInetAddress().getHostAddress());
            /* Create the view and execute it in a separate thread */
            SocketRemoteView view = new SocketRemoteView(client, this);
            executor.submit(view);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to connect client", e);
        }
    }

    @Override
    public void playerLogin(String username, RemoteView view) throws IOException, LoginException {
        /* TODO: Implement this method */
    }

    @Override
    public void playerLogout(String username) throws IOException {
        /* TODO: Implement this method */
    }

    @Override
    public void ping() {
        /* This method actually does nothing: it only exists because if an object with a remote reference to this
        calls it, it would get a RemoteException if there is no connection. */
    }
}

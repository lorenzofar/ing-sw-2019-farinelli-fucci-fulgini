package it.polimi.deib.se2019.sanp4.adrenaline.server;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteServer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.SocketServer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.CallbackInterface;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.GameTimer;

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
public class ServerImpl implements SocketServer, RemoteServer, Runnable, CallbackInterface {
    private static final int DEFAULT_SOCKET_THREADS = 64;

    /**
     * Load the time to wait before starting a new game
     * Fall back to a default value of 3 minutes if none is set
     */
    private static final int WAITING_TIME = (int)ServerProperties.getProperties().getOrDefault("adrenaline.waitingtime", 1800);

    private static final int MIN_GAME_PLAYERS = 3;

    /** Used to accept Socket connections */
    private ServerSocket serverSocket;

    /** Used to manage Socket connections in separate threads */
    private ExecutorService executor = Executors.newFixedThreadPool(DEFAULT_SOCKET_THREADS);

    private boolean gameActive; //TODO: Check whether to keep using this

    private GameTimer waitingGameTimer = new GameTimer(this, WAITING_TIME);

    /** Map with view for each player */
    private ConcurrentMap<String, RemoteView> playerViews;

    private static final Logger logger = Logger.getLogger(ServerImpl.class.getName());

    public ServerImpl(){
        playerViews = new ConcurrentHashMap<>();
        gameActive = false;
        //TODO: Complete constructor and methods implementation
    }

    /**
     * Runs the server, after it has been created and properly set up.
     * The server starts listening for both RMI and Socket connections
     */
    @Override
    public void run() {
        int rmiPort = (int)ServerProperties.getProperties().getOrDefault("adrenaline.rmiport", 1099);
        int socketPort = (int)ServerProperties.getProperties().getOrDefault("adrenaline.socketport", 3000);
        startRMI(rmiPort);
        startSocket(socketPort);
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
        try{
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
        if(!gameActive && !(waitingGameTimer.isRunning()) && playerViews.entrySet().size() >= MIN_GAME_PLAYERS){
            // There is a sufficient number of players for the game to start
            logger.log(Level.FINE, "Reached sufficient players number ({0}), starting timer...", playerViews.entrySet().size());
            // We start the timer
            waitingGameTimer.start();
        }
    }

    @Override
    public void playerLogout(String username) throws IOException {
        /* TODO: Implement this method */
        // We first check whethere there are still sufficient players for a game
        // Filtering these events when the game has started
        if(!gameActive && waitingGameTimer.isRunning() && playerViews.entrySet().size() < MIN_GAME_PLAYERS){
            logger.log(Level.FINE, "Players count below sufficient threshold ({0}), stopping timer...", playerViews.entrySet().size());
            // We reset the timer
            waitingGameTimer.reset();
        }
    }

    @Override
    public void ping() {
        /* This method actually does nothing: it only exists because if an object with a remote reference to this
        calls it, it would get a RemoteException if there is no connection. */
    }

    @Override
    public void callback() {
        // Here we check whether in the meantime someone disconnected
        // And whether the conditions are right for the game to start
        if(!gameActive && playerViews.entrySet().size() < MIN_GAME_PLAYERS){
            return;
        }
        logger.log(Level.FINE, "Timer expired, starting the game");
        // We then start the game
        //TODO: Finish implementing this method
        gameActive = true;
    }
}

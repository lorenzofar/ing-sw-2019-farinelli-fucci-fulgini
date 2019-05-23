package it.polimi.deib.se2019.sanp4.adrenaline.server;

import it.polimi.deib.se2019.sanp4.adrenaline.common.AdrenalineProperties;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteServer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.SocketServer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.Controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class representing the implementation of a server
 * it implements both the server and remote server interfaces
 * This class uses the singleton pattern
 */
public class ServerImpl implements SocketServer, RemoteServer, Runnable {
    /** Single instance */
    private static ServerImpl ourInstance = new ServerImpl();

    /** Return the instance of the server */
    public static ServerImpl getInstance() {
        return ourInstance;
    }

    private static final int SOCKET_EXECUTOR_THREAD_SIZE = 64;

    /** Used to accept Socket connections */
    private ServerSocket serverSocket;

    /** Used to manage Socket connections in separate threads */
    private ExecutorService socketExecutor = Executors.newFixedThreadPool(SOCKET_EXECUTOR_THREAD_SIZE);

    /** Executor for the lobby */
    private ExecutorService lobbyExecutor = Executors.newSingleThreadExecutor();

    /** The lobby used to handle waiting players */
    private Lobby lobby = new Lobby();

    /**
     * Map with all connected usernames:
     * <ul>
     *     <li>If the player belongs to a started match, the value is the controller of that match</li>
     *     <li>If the player belongs to the lobby the value is {@code null}</li>
     * </ul>
     */
    private ConcurrentMap<String, Controller> playerMatches = new ConcurrentHashMap<>();

    /**
     * The set of usernames in use by players. These cannot be taken
     */
    private Set<String> reservedUsernames = new HashSet<>();

    private static final Logger logger = Logger.getLogger(ServerImpl.class.getName());

    private ServerImpl(){}

    /**
     * Runs the server, after it has been created and properly set up.
     * The server starts listening for both RMI and Socket connections
     */
    @Override
    public void run() {
        /* First start the Lobby */
        lobbyExecutor.submit(lobby);
        /* Then start listening for connections */
        int rmiPort = Integer.parseInt((String) AdrenalineProperties.getProperties().getOrDefault("adrenaline.rmiport", "1099"));
        int socketPort = Integer.parseInt((String) AdrenalineProperties.getProperties().getOrDefault("adrenaline.socketport", "3000"));
        startRMI(rmiPort);
        startSocket(socketPort);
    }

    /* ============= CONNECTIONS ============ */

    /**
     * Creates an RMI registry on this host and binds the server on {@code adrenaline/server} so it can
     * be retrieved by remote clients.
     * @param port the port to start the registry on
     */
    private void startRMI(int port) {
        try {
            /* Get the IP address of the server */
            /* TODO: Find a better way */
            String hostname = (String) AdrenalineProperties.getProperties()
                    .getOrDefault("adrenaline.hostname", InetAddress.getLocalHost().getHostAddress());
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
            socketExecutor.submit(view);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to connect client", e);
        }
    }

    /* ========== LOGIN AND LOGOUT =========== */

    @Override
    public void playerLogin(String username, RemoteView view) throws LoginException {
        if (username == null || username.isEmpty() || view == null) {
            throw new LoginException("Please provide an username");
        }

        if (isUsernameReserved(username)) throw new LoginException("This name is already taken");

        /* If the username is valid then reserve it */
        reserveUsername(username);

        /* Send the player to the Lobby */
        lobby.insertPlayer(username, view);
    }


    /**
     * Request to log out a user.
     * If the user is logged in, it gets logged out and all operations to suspend him are taken care of.
     * If it is not logged in, nothing happens.
     *
     * @param username name of the player to be logged out
     */
    @Override
    public void playerLogout(String username) {
        /* Check whether the player is logged in */
        /* TODO: Tell the controller to disconnect him */
    }

    /**
     * Puts an username in the set of reserved usernames
     * @param username the username which has to be reserved
     */
    synchronized void reserveUsername(String username) {
        reservedUsernames.add(username);
    }

    /**
     * Unreserves a reserved username.
     * If that username had an associated controller, also deletes that
     * @param username the username to be unreserved
     */
    public synchronized void unreserveUsername(String username) {
        playerMatches.remove(username);
        reservedUsernames.remove(username);
    }

    /**
     * Given the username of a player, it returns if that is reserved.
     * An username is reserved if one of the following applies:
     * <ul>
     *     <li>The player has logged in and is in the lobby</li>
     *     <li>The player is in a running match, even if eh is not connected</li>
     * </ul>
     * @param username username of the player
     * @return whether this player is logged in (playing in a running match or in the lobby)
     */
    public boolean isUsernameReserved(String username) {
        return reservedUsernames.contains(username);
    }

    @Override
    public void ping() {
        /* This method actually does nothing: it only exists because if an object with a remote reference to this
        calls it, it would get a RemoteException if there is no connection. */
    }

    /* ========== MATCH HANDLING =======*/

    /**
     * Starts a new match with given players
     * @param players a map with username as the key and the RemoteView of the player as the value
     */
    public void startNewMatch(Map<String, RemoteView> players) {
        logger.log(Level.INFO, () -> String.format("Starting new match from players: %s", players.keySet()));
        /* TODO: Create a controller for this match, from given players */

        /* Set the controller for each player in the map */
    }

    /* ========== GETTERS ============= */

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public ConcurrentMap<String, Controller> getPlayerMatches() {
        return playerMatches;
    }
}

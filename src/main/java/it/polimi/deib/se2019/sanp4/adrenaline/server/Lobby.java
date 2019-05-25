package it.polimi.deib.se2019.sanp4.adrenaline.server;

import it.polimi.deib.se2019.sanp4.adrenaline.common.AdrenalineProperties;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.LobbyUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lobby implements Runnable {

    private static final Logger logger = Logger.getLogger(Lobby.class.getName());

    private boolean stayActive = true;

    /** Minimum number of players to start a match */
    private static final int MIN_PLAYERS = 3;

    /** Maxmimum number of players in a match */
    private static final int MAX_PLAYERS = 5;

    /** Waiting time of the timer, in seconds (default 30 sec.) */
    private static final int WAITING_TIME = Integer.parseInt((String)AdrenalineProperties.getProperties()
            .getOrDefault("adrenaline.waitingtime", "20"));

    /** This will contain the players coming from the server */
    private BlockingQueue<Map.Entry<String, RemoteView>> incomingPlayers = new LinkedBlockingQueue<>();

    /** A map with the views of the players who have been accepted and are waiting to start the match */
    private ConcurrentMap<String, RemoteView> waitingPlayers = new ConcurrentHashMap<>();

    /** Executor service tu run the timer */
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /** The return value of the timer */
    private Future<?> timer;

    /**
     * Inserts an incoming player that has just logged in in the incoming queue.
     * This method assumes that the server has checked that no player
     * with that username (either active or inactive) exists on the server
     * @param username username
     * @param view player
     */
    public void insertPlayer(String username, RemoteView view) {
        incomingPlayers.add(new AbstractMap.SimpleEntry<>(username, view));
    }

    /**
     * Waits for an incoming player from the queue.
     * When it gets the incoming player:
     * <ol>
     *     <li>Inserts it in the waiting list</li>
     *     <li>If there are {@link #MAX_PLAYERS} in the waiting list, creates a new match and empties the waiting list</li>
     *     <li>If there are at least {@link #MIN_PLAYERS} starts a timer which will start the match later</li>
     * </ol>
     * @param incomingPlayer (username, view) pair of a player view received from the server
     */
    synchronized void receiveIncomingPlayer(Map.Entry<String, RemoteView> incomingPlayer) {
        /* When we get the player we put him in the waiting map */
        String username = incomingPlayer.getKey();
        RemoteView view = incomingPlayer.getValue();

        logger.log(Level.INFO, "Got new waiting player: {0}", username);
        waitingPlayers.put(username, view);

        /* Show the lobby scene to the player */
        selectLobbyScene(view);

        /* Notify the players that a new player has connected */
        notifyWaitingList();

        /* Disconnect inactive players */
        disconnectInactive();

        /* First check if we can start straight away */
        if (waitingPlayers.size() == MAX_PLAYERS) {
            stopTimer(); /* Stop any running timer */
            triggerMatchStart();
        } else if (waitingPlayers.size() >= MIN_PLAYERS && !isTimerRunning()) {
            /* If the minimum number of players has been reached and the timer is not started, start it */
            startTimer();
        }
    }

    synchronized void timerCallback() {
        logger.log(Level.INFO, "Callback");
        /* Disconnect all inactive players */
        disconnectInactive();
        /* Check if we still have enough players */
        if (waitingPlayers.size() >= MIN_PLAYERS) {
            triggerMatchStart();
        }
        /* If we do not have minimum number of players, a new timer will be scheduled when we do */
        timer = null; /* Reset the timer */
    }

    synchronized void triggerMatchStart() {
        /* Remove the players from the waiting map and pass them to the server */
        Map<String, RemoteView> players = new HashMap<>(waitingPlayers);
        waitingPlayers.clear();
        ServerImpl.getInstance().startNewMatch(players);
    }

    /* ========== DISCONNECTION ============ */

    /**
     * Checks if there are inactive players in {@code waitingPlayers} and disconnects
     * them, also notifying the other players
     */
    synchronized void disconnectInactive() {
        for (Map.Entry<String, RemoteView> player : waitingPlayers.entrySet()) {
            try {
                /* Send a ping command to check connectivity */
                player.getValue().ping();
            } catch (IOException e) {
                /* Disconnect the player if the ping fails */
                disconnectPlayer(player.getKey());
            }
        }
    }

    /**
     * Disconnects a player and sends a notification to the other waiting players
     * @param username the player who has to be disconnected
     */
    void disconnectPlayer(String username) {
        logger.log(Level.INFO, "Player \"{0}\" does not respond, deleting...", username);
        /* Remove it from local */
        waitingPlayers.remove(username);
        /* Notify the players that a player has disconnected */
        notifyWaitingList();
        ServerImpl.getInstance().unreserveUsername(username);
    }

    /* ========= COMMUNICATE WITH PLAYERS ========== */

    /**
     * Selects the LOBBY scene on given player
     * @param view the view of the player
     */
    void selectLobbyScene(RemoteView view) {
        try {
            view.selectScene(ViewScene.LOBBY);
        } catch (IOException ignore) {
            /* Do nothing */
        }
    }

    /**
     * Sends the current list of waiting players to the waiting players
     */
    void notifyWaitingList() {
        for (RemoteView view : waitingPlayers.values()) {
            try {
                view.update(new LobbyUpdate(new ArrayList<>(waitingPlayers.keySet())));
            } catch (IOException ignore) {
                /* Ignore the exception */
            }
        }
    }

    /* ========== TIMER ========== */

    synchronized boolean isTimerRunning() {
        if (timer == null) {
            return false;
        } else {
            return !timer.isDone();
        }
    }

    synchronized void startTimer() {
        timer = executor.schedule(this::timerCallback, WAITING_TIME, TimeUnit.SECONDS);
    }

    synchronized void stopTimer() {
        if (timer != null) {
            timer.cancel(true);
            timer = null;
        }
    }

    /* ========= RUNNING ========== */

    /**
     * Starts listening for incoming players, until interrupted
     */
    @Override
    public void run() {
        logger.log(Level.FINER, "Running lobby");
        while (stayActive) {
            try {
                /* This call blocks waiting for a player */
                Map.Entry<String, RemoteView> incomingPlayer = incomingPlayers.take();
                receiveIncomingPlayer(incomingPlayer);
            } catch (InterruptedException e) {
                shutdown();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Shuts down this lobby when the thread it runs in is interrupted
     */
    void shutdown() {
        logger.log(Level.INFO, "Shutting down Lobby");
        /* Stop listening for incoming players */
        stayActive = false;
        /* Stop any running timer */
        stopTimer();
    }
}
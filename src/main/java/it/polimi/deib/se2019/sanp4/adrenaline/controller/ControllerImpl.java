package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.match.MatchController;
import it.polimi.deib.se2019.sanp4.adrenaline.model.MatchOperationalState;
import it.polimi.deib.se2019.sanp4.adrenaline.model.Model;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelImpl;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the Controller, which uses {@link PersistentView}s to decorate the
 * player's views.
 * @author Alessandro Fulgini
 */
public class ControllerImpl implements Controller {

    /**
     * Associated model instance
     */
    private final Model model;

    /**
     * A map with (username, view)
     */
    private final Map<String, PersistentView> views;

    private final ConcurrentMap<String, PersistentView> waitingToRejoin;

    private static final Logger logger = Logger.getLogger(ControllerImpl.class.getName());

    /**
     * Creates the controller for a new instance of the game
     *
     * @param remotes a map with (username, remote view)
     */
    public ControllerImpl(Map<String, RemoteView> remotes) {
        /* Create the model instance */
        this.model = new ModelImpl();

        /* Set up the persistent views */
        views = new HashMap<>(remotes.size());
        remotes.forEach(this::setupPersistentView);

        /* Set up the data structure for views waiting to rejoin the match after reconnection */
        waitingToRejoin = new ConcurrentHashMap<>();
    }

    /**
     * Runs an instance of the game
     */
    @Override
    public void run() {
        try {
            /* Ask a player to select the match configuration */
            PersistentView selectedPlayer = views.values().iterator().next();
            MatchConfiguration config = new MatchBootstrapper().askForMatchConfiguration(selectedPlayer);

            /* Create the match with given configuration */
            Set<String> usernames = views.keySet();
            Match match = MatchCreator.createMatch(usernames, config);
            model.setMatch(match);

            /* Create the controller factory */
            ControllerFactory factory = new StandardControllerFactory(match, views);

            /* Send the initial update (with the whole state of the model) to all the views */
            usernames.forEach(model::sendInitialUpdate);

            /* Set the operational state of the match */
            model.setOperationalState(MatchOperationalState.ACTIVE);

            /* Create the match controller and run the match */
            MatchController matchController = factory.createMatchController();
            matchController.setAfterTurnCallback(this::rejoinReconnectedPlayers); /* Rejoin after turn */
            matchController.runMatch();

            /* When the match is over, change the operational state again */
            model.setOperationalState(MatchOperationalState.FINISHED);

            /* Return to the caller (server) */
        } catch (InterruptedException e) {
            shutdown(); /* Force the game to terminate */
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Retrieves the model associated to the controller
     *
     * @return The object representing the model
     */
    @Override
    public Model getModel() {
        return model;
    }

    /**
     * Disconnects the remote view of the player with given name.
     *
     * @param username username of the player, not null
     */
    @Override
    public void disconnectRemoteView(String username) {
        PersistentView view = views.get(username);
        if (view != null) {
            view.disconnectRemoteView();
        }
    }

    /**
     * Tries to reconnect the player with given username,
     * if it has been detected that the player is disconnected
     *
     * @param username username of the player
     * @param remote   remote view of the player
     * @return {@code true} if the reconnection succeeded, {@code false} otherwise
     */
    @Override
    public boolean reconnectRemoteView(String username, RemoteView remote) {
        PersistentView view = views.get(username);
        if (view == null) return false;
        return view.reconnectRemoteView(remote);
    }

    /**
     * Forces interruption of the running game
     * <p>
     * Disconnects all views
     * </p>
     */
    @Override
    public void shutdown() {
        views.values().forEach(PersistentView::disconnectRemoteView);
    }

    /**
     * Send an update/event from a {@link RemoteObservable} object.
     *
     * @param event event/update to be sent
     */
    @Override
    public void update(ViewEvent event) {
        /* This implementation does not receive events from the remote views, it uses the persistent views to do that */
    }

    /* ========= PRIVATE METHODS =========== */

    /**
     * Creates a persistent view which wraps the given remote view,
     * then adds it to the map, subscribes it to the model for updates
     * and sets the callbacks
     *
     * @param username username of the player the view belongs to
     * @param remote   the remote view of the player
     */
    private void setupPersistentView(String username, RemoteView remote) {
        PersistentView view = new PersistentViewImpl(username, remote);
        views.put(username, view);

        /* Subscribe for updates */
        model.addObserver(username, view);

        /* Set callbacks */
        view.setNetworkFaultCallback(() -> {
            logger.log(Level.FINE, "Suspending player \"{0}\" for network fault", username);
            model.suspendPlayer(username);
            return null;
        });

        view.setReconnectionCallback(() -> {
            /* Put this view in the map so it will be picked up after the end of the current turn */
            waitingToRejoin.put(username, view);
            /* Set the proper view */
            view.selectScene(ViewScene.WAITING_REJOIN);
            return null;
        });
    }

    /**
     * Takes the views that have been reconnected, but are waiting to rejoin the match,
     * unsuspends the relative players and sends them the initial update.
     * <p>
     * This is given as a callback to the match controller after executing the turn
     */
    private void rejoinReconnectedPlayers() {
        Set<PersistentView> toRejoin = new HashSet<>(waitingToRejoin.values());

        toRejoin.forEach(view -> {
            logger.log(Level.FINE, "Player \"{0}\" is rejoining the match", view.getUsername());
            model.unsuspendPlayer(view.getUsername());
            model.sendInitialUpdate(view.getUsername());

            /* Remove it from the map */
            waitingToRejoin.remove(view.getUsername());
        });
    }
}

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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the Controller, which uses {@link PersistentView}s to decorate the
 * player's views.
 */
public class ControllerImpl implements Controller {

    /** Associated model instance */
    private final Model model;

    /** A map with (username, view) */
    private final Map<String, PersistentView> views;

    /**
     * Creates the controller for a new instance of the game
     * @param remotes a map with (username, remote view)
     */
    public ControllerImpl(Map<String, RemoteView> remotes) {
        /* Create the model instance */
        this.model = new ModelImpl();

        /* Set up the persistent views */
        views = new HashMap<>(remotes.size());
        remotes.forEach(this::setupPersistentView);
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
     * @param remote     remote view of the player
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
     */
    @Override
    public void shutdown() {
        /* TODO: Implement this method */
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
     * @param username username of the player the view belongs to
     * @param remote the remote view of the player
     */
    private void setupPersistentView(String username, RemoteView remote) {
        PersistentView view = new PersistentViewImpl(username, remote);
        views.put(username, view);

        /* Subscribe for updates */
        model.addObserver(username, view);

        /* Set callbacks */
        view.setNetworkFaultCallback(() -> {
            model.suspendPlayer(username);
            return null;
        });

        view.setReconnectionCallback(() -> {
            model.unsuspendPlayer(username);
            model.sendInitialUpdate(username);
            return null;
        });
    }
}

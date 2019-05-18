package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.Model;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.server.ServerProperties;

import java.util.HashMap;
import java.util.Map;

public class ControllerImpl implements Controller {

    /** The maximum number of squares a player can move */
    public static final int MAX_MOVE_STEPS = 3; //TODO: Check this attribute

    /** The maximum time of a turn, loaded from properties with a default value of 3 minutes */
    private static final int MAX_TURN_TICKS = (int) ServerProperties.getProperties().getOrDefault("adrenaline.turntime", 1800);

    /** Associated model instance */
    private Model model;

    /** A map of the pending request corresponding to each player */
    private Map<Player, RequestContext> pendingRequests;

    /** A map of the view corresponding to each player */
    private Map<Player, RemoteView> views;

    /** The helper class to manage the match */
    private MatchController matchController;

    /** The helper class to compute and assign scores */
    private ScoreManager scoreManager;

    /** The game timer associated to the match */
    private GameTimer gameTimer;

    public ControllerImpl(Model model) {
        this.model = model;
        this.gameTimer = new GameTimer(this, MAX_TURN_TICKS);
        this.views = new HashMap<>(); // Create a new empty map for the views
        this.scoreManager = new StandardScoreManager();
        this.matchController = new MatchController(this);
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public GameTimer getGameTimer() {
        return gameTimer;
    }

    @Override
    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    @Override
    public void sendRequest(Player player, RequestContext req) {
        /* TODO: Reimplement this with RequestManager */
//        if(player == null || req == null){
//            throw new NullPointerException("Found null parameters");
//        }
//        pendingRequests.put(player, req);
//        RemoteView playerView = views.get(player);
//        if(playerView != null){
//            playerView.performRequest(req.getRequest());
//        }
    }

    @Override
    public void cancelRequest(Player player) {
        if(player == null){
            throw new NullPointerException("Player cannot be null");
        }
        // Get pending request context and cancel the handler
        pendingRequests.get(player).getHandler().cancel();
        // Remove pending request for the player
        pendingRequests.remove(player);
    }

    @Override
    public RequestContext getRequestContext(Player player) {
            if(player == null) {
                throw new NullPointerException("Player cannot be null");
            }
            return pendingRequests.get(player);
    }

    /**
     * Callback method invoked when the game timer expires
     */
    @Override
    public void callback() {
        // Here the time is expired.
        // This happens only when the player has not finished its turn in time
        // Since we stop it at the end of each turn

        // We should first suspend the player
        matchController.suspendCurrentPlayer();
        //TODO: Implement this method
    }

    @Override
    public void update(ViewEvent event) {
        //TODO: Implement this method
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.Observable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.Observer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.events.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.Model;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * A class representing the controller of the game.
 * It is responsible of listening to events and take actions accordingly.
 * It also manages interactions with the players when they have to choose something (e.g. targets, destinations, ...).
 */
public class Controller implements CallbackInterface, Observer<Event> {

    //TODO: Implement observable

    private static final int MAX_TIMER_TICKS = 180; // 3 minutes TODO: check this attribute

    /** Associated model instance */
    private Model model;

    /** A map of the pending request corresponding to each player */
    private Map<Player, RequestContext> pendingRequests;

    /** A map of the view corresponding to each player */
    private Map<Player, View> views;

    /** The helper class to manage the match */
    private MatchController matchController;

    /** The helper class to compute and assign scores */
    private ScoreManager scoreManager;

    /** The game timer associated to the match */
    private GameTimer gameTimer;

    public Controller(Model model) {
        this.model = model;
        this.gameTimer = new GameTimer(this, MAX_TIMER_TICKS);
        this.views = new HashMap<>(); // Create a new empty map for the views
        this.scoreManager = new StandardScoreManager();
        this.matchController = new MatchController(this);
    }

    /**
     * Retrieves the model associated to the controller
     * @return The object representing the model
     */
    public Model getModel(){
        return model;
    }


    /**
     * Retrieves the timer of the current match
     * @return The object representing the game timer
     */
    public GameTimer getGameTimer(){
        return gameTimer;
    }

    /**
     * Retrieves the score manager for the current match
     * @return The object representing the score manager
     */
    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    /**
     * Creates a request for the specified player and send it to him
     * @param player The object representing the player, not null
     * @param req The object representing the request, not null
     */
    public void sendRequest(Player player, RequestContext req){
        if(player == null || req == null){
            throw new NullPointerException("Found null parameters");
        }
        pendingRequests.put(player, req);
        //TODO: Finish implementing this method
    }

    /**
     * Cancels the pending request for a player
     * @param player The object representing the player, not null
     */
    public void cancelRequest(Player player){
        if(player == null){
            throw new NullPointerException("Player cannot be null");
        }
        // Get pending request context and cancel the handler
        pendingRequests.get(player).getHandler().cancel();
        // Remove pending request for the player
        pendingRequests.remove(player);
    }

    /**
     * Retrieves the pending request for a player
     * @param player The object representing the player, not null
     * @return The object representing the request
     */
    public RequestContext getRequestContext(Player player){
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
    public void update(Observable<Event> observable, Event event) {
        //TODO: Implement this method
    }
}

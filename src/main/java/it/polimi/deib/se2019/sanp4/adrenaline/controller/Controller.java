package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.View;

import java.util.Map;

/**
 * A class representing the controller of the game.
 * It is responsible of listening to events and take actions accordingly.
 * It also manages interactions with the players when they have to choose something (e.g. targets, destinations, ...).
 */
public class Controller implements CallbackInterface{

    /** The match it is controlling */
    private Match match;

    /** A map of the pending request corresponding to each player */
    private Map<Player, RequestContext> pendingRequests;

    /** A map of the view corresponding to each player */
    private Map<Player, View> views;

    /** The helper class to manage the match */
    private MatchController matchController;

    /** The game timer associated to the match */
    private GameTimer gameTimer;

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
        pendingRequests.put(player, null);
        //TODO: Finish implementing this method
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
        //TODO: Implement this method
    }
}

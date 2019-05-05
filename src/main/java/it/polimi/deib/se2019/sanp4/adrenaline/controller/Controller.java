package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.model.Model;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

/**
 * A class representing the controller of the game.
 * It is responsible of listening to events and take actions accordingly.
 * It also manages interactions with the players when they have to choose something (e.g. targets, destinations, ...).
 */
public interface Controller extends CallbackInterface, RemoteObserver<ViewEvent> {
    /**
     * Retrieves the model associated to the controller
     * @return The object representing the model
     */
    Model getModel();


    /**
     * Retrieves the timer of the current match
     * @return The object representing the game timer
     */
    GameTimer getGameTimer();

    /**
     * Retrieves the score manager for the current match
     * @return The object representing the score manager
     */
    ScoreManager getScoreManager();

    /**
     * Creates a request for the specified player and send it to him
     * @param player The object representing the player, not null
     * @param req The object representing the request, not null
     */
    public void sendRequest(Player player, RequestContext req);

    /**
     * Cancels the pending request for a player
     * @param player The object representing the player, not null
     */
    public void cancelRequest(Player player);

    /**
     * Retrieves the pending request for a player
     * @param player The object representing the player, not null
     * @return The object representing the request
     */
    RequestContext getRequestContext(Player player);

}

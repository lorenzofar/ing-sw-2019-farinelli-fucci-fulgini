package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.model.Model;

/**
 * A class representing the controller of the game.
 * It is responsible of listening to events and take actions accordingly.
 * It also manages interactions with the players when they have to choose something (e.g. targets, destinations, ...).
 */
public interface Controller extends RemoteObserver<ViewEvent> {
    /**
     * Retrieves the model associated to the controller
     * @return The object representing the model
     */
    Model getModel();
}

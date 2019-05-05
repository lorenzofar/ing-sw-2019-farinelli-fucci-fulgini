package it.polimi.deib.se2019.sanp4.adrenaline.view;

import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.Request;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.events.ViewEvent;

import java.rmi.Remote;

/** An interface describing the structure of a view */
public interface RemoteView extends RemoteObserver<ModelUpdate>, Remote {
    /**
     * Performs the provided request on the view
     * @param request The object representing the request, not null
     */
    void performRequest(Request request);

    /**
     * Displays the provided message
     * @param text The text of the message, not null
     * @param type The type of the message, not null
     */
    void showMessage(String text, MessageType type);

    /**
     * Adds an observer to listen for updates
     * @param observer The object representing the observer, not null
     */
    void addObserver(RemoteObserver<ViewEvent> observer);

    /**
     * Removes an observer from listening for updates
     * If it hasn't previously subscribed, does nothing
     * @param observer The object representing the observer, not null
     */
    void removeObserver(RemoteObserver<ViewEvent> observer);
}

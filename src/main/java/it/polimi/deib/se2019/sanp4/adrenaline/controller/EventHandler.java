package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;

/**
 * Describes an object that can handle an event and update the match accordingly
 */
public interface EventHandler {

    /**
     * Handle the event provided event, with side effects on the provided match
     * @param event The object representing the event, not null
     * @param controller The objects representing the controller, not null
     */
    static void handle(ViewEvent event, Controller controller){
        if(event == null || controller == null) {
            throw new NullPointerException("Found null parameters");
        }
        //TODO: Implement this method
    }
}

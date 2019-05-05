package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;

/**
 * Describes an object that can handle an event and update the match accordingly
 */
public interface EventHandler {

    /**
     * Handle the event provided event, with side effects on the provided match
     * @param event The object representing the event, not null
     * @param match The objects representing the match, not null
     */
    static void handle(ViewEvent event, Match match){
        if(event == null || match == null) {
            throw new NullPointerException("Found null parameters");
        }
        //TODO: Implement this method
    }
}

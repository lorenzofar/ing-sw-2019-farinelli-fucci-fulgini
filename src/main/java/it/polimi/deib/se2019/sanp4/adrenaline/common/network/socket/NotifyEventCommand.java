package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;

/**
 * Sent when the view has to notify its remote observers (the controller)
 * with a {@link ViewEvent}
 */
public class NotifyEventCommand implements SocketServerCommand {

    private ViewEvent event;

    /**
     * Create new command with given event
     * @param event event which sould be notified
     */
    @JsonCreator
    public NotifyEventCommand(@JsonProperty("event") ViewEvent event) {
        if (event == null) throw new NullPointerException("Event cannot be null");
        this.event = event;
    }

    public ViewEvent getEvent() {
        return event;
    }

    /**
     * Applies the command to given target, namely it is the {@code SocketRemoteView}
     *
     * @param target target of the command
     */
    @Override
    public void applyOn(SocketServerCommandTarget target) {
        /* Notify the observers */
        target.notifyEvent(event);
    }
}

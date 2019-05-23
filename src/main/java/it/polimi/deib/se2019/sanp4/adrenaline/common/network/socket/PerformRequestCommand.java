package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;

/**
 * Sent when the controller wants to perform a request on the client
 */
public class PerformRequestCommand implements SocketClientCommand {

    private ChoiceRequest request;

    /**
     * Creates a new command
     * @param request the request which has to be sent, not null
     */
    @JsonCreator
    public PerformRequestCommand(@JsonProperty("request") ChoiceRequest request) {
        if (request == null) throw new NullPointerException("Request cannot be null");
        this.request = request;
    }

    /**
     * Returns the request
     * @return the request
     */
    public ChoiceRequest getRequest() {
        return request;
    }

    /**
     * Applies the command to given target, namely it is a {@code SocketServerConnection}
     *
     * @param target target of the command
     */
    @Override
    public void applyOn(SocketClientCommandTarget target) {
        ClientView view = target.getClientView();
        /* Call the method on the view */
        view.performRequest(request);
    }
}

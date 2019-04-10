package it.polimi.deib.se2019.sanp4.adrenaline.controller;

/**
 * A class describing the context of a request made to a player
 * @param <T> The type of object requested to the player
 */
public class RequestContext<T> {

    private ChoiceHandler<T> handler;
    private Request<T> request;

    /**
     * Creates a new request for the provided request,
     * that will be handled by the specified handler
     * @param req The object representing the request, not null
     * @param handler The object representing the handler, not null
     */
    RequestContext(Request<T> req,  ChoiceHandler<T> handler){
        if(req == null || handler == null){
            throw new NullPointerException("Found null parameters");
        }
        this.handler = handler;
        this.request = req;
    }

    /**
     * Retrieves the handler for the request
     * @return The object representing the handler
     */
    public ChoiceHandler<T> getHandler() {
        return handler;
    }

    /**
     * Retrieves the associated request
     * @return The object associated with the request
     */
    public Request<T> getRequest() {
        return request;
    }
}

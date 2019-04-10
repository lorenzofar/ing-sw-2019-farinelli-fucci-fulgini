package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class representing a request made to a player
 * @param <T> The type of objects representing the choices the player can make
 */
public abstract class Request<T>{

    /** A human readable message shown to the recipient of the request */
    private String message;

    /** A list of objects representing the available choices */
    private List<T> choices;

    /** Indicates whether the request is optional */
    private boolean optional;

    /**
     * Checks whether the provided choice belong to the set of allowed choices
     * @param choice The object representing the choice, not null
     * @return {@code true} if the choice belongs, {@code false} otherwise
     */
    public abstract boolean isValid(T choice);

    /**
     * Retrieves the message associated to the request
     * @return The message associated to the request
     */
    public String getMessage() {
        return message;
    }

    /**
     * Retrieves the available choices for the request
     * @return The list of objects representing the choices
     */
    public List<T> getChoices() {
        return new ArrayList<>(choices);
    }

    /**
     * Determines whether a request is optional
     * @return {@code true} if it is optional, {@code false} otherwise
     */
    public boolean isOptional() {
        return optional;
    }
}

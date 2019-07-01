package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

/**
 * Thrown when performing an operation that involves on object which has to be retrieved from a given identifier,
 * but the object with that identifier could not be obtained.
 */
public class UnknownIdException extends Exception {

    private static final long serialVersionUID = 9065206789660670809L;

    /**
     * Creates an exception with no message
     */
    public UnknownIdException() {
        super();
    }

    /**
     * Creates an exception with a specific message
     *
     * @param message The message
     */
    public UnknownIdException(String message) {
        super(message);
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

/**
 * Thrown when a requested card cannot be created/returned, either because it has not been loaded or because it
 * does not belong to a specific collection.
 *
 * @author Alessandro Fulgini
 */
public class CardNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1611554844941738693L;

    /**
     * Creates an exception with no message
     */
    public CardNotFoundException() {
        super();
    }

    /**
     * Creates an exception with a specific message
     *
     * @param message The message
     */
    public CardNotFoundException(String message) {
        super(message);
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

/**
 * Thrown when a requested board (e.g. by id) is not available for retrieval
 *
 * @author Alessandro Fulgini
 */
public class BoardNotFoundException extends Exception {
    private static final long serialVersionUID = -357038850636423785L;

    /**
     * Creates an exception with no message
     */
    public BoardNotFoundException() {
        super();
    }

    /**
     * Creates an exception with a specific message
     *
     * @param message The message
     */
    public BoardNotFoundException(String message) {
        super(message);
    }
}

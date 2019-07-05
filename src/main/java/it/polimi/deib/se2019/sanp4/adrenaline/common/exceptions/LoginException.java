package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

/**
 * Thrown when a login request fails
 *
 * @author Alessandro Fulgini
 */
public class LoginException extends Exception {

    private static final long serialVersionUID = -5385824215309040071L;

    /**
     * Creates an exception with no message
     */
    public LoginException() {
    }

    /**
     * Creates an exception with specific message
     *
     * @param message The message
     */
    public LoginException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a message and carrying the exception that caused it
     *
     * @param message The message
     * @param cause   The exception which prevented login to succeed
     */
    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }
}

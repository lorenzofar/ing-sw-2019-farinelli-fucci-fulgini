package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

/**
 * Thrown when a login request fails
 */
public class LoginException extends Exception {

    private static final long serialVersionUID = -5385824215309040071L;

    public LoginException() {
    }

    public LoginException(String message) {
        super(message);
    }

    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }
}

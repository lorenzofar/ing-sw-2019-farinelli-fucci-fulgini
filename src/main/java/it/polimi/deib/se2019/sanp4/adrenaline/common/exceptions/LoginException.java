package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

/**
 * Thrown when a login request fails
 */
public class LoginException extends Exception {
    public LoginException() {
    }

    public LoginException(String message) {
        super(message);
    }

    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }
}

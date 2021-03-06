package it.polimi.deib.se2019.sanp4.adrenaline.controller.requests;

/**
 * Thrown when an invalid choice is provided in response to a request
 * @author Alessandro Fulgini
 */
public class InvalidChoiceException extends Exception {

    private static final long serialVersionUID = -8167961371178186011L;

    /**
     * Constructs the exception with a default message
     * @param choice the attempted choice which generated the exception
     */
    public InvalidChoiceException(Object choice) {
        super(String.format("The choice %s is invalid for this request", choice));
    }
}

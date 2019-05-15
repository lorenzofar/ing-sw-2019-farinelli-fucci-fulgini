package it.polimi.deib.se2019.sanp4.adrenaline.controller.requests;

/**
 * Thrown when you try to complete a choice, but it has already been completed
 */
public class AlreadyCompletedException extends Exception {

    /**
     * Creates the exception with a default message
     */
    public AlreadyCompletedException() {
        super("Cannot complete this choice because it has already been completed");
    }
}

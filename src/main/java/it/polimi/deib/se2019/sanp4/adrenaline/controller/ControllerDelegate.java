package it.polimi.deib.se2019.sanp4.adrenaline.controller;

/**
 * An interface describing a class that can bind the controller and act on it
 */
public interface ControllerDelegate {

    /**
     * Binds the controller
     * @param controller The object representing the controller, not null
     */
    void bind(Controller controller);

    /**
     * Starts executing the action on the controller
     */
    void start();
}

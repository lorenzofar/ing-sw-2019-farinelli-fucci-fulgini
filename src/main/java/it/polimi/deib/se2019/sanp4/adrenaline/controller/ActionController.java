package it.polimi.deib.se2019.sanp4.adrenaline.controller;

/**
 * An abstract class describing an object that can handle players' actions
 * It implements the ControllerDelegate interface to act on the controller and update the game
 */
public abstract class ActionController implements ControllerDelegate {

    private ActionController nextAction;

}

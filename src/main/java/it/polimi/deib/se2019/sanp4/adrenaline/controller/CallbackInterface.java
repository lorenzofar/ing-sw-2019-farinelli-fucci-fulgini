package it.polimi.deib.se2019.sanp4.adrenaline.controller;

/**
 * An interface describing a class that exposes a callback method,
 * that can be invoked after the completion of another task.
 */
public interface CallbackInterface {

    /** The method that will be called as a callback*/
    void callback();
}

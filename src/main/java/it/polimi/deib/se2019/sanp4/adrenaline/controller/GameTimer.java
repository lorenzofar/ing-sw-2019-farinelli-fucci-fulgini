package it.polimi.deib.se2019.sanp4.adrenaline.controller;

/**
 * A class describing an object that manages the game timing
 * It provides the basic functionalities of a stopwatch and
 * holds a reference to a method to invoke when time expires
 */
public class GameTimer {

    /** The maximum number of ticks before expiration */
    private int maxTicks;

    /** The object representing the callback */
    private CallbackInterface expiredCallback;

    /**
     * Creates a new game timer with the provided callback
     * @param expiredCallback The object representing the callback, not null
     */
    GameTimer(CallbackInterface expiredCallback){
        if(expiredCallback == null){
            throw new NullPointerException("Callback cannot be null");
        }
        this.expiredCallback = expiredCallback;
    }

    /**
     * Starts the timer
     */
    public void start(){
        //TODO: Implement this method
    }

    /**
     * Stops the timer
     */
    public void stop(){
        //TODO: Implement this method
    }

    /**
     * Resets the timer
     */
    public void reset(){
        //TODO: Implement this method
    }
}

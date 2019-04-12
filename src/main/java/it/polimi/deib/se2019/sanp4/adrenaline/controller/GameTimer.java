package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A class describing an object that manages the game timing
 * It provides the basic functionalities of a stopwatch and
 * holds a reference to a method to invoke when time expires
 */
public class GameTimer {

    /** The maximum number of ticks before expiration */
    private final int maxTicks;
    private int ticks;
    private Timer timer;

    /** The object representing the callback */
    private CallbackInterface expiredCallback;

    /**
     * Creates a new game timer with the provided callback
     * @param expiredCallback The object representing the callback, not null
     * @param maxTicks The maximum number of seconds to count, must be positive
     */
    GameTimer(CallbackInterface expiredCallback, int maxTicks){
        if(expiredCallback == null){
            throw new NullPointerException("Callback cannot be null");
        }
        if(maxTicks < 0){
            throw new IllegalArgumentException("Game time cannot be negative");
        }
        this.expiredCallback = expiredCallback;
        this.maxTicks = maxTicks;
        timer = new Timer();
        ticks = 0;
    }

    /**
     * Updates the elapsed time count at each tick
     * It is also responsible of managing expiration and invoking the callback
     */
    private void tick(){
        ticks++;
        if(ticks >= maxTicks){
            stop();
            expiredCallback.callback();
        }
    }

    /**
     * Creates a new timer
     * First resets the timer and then schedules tick execution
     */
    public void start(){
        reset();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        }, 1000, 1000);
    }

    /**
     * Stops the timer
     */
    public void stop(){
        try{
            timer.cancel();
        }
        catch(IllegalStateException ex){}
    }

    /**
     * Stops the timer and resets ticks count
     */
    public void reset(){
        stop();
        ticks = 0;
    }

    /**
     * Retrieves time elapsed since timer start
     * @return Elapsed time (in seconds)
     */
    public int getElapsedTime(){
        return ticks;
    }
}

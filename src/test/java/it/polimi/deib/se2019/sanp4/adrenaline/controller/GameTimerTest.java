package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameTimerTest {

    private static class TestCallback implements CallbackInterface{
        @Override
        public void callback() {
            synchronized (lock) {
                called = true;
                lock.notifyAll();
            }
        }
    }

    private static TestCallback callbackInterface;
    private static int properMaxTicks;
    private static GameTimer timer;

    private static Object lock;
    private static boolean called = false;

    @BeforeClass
    public static void setup(){
        callbackInterface = new TestCallback();
        properMaxTicks = 5;
        lock = new Object();
    }

    @Test(expected = NullPointerException.class)
    public void createTimer_nullCallbackProvided_shouldThrowNullPointerException(){
        timer = new GameTimer(null, properMaxTicks);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTimer_negativeTicksProvided_shouldThrowIllegalArgumentException(){
        timer = new GameTimer(callbackInterface, -1);
    }

    @Test
    public void createTimer_properParametersProvided_shouldNotThrowException(){
        timer = new GameTimer(callbackInterface, properMaxTicks);
    }

    @Test
    public void createTimer_notStartedCheckTime_shouldReturnZero(){
        timer = new GameTimer(callbackInterface, properMaxTicks);
        assertEquals(0, timer.getElapsedTime());
    }

    @Test
    public void resetTimer_checkTime_shouldReturnZero(){
        timer = new GameTimer(callbackInterface, properMaxTicks);
        timer.reset();
        assertEquals(0, timer.getElapsedTime());
    }

    @Test
    public void startTimer_properParametersProvided_shouldNotThrowException(){
        timer = new GameTimer(callbackInterface, properMaxTicks);
        timer.start();
    }

    @Test
    public void startTimer_properTimeElapsed_shouldReturnMaxTicks() {
        timer = new GameTimer(callbackInterface, properMaxTicks);
        timer.start();
        synchronized (lock) {
            while (!called) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        assertEquals(properMaxTicks, timer.getElapsedTime());
    }

    @Test
    public void stopTimer_timerNotStarted_shouldNotThrowException(){
        timer = new GameTimer(callbackInterface, properMaxTicks);
        timer.stop();
    }
}

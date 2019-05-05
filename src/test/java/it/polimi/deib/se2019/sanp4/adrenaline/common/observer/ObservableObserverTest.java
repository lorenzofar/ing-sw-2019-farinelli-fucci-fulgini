package it.polimi.deib.se2019.sanp4.adrenaline.common.observer;

import org.junit.Test;

import static org.junit.Assert.*;

public class ObservableObserverTest {

    private String message = "Message";

    public class ObservableImpl extends Observable<String>{
        ObservableImpl(){
            super();
        }
    }

    public class ObserverImpl implements Observer<String> {

        boolean shouldReceiveUpdate;

        @Override
        public void update(String event) {
            if (shouldReceiveUpdate) {
                assertEquals(message, event);
            } else {
                fail();
            }
        }
    }

    @Test
    public void addObserver_Notify_ShouldSucceed(){
        ObservableImpl observableImpl = new ObservableImpl();
        ObserverImpl observer = new ObserverImpl();
        observableImpl.addObserver(observer);
        observer.shouldReceiveUpdate = true;
        observableImpl.notifyObservers(message);
    }

    @Test
    public void removeObserver_ShouldSucceed(){
        ObservableImpl observableImpl = new ObservableImpl();
        ObserverImpl observer = new ObserverImpl();
        observableImpl.addObserver(observer);
        observer.shouldReceiveUpdate = true;
        observableImpl.notifyObservers(message);
        observableImpl.removeObserver(observer);
        observer.shouldReceiveUpdate = false;
        observableImpl.notifyObservers(message);
    }

    @Test
    public void removeNotAddedObserver_ShouldNotThrowException(){
        ObservableImpl observableImpl = new ObservableImpl();
        ObserverImpl observer = new ObserverImpl();
        observableImpl.removeObserver(observer);
    }

}
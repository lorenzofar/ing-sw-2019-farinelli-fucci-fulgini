package it.polimi.deib.se2019.sanp4.adrenaline.common.observer;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

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

    public class RoutingObservableImpl extends RoutingObservable<String>{
        RoutingObservableImpl(){
            super();
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

    @Test
    public void routingAddObserver_Notify_ShouldSucceed(){
        ObserverImpl observer = new ObserverImpl();
        RoutingObservableImpl routingObservable = new RoutingObservableImpl();
        routingObservable.addObserver("Giammarco", observer);
        observer.shouldReceiveUpdate = true;
        routingObservable.notifyObservers(message);
    }

    @Test
    public void routingRemoveObserver_ShouldSucceed(){
        ObserverImpl observer = new ObserverImpl();
        RoutingObservableImpl routingObservable = new RoutingObservableImpl();
        routingObservable.addObserver("Giammarco", observer);
        routingObservable.removeObserver("Giammarco", observer);
        routingObservable.removeObserver("Giammarco", observer);
        observer.shouldReceiveUpdate = false;
        routingObservable.notifyObservers(message);
    }

    @Test
    public void routingRemoveAllObservers_ShouldSucceed(){
        ObserverImpl observer = new ObserverImpl();
        ObserverImpl observer2 = new ObserverImpl();
        RoutingObservableImpl routingObservable = new RoutingObservableImpl();
        routingObservable.addObserver("Giammarco", observer);
        routingObservable.addObserver("Giammarco", observer2);
        routingObservable.removeAllObservers("Giammarco");
        observer.shouldReceiveUpdate = false;
        observer2.shouldReceiveUpdate = false;
        routingObservable.notifyObservers(message);
    }

    @Test
    public void routingAddObserver_NotifyUser_ShouldSucceed(){
        ObserverImpl observer = new ObserverImpl();
        RoutingObservableImpl routingObservable = new RoutingObservableImpl();
        routingObservable.addObserver("Giammarco", observer);
        observer.shouldReceiveUpdate = true;
        routingObservable.notifyObservers("Giammarco", message);
    }

    @Test
    public void routingAddObserver_NotifyUser_NullUserProvided_ShouldSucceed(){
        ObserverImpl observer = new ObserverImpl();
        RoutingObservableImpl routingObservable = new RoutingObservableImpl();
        routingObservable.addObserver("Giammarco", observer);
        observer.shouldReceiveUpdate = true;
        String user = null;
        routingObservable.notifyObservers(user, message);
    }

    @Test
    public void routingNotifyObservers_ShouldSucceed(){
        ObserverImpl observerG = new ObserverImpl();
        ObserverImpl observerS = new ObserverImpl();
        RoutingObservableImpl routingObservable = new RoutingObservableImpl();
        routingObservable.addObserver("Giammarco", observerG);
        routingObservable.addObserver("Stefano", observerS);
        observerG.shouldReceiveUpdate = true;
        observerS.shouldReceiveUpdate = true;
        routingObservable.notifyObservers(Arrays.asList("Stefano", "Giammarco", "German"), message);

    }





}
package it.polimi.deib.se2019.sanp4.adrenaline.common.observer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ObservableObserverTest {

    private String message = "Message";

    @Mock
    private static Observer<String> observer;

    @Mock
    private static Observer<String> anotherObserver;

    private static ObservableImpl observable;

    private static RoutingObservableImpl routingObservable;

    @Before
    public void setUp() {
        observable = new ObservableImpl();
        routingObservable = new RoutingObservableImpl();
    }

    public class ObservableImpl extends Observable<String>{
        ObservableImpl(){
            super();
        }
    }

    public class RoutingObservableImpl extends RoutingObservable<String>{
        RoutingObservableImpl(){
            super();
        }
    }

    @Test
    public void addObserver_Notify_ShouldSucceed(){
        observable.addObserver(observer);
        observable.notifyObservers(message);

        /* Check that the observer received the message */
        verify(observer).update(message);
    }

    @Test
    public void removeObserver_thenNotify_shouldNotBeNotified(){
        observable.addObserver(observer);
        observable.removeObserver(observer);

        observable.notifyObservers(message);

        verify(observer, never()).update(anyString());
    }

    @Test
    public void removeNotAddedObserver_ShouldNotThrowException(){
        try {
            observable.removeObserver(observer);
        } catch (RuntimeException e) {
            fail();
        }
    }

    @Test
    public void routingAddObserver_Notify_ShouldSucceed(){
        routingObservable.addObserver("Giammarco", observer);
        routingObservable.notifyObservers(message);

        verify(observer).update(message);
    }

    @Test
    public void routingRemoveObserver_ShouldSucceed(){
        routingObservable.addObserver("Giammarco", observer);
        routingObservable.removeObserver("Giammarco", observer);
        routingObservable.removeObserver("Giammarco", observer);
        routingObservable.notifyObservers(message);

        verify(observer, never()).update(anyString());
    }

    @Test
    public void routingRemoveAllObservers_ShouldSucceed(){
        routingObservable.addObserver("Giammarco", observer);
        routingObservable.addObserver("Giammarco", anotherObserver);
        routingObservable.removeAllObservers("Giammarco");
        routingObservable.notifyObservers(message);

        verify(observer, never()).update(anyString());
        verify(anotherObserver, never()).update(anyString());
    }

    @Test
    public void routingAddObserver_NotifyUser_NullUserProvided_ShouldNotNotify(){
        routingObservable.addObserver("Giammarco", observer);
        routingObservable.notifyObservers((String) null, message);

        verify(observer, never()).update(message);
    }

    @Test
    public void routingNotifyObservers_ShouldSucceed(){
        RoutingObservableImpl routingObservable = new RoutingObservableImpl();
        routingObservable.addObserver("Giammarco", observer);
        routingObservable.addObserver("Stefano", anotherObserver);
        routingObservable.notifyObservers(Arrays.asList("Stefano", "Giammarco", "German"), message);

        verify(observer).update(message);
        verify(anotherObserver).update(message);
    }





}
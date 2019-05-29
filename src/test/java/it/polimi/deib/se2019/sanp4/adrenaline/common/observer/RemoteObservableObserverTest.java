package it.polimi.deib.se2019.sanp4.adrenaline.common.observer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteObservableObserverTest {

    private String message = "Message";

    @Mock
    private static RemoteObserver<String> remoteObserver;

    @Mock
    private static RemoteObserver<String> anotherRemoteObserver;

    private static RemoteObservableImpl remoteObservable;

    private static RemoteRoutingObservableImpl remoteRoutingObservable;

    public class RemoteObservableImpl extends RemoteObservable<String>{
        RemoteObservableImpl(){
            super();
        }
    }

    @Before
    public void setUp() {
        remoteObservable = new RemoteObservableImpl();
        remoteRoutingObservable = new RemoteRoutingObservableImpl();
    }

    public class RemoteRoutingObservableImpl extends RemoteRoutingObservable<String>{
        RemoteRoutingObservableImpl(){
            super();
        }
    }

    @Test
    public void addRemoteObserver_Notify_ShouldSucceed() throws IOException {
        remoteObservable.addObserver(remoteObserver);
        remoteObservable.notifyObservers(message);

        /* Check that the observer received the message */
        verify(remoteObserver).update(message);
    }

    @Test
    public void removeRemoteObserver_ShouldSucceed() throws IOException {
        remoteObservable.addObserver(remoteObserver);
        remoteObservable.removeObserver(remoteObserver);
        remoteObservable.notifyObservers(message);

        verify(remoteObserver, never()).update(message);
    }

    @Test
    public void notify_observerThrows_shouldIgnore() throws IOException {
        /* Set up the observer to always throw */
        doThrow(new IOException()).when(remoteObserver).update(anyString());

        remoteObservable.addObserver(remoteObserver);
        remoteObservable.notifyObservers(message);

        verify(remoteObserver).update(anyString());
    }

    @Test
    public void remoteRoutingAddObserver_Notify_ShouldSucceed() throws IOException {
        remoteRoutingObservable.addObserver("Giammarco", remoteObserver);
        remoteRoutingObservable.notifyObservers(message);

        verify(remoteObserver).update(message);
    }

    @Test
    public void remoteRoutingRemoveObserver_ShouldNotReceiveUpdate() throws IOException {
        remoteRoutingObservable.addObserver("Giammarco", remoteObserver);
        remoteRoutingObservable.removeObserver("Giammarco", remoteObserver);
        remoteRoutingObservable.removeObserver("Giammarco", remoteObserver);
        remoteRoutingObservable.notifyObservers(message);

        verify(remoteObserver, never()).update(message);
    }

    @Test
    public void remoteRoutingRemoveAllObservers_ShouldNotReceiveUpdate() throws IOException {
        RemoteRoutingObservableImpl remoteRoutingObservable = new RemoteRoutingObservableImpl();
        remoteRoutingObservable.addObserver("Giammarco", remoteObserver);
        remoteRoutingObservable.addObserver("Giammarco", anotherRemoteObserver);
        remoteRoutingObservable.removeAllObservers("Giammarco");
        remoteRoutingObservable.notifyObservers(message);

        verify(remoteObserver, never()).update(message);
        verify(anotherRemoteObserver, never()).update(message);
    }

    @Test
    public void remoteRoutingAddObserver_NotifyUser_ShouldSucceed() throws IOException {
        remoteRoutingObservable.addObserver("Giammarco", remoteObserver);
        remoteRoutingObservable.notifyObservers("Giammarco", message);

        verify(remoteObserver).update(message);
    }

    @Test
    public void routingNotifyObservers_MultipleObservers_ShouldSucceed() throws IOException {
        RemoteRoutingObservableImpl remoteRoutingObservable = new RemoteRoutingObservableImpl();
        remoteRoutingObservable.addObserver("Giammarco", remoteObserver);
        remoteRoutingObservable.addObserver("Stefano", anotherRemoteObserver);
        remoteRoutingObservable.notifyObservers(Arrays.asList("Stefano", "Giammarco", "German"), message);

        verify(remoteObserver).update(message);
        verify(anotherRemoteObserver).update(message);
    }

    @Test
    public void routingNotify_observerThrows_shouldIgnore() throws IOException {
        /* Set up the observer to always throw */
        doThrow(new IOException()).when(remoteObserver).update(anyString());

        remoteRoutingObservable.addObserver("Giammarco", remoteObserver);
        remoteRoutingObservable.notifyObservers(message);

        verify(remoteObserver).update(anyString());
    }
}
package it.polimi.deib.se2019.sanp4.adrenaline.common.observer;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class RemoteObservableObserverTest {

    private String message = "Message";

    public class RemoteObservableImpl extends RemoteObservable<String>{
        RemoteObservableImpl(){
            super();
        }
    }

    public class RemoteObserverImpl implements RemoteObserver<String> {

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

    public class RemoteRoutingObservableImpl extends RemoteRoutingObservable<String>{
        RemoteRoutingObservableImpl(){
            super();
        }
    }



    @Test
    public void addRemoteObserver_Notify_ShouldSucceed(){
        RemoteObservableImpl remoteObservable = new RemoteObservableImpl();
        RemoteObserverImpl remoteObserver = new RemoteObserverImpl();
        remoteObservable.addObserver(remoteObserver);
        remoteObserver.shouldReceiveUpdate = true;
        remoteObservable.notifyObservers(message);
    }

    @Test
    public void removeRemoteObserver_ShouldSucceed(){
        RemoteObservableImpl remoteObservable = new RemoteObservableImpl();
        RemoteObserverImpl remoteObserver = new RemoteObserverImpl();
        remoteObservable.addObserver(remoteObserver);
        remoteObserver.shouldReceiveUpdate = true;
        remoteObservable.notifyObservers(message);
        remoteObservable.removeObserver(remoteObserver);
        remoteObserver.shouldReceiveUpdate = false;
        remoteObservable.notifyObservers(message);
    }

    @Test
    public void remoteRoutingAddObserver_Notify_ShouldSucceed(){
        RemoteObserverImpl remoteObserver = new RemoteObserverImpl();
        RemoteRoutingObservableImpl remoteRoutingObservable = new RemoteRoutingObservableImpl();
        remoteRoutingObservable.addObserver("Giammarco", remoteObserver);
        remoteObserver.shouldReceiveUpdate = true;
        remoteRoutingObservable.notifyObservers(message);
    }

    @Test
    public void remoteRoutingRemoveObserver_ShouldSucceed(){
        RemoteObserverImpl remoteObserver = new RemoteObserverImpl();
        RemoteRoutingObservableImpl remoteRoutingObservable = new RemoteRoutingObservableImpl();
        remoteRoutingObservable.addObserver("Giammarco", remoteObserver);
        remoteRoutingObservable.removeObserver("Giammarco", remoteObserver);
        remoteRoutingObservable.removeObserver("Giammarco", remoteObserver);
        remoteObserver.shouldReceiveUpdate = false;
        remoteRoutingObservable.notifyObservers(message);
    }

    @Test
    public void remoteRoutingRemoveAllObservers_ShouldSucceed(){
        RemoteObserverImpl remoteObserver = new RemoteObserverImpl();
        RemoteObserverImpl remoteObserver2 = new RemoteObserverImpl();
        RemoteRoutingObservableImpl remoteRoutingObservable = new RemoteRoutingObservableImpl();
        remoteRoutingObservable.addObserver("Giammarco", remoteObserver);
        remoteRoutingObservable.addObserver("Giammarco", remoteObserver2);
        remoteRoutingObservable.removeAllObservers("Giammarco");
        remoteObserver.shouldReceiveUpdate = false;
        remoteObserver2.shouldReceiveUpdate = false;
        remoteRoutingObservable.notifyObservers(message);
    }

    @Test
    public void remoteRoutingAddObserver_NotifyUser_ShouldSucceed(){
        RemoteObserverImpl remoteObserver = new RemoteObserverImpl();
        RemoteRoutingObservableImpl remoteRoutingObservable = new RemoteRoutingObservableImpl();
        remoteRoutingObservable.addObserver("Giammarco", remoteObserver);
        remoteObserver.shouldReceiveUpdate = true;
        remoteRoutingObservable.notifyObservers("Giammarco", message);
    }

    @Test
    public void routingNotifyObservers_ShouldSucceed(){
        RemoteObserverImpl remoteObserverG = new RemoteObserverImpl();
        RemoteObserverImpl remoteObserverS = new RemoteObserverImpl();
        RemoteRoutingObservableImpl remoteRoutingObservable = new RemoteRoutingObservableImpl();
        remoteRoutingObservable.addObserver("Giammarco", remoteObserverG);
        remoteRoutingObservable.addObserver("Stefano", remoteObserverG);
        remoteObserverG.shouldReceiveUpdate = true;
        remoteObserverS.shouldReceiveUpdate = true;
        remoteRoutingObservable.notifyObservers(Arrays.asList("Stefano", "Giammarco", "German"), message);

    }

}
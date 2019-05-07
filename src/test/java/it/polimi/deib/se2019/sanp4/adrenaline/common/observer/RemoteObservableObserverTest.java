package it.polimi.deib.se2019.sanp4.adrenaline.common.observer;

import org.junit.Test;

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

    public class RemoteRoutingObservableImpl extends RoutingObservable<String>{
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



}
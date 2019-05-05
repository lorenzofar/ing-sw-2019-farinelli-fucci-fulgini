package it.polimi.deib.se2019.sanp4.adrenaline.server;

import it.polimi.deib.se2019.sanp4.adrenaline.view.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.view.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.Request;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.io.IOException;

/**
 * A class representing a remote view connected via a socket connection
 */
public class SocketRemoteView extends RemoteObservable<ViewEvent> implements RemoteView{

    SocketRemoteView(){
        //TODO: Complete constructor and method implementation
    }

    @Override
    public void performRequest(Request request) {

    }

    @Override
    public void showMessage(String text, MessageType type) {

    }

    @Override
    public void addObserver(RemoteObserver<ViewEvent> observer) {

    }

    @Override
    public void removeObserver(RemoteObserver<ViewEvent> observer) {

    }

    @Override
    public void update(ModelUpdate event) throws IOException {

    }
}

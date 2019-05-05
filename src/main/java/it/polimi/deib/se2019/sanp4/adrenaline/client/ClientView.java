package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.view.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.Request;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.io.IOException;

public class ClientView extends RemoteObservable<ViewEvent> implements RemoteView {
    private String player;

    ClientView(){
        //TODO: Complete constructor and methods implementation
    }

    @Override
    public void performRequest(Request request) {

    }

    @Override
    public void showMessage(String text, MessageType type) {

    }

    @Override
    public void removeObserver(RemoteObserver<ViewEvent> observer) {

    }

    @Override
    public void update(ModelUpdate event) throws IOException {

    }
}

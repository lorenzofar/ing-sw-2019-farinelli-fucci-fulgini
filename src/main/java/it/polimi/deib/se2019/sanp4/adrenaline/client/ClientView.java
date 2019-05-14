package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.io.IOException;

public class ClientView extends RemoteObservable<ViewEvent> implements RemoteView {
    private String player;

    ClientView(){
        //TODO: Complete constructor and methods implementation
    }

    @Override
    public void performRequest(ChoiceRequest request) {
        /* TODO: Implement this method */
    }

    @Override
    public void showMessage(String text, MessageType type) {
        /* TODO: Implement this method */
    }

    @Override
    public void removeObserver(RemoteObserver<ViewEvent> observer) {
        /* TODO: Implement this method */
    }

    @Override
    public void update(ModelUpdate event) throws IOException {
        /* TODO: Implement this method */
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

public class MockClientView implements ClientView {

    private String username;

    @Override
    public void setSocketConnection() {

    }

    @Override
    public void setRMIConnection() {

    }

    @Override
    public ServerConnection getServerConnection() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void ping() {

    }

    @Override
    public void setRenderer(UIRenderer renderer) {

    }

    @Override
    public void performRequest(ChoiceRequest request) {

    }

    @Override
    public void showMessage(String text, MessageType type) {

    }

    @Override
    public void selectScene(ViewScene scene) {

    }

    @Override
    public void addObserver(RemoteObserver<ViewEvent> observer) {

    }

    @Override
    public void removeObserver(RemoteObserver<ViewEvent> observer) {

    }

    @Override
    public void update(ModelUpdate event) {

    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observer;

public class RMIServerConnection implements ServerConnection {

    RMIServerConnection(){
        //TODO: Complete constructor and finish implementation of methods
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void connect(String hostname) {

    }

    @Override
    public void connect(String hostname, int port) {

    }

    @Override
    public void login(String username) {

    }

    @Override
    public void logout(String username) {

    }

    @Override
    public void addObserver(Observer<ModelUpdate> observer) {

    }

    @Override
    public void removeObserver(Observer<ModelUpdate> observer) {

    }
}

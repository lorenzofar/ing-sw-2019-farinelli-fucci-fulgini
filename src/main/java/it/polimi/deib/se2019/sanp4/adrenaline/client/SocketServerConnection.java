package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observer;

public class SocketServerConnection implements ServerConnection {

    public SocketServerConnection(){
        //TODO: Complete constructor and implementation of methods
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void connect(String hostname) {
        /* TODO: Implement this method */
    }

    @Override
    public void connect(String hostname, int port) {
        /* TODO: Implement this method */
    }

    @Override
    public void login(String username) {
        /* TODO: Implement this method */
    }

    @Override
    public void logout(String username) {
        /* TODO: Implement this method */
    }

    @Override
    public void addObserver(Observer<ModelUpdate> observer) {
        /* TODO: Implement this method */
    }

    @Override
    public void removeObserver(Observer<ModelUpdate> observer) {
        /* TODO: Implement this method */
    }

    @Override
    public void update(Object event) {
        /* TODO: Implement this method */
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;

import java.io.IOException;

public class MockSocketServerCommandTarget implements SocketServerCommandTarget {

    private String username;

    private SocketServer server;

    private RemoteView view;

    public MockSocketServerCommandTarget() {}

    MockSocketServerCommandTarget(String username, SocketServer server, RemoteView view) {
        this.username = username;
        this.server = server;
        this.view = view;
    }

    @Override
    public SocketServer getServer() {
        return server;
    }

    @Override
    public RemoteView getRemoteView() {
        return view;
    }

    @Override
    public void notifyEvent(ViewEvent event) {

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
    public void sendCommand(SocketClientCommand command) throws IOException {

    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.client.MockClientView;

import java.io.IOException;

public class MockSocketClientCommandTarget implements SocketClientCommandTarget {
    @Override
    public ClientView getClientView() {
        return new MockClientView();
    }

    @Override
    public void sendCommand(SocketServerCommand command) throws IOException {

    }
}

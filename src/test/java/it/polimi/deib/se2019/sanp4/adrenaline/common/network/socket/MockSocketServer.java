package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;

public class MockSocketServer implements SocketServer {

    boolean failLogin = false;

    @Override
    public void playerLogin(String username, RemoteView view) throws LoginException {
        if (failLogin) throw new LoginException();
    }

    @Override
    public void playerLogout(String username) {

    }
}

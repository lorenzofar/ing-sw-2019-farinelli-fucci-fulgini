package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.PingCommand;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.LogManager;

import static org.junit.Assert.*;

public class SocketServerConnectionOfflineTest {

    private static ClientView view;

    private static SocketServerConnection connection;

    @BeforeClass
    public static void setUp() {
        view = new MockClientView();
        connection = new SocketServerConnection(view);
        LogManager.getLogManager().reset(); /* Disable logging */
    }

    @Test
    public void createConnection_shouldNotBeActive() {
        assertFalse(connection.isActive()); /* Check that the connection is not active, since we never opened it */
    }

    @Test
    public void close_neverConnected_shouldStillBeClosed() {
        connection.close();
        assertFalse(connection.isActive());
    }

    @Test(expected = IOException.class)
    public void sendCommand_notConnected_shouldThrow() throws IOException {
        connection.sendCommand(new PingCommand());
    }

    @Test(expected = LoginException.class)
    public void login_nullName_shouldThrow() throws IOException, LoginException {
        connection.login(null);
    }

    @Test(expected = IOException.class)
    public void login_notConnected_shouldThrow() throws IOException, LoginException {
        connection.login("fuljo");
    }

    @Test(expected = IOException.class)
    public void logout_notConnected_shouldThrow() throws IOException {
        connection.logout("fuljo");
    }

    @Test(expected = IOException.class)
    public void update_notConnected_shouldThrow() throws IOException {
        connection.update(new ViewEvent() {
            @Override
            public String getSender() {
                return "fuljo";
            }
        });
    }

    @Test
    public void connect_acceptConnection_shouldComplete() throws IOException {
        ServerSocket server = new ServerSocket(3000);
        new Thread(() -> {
            try {
                server.accept();
            } catch (IOException ignore) {

            }
        }).start();

        SocketServerConnection connection = new SocketServerConnection(view);
        connection.connect("localhost");

        assertTrue(connection.isActive());
    }

    @Test
    public void getClientView_shouldReturnView() {
        assertEquals(view, connection.getClientView());
    }
}
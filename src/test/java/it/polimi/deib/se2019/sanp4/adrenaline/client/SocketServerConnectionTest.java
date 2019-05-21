package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.PingCommand;
import it.polimi.deib.se2019.sanp4.adrenaline.server.ServerImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class SocketServerConnectionTest {

    /* TODO: This test is just a mock, make it better */
    /* The server to connect to */
    private static ServerImpl server = ServerImpl.getInstance();

    /* Executor to run the server */
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    private static Future<?> serverFuture;

    @BeforeClass
    public static void setupClass() {
        /* Run the server */
        serverFuture = executor.submit(server);
    }

    @Before
    public void setUp() throws Exception {
        /* If the server has been terminated to simulate */
    }

    @Test
    public void isActive_suddenServerDisconnection_shouldCloseConnection() throws IOException {
        ClientView view = new ClientViewImpl();
        SocketServerConnection connection = new SocketServerConnection(view);

        /* Connect to the server */
        connection.connect("localhost");
        /* Check that the connection is active */
        assertTrue(connection.isActive());

        /* Terminate the server */
        serverFuture.cancel(true);

        /* Check the connection */
        assertFalse(connection.isActive());

        /* Try to send a command */
        try {
            connection.sendCommand(new PingCommand());
            fail();
        } catch (IOException e) {
            /* Should be thrown */
        }
    }
}
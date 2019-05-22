package it.polimi.deib.se2019.sanp4.adrenaline.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.LoginResponse;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.PingCommand;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket.SocketClientCommand;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.LogManager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore
/* Ignore because it uses network */
public class SocketServerConnectionOnlineTest {

    private static SocketServerConnection connection;

    private static ServerSocket server;

    private static Socket remote;

    private static ObjectMapper mapper;

    private static void acceptConnection() {
        new Thread(() -> {
            try {
                remote = server.accept();
            } catch (IOException ignore) {

            }
        }).start();
    }

    private static void sendCommandToClient(SocketClientCommand command, OutputStream out) throws IOException {
        String s = mapper.writeValueAsString(command) + "\n";
        out.write(s.getBytes());
        out.flush();
    }

    @BeforeClass
    public static void classSetup() throws IOException {
        /* Bring up a server */
        server = new ServerSocket(3000);
        mapper = JSONUtils.getNetworkObjectMapper();
        LogManager.getLogManager().reset(); /* Disable logging */
    }

    @Before
    public void setUp() {
        /* Bring up a new connection before each test */
        connection = new SocketServerConnection(new MockClientView());
    }

    @After
    public void tearDown() throws Exception {
        /* Close the sockets */
        if (remote != null) remote.close();
        connection.close();
    }

    @Test
    public void connect_serverIsUp_shouldConnect() throws IOException {
        acceptConnection();
        connection.connect("localhost");
        assertTrue(connection.isActive());
    }

    @Test
    public void sendCommand_connected_shouldSend() throws IOException {
        acceptConnection();
        connection.connect("localhost");
        connection.sendCommand(new PingCommand());
        assertTrue(connection.isActive());
    }

    @Test
    public void update_notConnected_shouldComplete() throws IOException {
        acceptConnection();
        connection.connect("localhost");
        connection.update(new ViewEvent() {
            @Override
            public String getSender() {
                return "fuljo";
            }
        });
        assertTrue(connection.isActive());
    }

    @Test
    public void close_connected_shouldClose() throws IOException {
        acceptConnection();
        connection.connect("localhost");
        connection.close();
        assertFalse(connection.isActive());
    }

    @Test
    public void login_receivesResponse_shouldLoginSuccessfully() throws IOException, LoginException {
        /* Create a remote socket which responds */
        new Thread(() -> {
            try {
                Socket remoteLogin = server.accept();
                OutputStream out = remoteLogin.getOutputStream();
                /* First write out a ping command, that is executed asynchronously */
                sendCommandToClient(new PingCommand(), out);
                /* Then send the response */
                sendCommandToClient(new LoginResponse(true), out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        connection.connect("localhost");
        connection.login("valid");
    }

    @Test(expected = LoginException.class)
    public void login_receivesResponse_shouldReceiveException() throws IOException, LoginException {
        /* Create a remote socket which responds */
        new Thread(() -> {
            try {
                Socket remoteLogin = server.accept();
                OutputStream out = remoteLogin.getOutputStream();
                sendCommandToClient(new LoginResponse(false), out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        connection.connect("localhost");
        connection.login("invalid");
    }
}
package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import it.polimi.deib.se2019.sanp4.adrenaline.client.MockClientView;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginCommandTest {

    @Test(expected = NullPointerException.class)
    public void create_nullUsername_shouldThrow() {
        new LoginCommand(null);
    }

    @Test
    public void create_validName_shouldSucceed() {
        LoginCommand command = new LoginCommand("fuljo");
        assertEquals("fuljo", command.getUsername());
    }

    @Test
    public void applyOn_loginSucceds_shouldSetUsername() {
        /* Set up mocks */
        MockSocketServer server = new MockSocketServer();
        MockSocketServerCommandTarget target =
                new MockSocketServerCommandTarget(null, server, new MockClientView());
        server.failLogin = false;

        /* Apply the command */
        LoginCommand command = new LoginCommand("fuljo");
        command.applyOn(target);
        assertEquals("fuljo", target.getUsername());
    }

    @Test
    public void applyOn_loginFails_shouldNotSetUsername() {
        /* Set up mocks */
        MockSocketServer server = new MockSocketServer();
        MockSocketServerCommandTarget target =
                new MockSocketServerCommandTarget(null, server, new MockClientView());
        server.failLogin = true;

        /* Apply the command */
        LoginCommand command = new LoginCommand("fuljo");
        command.applyOn(target);
        assertNull(target.getUsername());
    }
}
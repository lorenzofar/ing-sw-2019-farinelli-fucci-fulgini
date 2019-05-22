package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoginResponseTest {

    @Test
    public void create_shouldCreate() {
        LoginResponse res = new LoginResponse(false);
        assertFalse(res.isSuccesful());
    }

    @Test
    public void applyOn_shouldDoNothing() {
        MockSocketClientCommandTarget target = new MockSocketClientCommandTarget();

        LoginResponse res = new LoginResponse(false);
        res.applyOn(target);
    }
}
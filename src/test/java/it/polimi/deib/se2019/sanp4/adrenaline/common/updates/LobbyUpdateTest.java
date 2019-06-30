package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.JSONUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class LobbyUpdateTest {

    private ObjectMapper mapper = JSONUtils.getObjectMapper();

    @Test
    public void create_validCollection_shouldCreate() {
        LobbyUpdate u = new LobbyUpdate(Arrays.asList("1", "2", "3"), false);
        assertEquals(Arrays.asList("1", "2", "3"), u.getWaitingPlayers());
        assertFalse(u.isStarting());
    }

    @Test(expected = NullPointerException.class)
    public void create_nullCollection_shouldThrow() {
        new LobbyUpdate(null, false);
    }

    @Test
    public void serializationTest() throws IOException {
        LobbyUpdate u = new LobbyUpdate(Arrays.asList("1", "2", "3"), false);
        String ser = mapper.writeValueAsString(u);
        LobbyUpdate unser = mapper.readValue(ser, LobbyUpdate.class);
        assertTrue(unser.getWaitingPlayers().containsAll(u.getWaitingPlayers()));
        assertFalse(unser.isStarting());
    }
}
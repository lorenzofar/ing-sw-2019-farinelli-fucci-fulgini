package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class KillUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();

    private String killer = "Akiller";
    private String killed = "Akilled";
    private int deaths = 3;

    @Test
    public void setKiller_ShouldSucceed(){
        KillUpdate killUpdate = new KillUpdate(killer, killed, deaths);
        killUpdate.setKiller(killer);
        assertEquals(killer, killUpdate.getKiller());
    }

    @Test
    public void setKilled_ShouldSucceed(){
        KillUpdate killUpdate = new KillUpdate(killer, killed, deaths);
        killUpdate.setKilled(killed);
        assertEquals(killed, killUpdate.getKilled());
    }

    @Test
    public void serialize_shouldSucceed() throws IOException {
        KillUpdate killUpdate = new KillUpdate(killer, killed, deaths);
        String s = objectMapper.writeValueAsString(killUpdate);
        KillUpdate deserializedKillUpdate = objectMapper.readValue(s, KillUpdate.class);
        assertEquals(killer, deserializedKillUpdate.getKiller());
        assertEquals(killed, deserializedKillUpdate.getKilled());
        assertEquals(deaths, deserializedKillUpdate.getDeaths());

    }

}
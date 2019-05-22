package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import org.junit.Test;

import static org.junit.Assert.*;

public class KillUpdateTest {

    private String killer;
    private String killed;

    @Test
    public void setKiller_ShouldSucceed(){
        KillUpdate killUpdate = new KillUpdate(killer, killed);
        String killer = "Akiller";
        killUpdate.setKiller(killer);
        assertEquals(killer, killUpdate.getKiller());
    }

    @Test
    public void setKilled_ShouldSucceed(){
        KillUpdate killUpdate = new KillUpdate(killer, killed);
        String killed = "Akilled";
        killUpdate.setKilled(killed);
        assertEquals(killed, killUpdate.getKilled());
    }

}
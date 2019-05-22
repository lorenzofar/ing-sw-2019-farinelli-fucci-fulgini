package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import org.junit.Test;

import static org.junit.Assert.*;

public class OverkillUpdateTest {

    private String killer;
    private String killed;

    @Test
    public void setKiller_ShouldSucceed(){
        OverkillUpdate overkillUpdate = new OverkillUpdate(killer, killed);
        String killer = "Akiller";
        overkillUpdate.setKiller(killer);
        assertEquals(killer, overkillUpdate.getKiller());
    }

    @Test
    public void setKilled_ShouldSucceed(){
        OverkillUpdate overkillUpdate = new OverkillUpdate(killer, killed);
        String killed = "Akilled";
        overkillUpdate.setKilled(killed);
        assertEquals(killed, overkillUpdate.getKilled());
    }

}
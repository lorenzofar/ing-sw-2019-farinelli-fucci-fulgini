package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReloadUpdateTest {

    private String player = "Player";
    private String weapon = "Weapon";

    @Test
    public void setPlayer_ShouldSucceed() {
        ReloadUpdate update = new ReloadUpdate(player, weapon);
        String player = "Fiziano";
        update.setPlayer(player);
        assertEquals(player, update.getPlayer());
    }

    @Test
    public void setWeapon_ShouldSucceed() {
        ReloadUpdate update = new ReloadUpdate(player, weapon);
        String weapon = "Rifle";
        update.setWeapon(weapon);
        assertEquals(weapon, update.getWeapon());
    }

}
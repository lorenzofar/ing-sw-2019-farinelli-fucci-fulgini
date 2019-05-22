package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import org.junit.Test;

import static org.junit.Assert.*;

public class RemovedWeaponUpdateTest {

    private String player = "Player";
    private String weaponId = "Weapon";

    @Test
    public void setPlayer_ShouldSucceed() {
        RemovedWeaponUpdate update = new RemovedWeaponUpdate(player, weaponId);
        String player = "Fiorenzo";
        update.setPlayer(player);
        assertEquals(player, update.getPlayer());
    }

    @Test
    public void setWeapon_ShouldSucceed() {
        RemovedWeaponUpdate update = new RemovedWeaponUpdate(player, weaponId);
        String weapon = "Pistol";
        update.setWeapon(weapon);
        assertEquals(weapon, update.getWeapon());
    }
}
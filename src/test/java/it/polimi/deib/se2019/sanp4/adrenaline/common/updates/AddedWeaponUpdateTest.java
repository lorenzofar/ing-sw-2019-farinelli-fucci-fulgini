package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import org.junit.Test;

import static org.junit.Assert.*;

public class AddedWeaponUpdateTest {

    private String player = "Player";
    private String weapon = "Weapon";

    @Test
    public void setPlayer_ShouldSucceed() {
        AddedWeaponUpdate update = new AddedWeaponUpdate(player, weapon);
        String player = "Forenzo";
        update.setPlayer(player);
        assertEquals(player, update.getPlayer());
    }

    @Test
    public void setWeapon_ShouldSucceed() {
        AddedWeaponUpdate update = new AddedWeaponUpdate(player, weapon);
        String weapon = "Gun";
        update.setWeapon(weapon);
        assertEquals(weapon, update.getWeapon());
    }
}
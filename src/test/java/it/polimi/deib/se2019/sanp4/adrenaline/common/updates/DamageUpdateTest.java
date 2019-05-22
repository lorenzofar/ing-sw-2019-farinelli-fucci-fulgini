package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class DamageUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();

    private String shooter = "Cooper";
    private String shot = "Scott";
    private int damage = 5;

    @Test
    public void serialize_ShouldSucceed() throws IOException {
        DamageUpdate update = new DamageUpdate(shooter, shot, damage);
        String s = objectMapper.writeValueAsString(update);
        DamageUpdate damageUpdate = objectMapper.readValue(s, DamageUpdate.class);
        assertEquals(shooter, damageUpdate.getShooter());
        assertEquals(shot, damageUpdate.getShot());
        assertEquals(damage, damageUpdate.getDamage());
    }

    @Test
    public void setShooter_ShouldSucceed() {
        DamageUpdate update = new DamageUpdate(shooter, shot, damage);
        String shooter = "Soldier";
        update.setShooter(shooter);
        assertEquals(shooter, update.getShooter());
    }

    @Test
    public void setShot_ShouldSucceed() {
        DamageUpdate update = new DamageUpdate(shooter, shot, damage);
        String shot = "Enemy";
        update.setShot(shot);
        assertEquals(shot, update.getShot());
    }

    @Test
    public void setDamage_ShouldSucceed() {
        DamageUpdate update = new DamageUpdate(shooter, shot, damage);
        int damage = 3;
        update.setDamage(damage);
        assertEquals(damage, update.getDamage());
    }
}
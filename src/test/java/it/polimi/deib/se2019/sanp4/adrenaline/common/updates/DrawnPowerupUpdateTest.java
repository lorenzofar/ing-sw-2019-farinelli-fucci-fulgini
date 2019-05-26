package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class DrawnPowerupUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();
    private String player = "Akiller";
    private PowerupCard powerupCard;
    private PowerupEnum powerup = PowerupEnum.TARGETING_SCOPE;
    private AmmoCube ammoCube = AmmoCube.YELLOW;

    @Test
    public void serialize_ShouldSucceed() throws IOException {
        powerupCard = new PowerupCard (powerup, ammoCube);

        DrawnPowerupUpdate update = new DrawnPowerupUpdate(player, powerupCard);
        String s = objectMapper.writeValueAsString(update);
        System.out.println(s);
        DrawnPowerupUpdate drawnPowerupUpdate = objectMapper.readValue(s, DrawnPowerupUpdate.class);

        assertEquals(player, drawnPowerupUpdate.getPlayer());
        assertEquals(powerupCard, drawnPowerupUpdate.getPowerupCard());

        /* Setters */

        player = "Akimbo";
        powerupCard = new PowerupCard(PowerupEnum.NEWTON, AmmoCube.RED);
        update.setPlayer(player);
        update.setPowerupCard(powerupCard);

        s = objectMapper.writeValueAsString(update);
        drawnPowerupUpdate = objectMapper.readValue(s, DrawnPowerupUpdate.class);

        assertEquals(player, drawnPowerupUpdate.getPlayer());
        assertEquals(powerupCard, drawnPowerupUpdate.getPowerupCard());
    }
}
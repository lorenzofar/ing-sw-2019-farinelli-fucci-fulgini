package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.JSONUtils;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DrawnPowerupUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();
    private String player = "Akiller";
    private PowerupCard powerupCard;
    private PowerupEnum powerup = PowerupEnum.TARGETING_SCOPE;
    private AmmoCube ammoCube = AmmoCube.YELLOW;
    @Mock
    private static ModelUpdateVisitor visitor;

    @Test
    public void serialize_ShouldSucceed() throws IOException {
        powerupCard = new PowerupCard(powerup, ammoCube);

        DrawnPowerupUpdate update = new DrawnPowerupUpdate(player, powerupCard);
        String s = objectMapper.writeValueAsString(update);
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

    @Test
    public void accept_shouldAcceptVisitor() {
        DrawnPowerupUpdate update = new DrawnPowerupUpdate("player", new PowerupCard(powerup, ammoCube));

        update.accept(visitor);

        verify(visitor).handle(update);
    }
}
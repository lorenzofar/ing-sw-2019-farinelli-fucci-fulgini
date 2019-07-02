package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.JSONUtils;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.EffectDescription;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DrawnWeaponUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();
    private String player = "Policeman";
    private WeaponCard weaponCard;
    @Mock
    private static ModelUpdateVisitor visitor;

    public WeaponCard createWeaponCard(String description){
        String id = "0";
        String name = "Manganello";
        List<AmmoCubeCost> cost = new LinkedList<>();
        cost.add(AmmoCubeCost.BLUE);
        cost.add(AmmoCubeCost.YELLOW);
        List<EffectDescription> effects = new LinkedList<>();
        String effectName = "Manganellata";
        List<AmmoCubeCost> effectCost = new LinkedList<>();
        effectCost.add(AmmoCubeCost.RED);
        EffectDescription effectDescription = new EffectDescription(id, effectName, description, effectCost);
        return new WeaponCard(id, name, cost, effects);
    }

    @Test
    public void serialize_ShouldSucceed() throws IOException {
        weaponCard = createWeaponCard("Un poliziotto esprime il suo disappunto");

        DrawnWeaponUpdate update = new DrawnWeaponUpdate(player, weaponCard);
        String s = objectMapper.writeValueAsString(update);
        DrawnWeaponUpdate drawnWeaponUpdate = objectMapper.readValue(s, DrawnWeaponUpdate.class);

        assertEquals(player, drawnWeaponUpdate.getPlayer());
        assertEquals(weaponCard, drawnWeaponUpdate.getWeaponCard());

        /* Setters */
        weaponCard = createWeaponCard("Un carabiniere esprime il suo disappunto");
        player = "Carabiniere";
        update.setPlayer(player);
        update.setWeaponCard(weaponCard);

        s = objectMapper.writeValueAsString(update);
        drawnWeaponUpdate = objectMapper.readValue(s, DrawnWeaponUpdate.class);

        assertEquals(player, drawnWeaponUpdate.getPlayer());
        assertEquals(weaponCard, drawnWeaponUpdate.getWeaponCard());

    }

    @Test
    public void accept_shouldAcceptVisitor() {
        DrawnWeaponUpdate update = new DrawnWeaponUpdate("player", createWeaponCard("desc"));

        update.accept(visitor);

        verify(visitor).handle(update);
    }
}
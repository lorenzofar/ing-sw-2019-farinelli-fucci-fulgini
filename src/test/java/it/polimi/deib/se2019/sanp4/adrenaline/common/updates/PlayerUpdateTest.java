package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.EffectDescription;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;
import it.polimi.deib.se2019.sanp4.adrenaline.common.JSONUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class PlayerUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();

    private PlayerView playerView;
    private String name = "Luca";
    private int score = 777;
    private Collection<ActionEnum> actions = new HashSet<>();
    private ActionCard actionCard;
    private PlayerColor color = PlayerColor.BLUE;
    private PowerupCard powerupCard = new PowerupCard(PowerupEnum.TELEPORTER, AmmoCube.YELLOW);
    private List<WeaponCard> weaponCards = new LinkedList<>();
    private List<AmmoCubeCost> cost = new ArrayList<>();
    private List<EffectDescription> effects = new ArrayList<>();

    @Test
    public void serialize_ShouldSucceed() throws IOException {
        /*Initialize all the attributes */
        cost.add(AmmoCubeCost.BLUE);
        effects.add(new EffectDescription("1", "Effect",
                "Description", cost));
        weaponCards.add(new WeaponCard("1", "Pistol", cost, effects));
        actions.add(ActionEnum.ADRENALINE_GRAB);
        actionCard = new ActionCard(1, ActionCardEnum.ADRENALINE1,
                                    actions, ActionEnum.RELOAD);
        Map<AmmoCube, Integer> ammo = new HashMap<>();
        ammo.put(AmmoCube.RED, 1);

        /* Construct playerView */
        playerView = new PlayerView(name, color);
        playerView.setAmmo(ammo);
        playerView.setWeapons(weaponCards);
        playerView.setScore(score);

        /* Serialize */
        PlayerUpdate update = new PlayerUpdate(playerView);
        String s = objectMapper.writeValueAsString(update);

        /* Read */
        PlayerUpdate playerUpdate = objectMapper.readValue(s, PlayerUpdate.class);

        /* Check all the attributes */
        assertEquals(playerView.getName(), playerUpdate.getPlayer().getName());
        assertEquals(playerView.getColor(), playerUpdate.getPlayer().getColor());
        assertEquals(playerView.getScore(), playerUpdate.getPlayer().getScore());
        assertEquals(playerView.getAmmo(), playerUpdate.getPlayer().getAmmo());
        assertEquals(playerView.getWeapons(), playerUpdate.getPlayer().getWeapons());
        assertEquals(playerView.getPowerups(), playerUpdate.getPlayer().getPowerups());
    }
}
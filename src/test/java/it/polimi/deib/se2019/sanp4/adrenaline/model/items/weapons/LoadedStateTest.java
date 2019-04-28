package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerCharacter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class LoadedStateTest {

    private static WeaponCard weaponCard;
    private static final String validId = "weaponone";
    private static final String validName = "Weapon One";

    private static ActionCard validActionCard;
    private static String validPlayerName = "player1";
    private static PlayerCharacter validCharacter;
    private static Map<AmmoCube, Integer> initialPlayerAmmo;

    @BeforeClass
    public static void setup(){
        List<AmmoCubeCost> validCost = new ArrayList<>();
        validCost.add(AmmoCubeCost.BLUE);
        validCost.add(AmmoCubeCost.RED);
        validCost.add(AmmoCubeCost.YELLOW);
        List<EffectDescription> validEffects = new ArrayList<>();
        weaponCard = new WeaponCard(validId, validName, validCost, validEffects);

        List<ActionEnum> validActions = new ArrayList<>();
        validActions.add(ActionEnum.ADRENALINE_GRAB);
        validActions.add(ActionEnum.ADRENALINE_SHOOT);
        validActionCard = new ActionCard(2, ActionCardEnum.ADRENALINE1, validActions, ActionEnum.RELOAD);

        initialPlayerAmmo = new EnumMap<>(AmmoCube.class);
        for(int i = 0; i<AmmoCube.values().length; i++) {
            initialPlayerAmmo.put(AmmoCube.values()[i], Player.INITIAL_AMMO);
        }
        validCharacter = new PlayerCharacter("name", "description", RoomColor.BLUE);
    }

    @Test
    public void checkUsable_shouldReturnTrue(){
        assertTrue(new LoadedState().isUsable());
    }

    @Test
    public void unloadWeapon_checkState_shouldBeUnloaded(){
        weaponCard.setState(new LoadedState());
        weaponCard.getState().unload(weaponCard);
        assertEquals(UnloadedState.class, weaponCard.getState().getClass());
    }

    @Test
    public void resetWeapon_checkState_shouldBePickup(){
        weaponCard.setState(new LoadedState());
        weaponCard.getState().reset(weaponCard);
        assertEquals(PickupState.class, weaponCard.getState().getClass());
    }

    @Test(expected = NullPointerException.class)
    public void reloadWeapon_nullPlayerProvided_shouldThrowNullPointerException(){
        weaponCard.setState(new LoadedState());
        weaponCard.getState().reload(null, weaponCard);
    }

    @Test(expected = NullPointerException.class)
    public void reloadWeapon_nullWeaponProvided_shouldThrowNullPointerException() {
        Player player = new Player(validPlayerName, validActionCard, validCharacter);
        weaponCard.setState(new LoadedState());
        weaponCard.getState().reload(player, null);
    }

    @Test
    public void reloadWeapon_checkPlayerAmmo_shouldNotBeDecreasedAndWeaponStateShouldBeLoaded(){
        Player player = new Player(validPlayerName, validActionCard, validCharacter);
        // Here the player only has initial ammo
        try {
            player.addWeapon(weaponCard);
            weaponCard.setState(new LoadedState());
            weaponCard.getState().reload(player, weaponCard);
            assertEquals(initialPlayerAmmo, player.getAmmo());
            assertEquals(LoadedState.class, weaponCard.getState().getClass());
        } catch (FullCapacityException e) {
            fail();
        }
    }

}

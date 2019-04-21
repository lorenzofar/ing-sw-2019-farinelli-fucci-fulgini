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

import java.util.*;

import static org.junit.Assert.*;

public class UnloadedStateTest {

    private static WeaponCard weaponCard;
    private static final String validId = "weaponone";
    private static final String validName = "Weapon One";

    private static ActionCard validActionCard;
    private static String validPlayerName = "player1";
    private static PlayerCharacter validCharacter;
    private static Map<AmmoCube, Integer> validPlayerAmmo;

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

        validPlayerAmmo = new EnumMap<>(AmmoCube.class);
        validPlayerAmmo.put(AmmoCube.BLUE, 1);
        validPlayerAmmo.put(AmmoCube.RED, 1);
        validPlayerAmmo.put(AmmoCube.YELLOW, 1);
        validCharacter = new PlayerCharacter("name", "description", RoomColor.BLUE);
    }

    @Test
    public void checkUsable_shouldReturnFalse(){
        assertFalse(new UnloadedState().isUsable());
    }

    @Test
    public void unloadWeapon_checkState_shouldBeUnloaded(){
        weaponCard.setState(new UnloadedState());
        weaponCard.getState().unload(weaponCard);
        assertEquals(UnloadedState.class, weaponCard.getState().getClass());
    }

    @Test
    public void resetWeapon_checkState_shouldBePickup(){
        weaponCard.setState(new UnloadedState());
        weaponCard.getState().reset(weaponCard);
        assertEquals(PickupState.class, weaponCard.getState().getClass());
    }

    @Test(expected = NullPointerException.class)
    public void reloadWeapon_nullPlayerProvided_shouldThrowNullPointerException(){
        weaponCard.setState(new UnloadedState());
        weaponCard.getState().reload(null, weaponCard);
    }

    @Test(expected = NullPointerException.class)
    public void reloadWeapon_nullWeaponProvided_shouldThrowNullPointerException() {
        Player player = new Player(validPlayerName, validActionCard, validCharacter);
        weaponCard.setState(new UnloadedState());
        weaponCard.getState().reload(player, null);
    }

    @Test
    public void reloadWeapon_playerDoesNotHaveWeapon_playerAmmoShouldNotBeDecreasedAndWeaponStateShouldBeUnloaded(){
        Player player = new Player(validPlayerName, validActionCard, validCharacter);
        player.addAmmo(validPlayerAmmo);
        weaponCard.setState(new UnloadedState());
        weaponCard.getState().reload(player, weaponCard);
        assertEquals(validPlayerAmmo, player.getAmmo());
        assertEquals(UnloadedState.class, weaponCard.getState().getClass());
    }

    @Test
    public void reloadWeapon_playerHasWeaponButNotEnoughAmmo_playerAmmoShouldNotBeDecreasedAndWeaponStateShouldBeUnloaded(){
        Player player = new Player(validPlayerName, validActionCard, validCharacter);
        player.addAmmo(Collections.emptyMap());
        weaponCard.setState(new UnloadedState());
        weaponCard.getState().reload(player, weaponCard);
        assertEquals(new HashMap<AmmoCube, Integer>(), player.getAmmo());
        assertEquals(UnloadedState.class, weaponCard.getState().getClass());
    }

    @Test
    public void reloadWeapon_playerHasWeaponAndEnoughAmmo_playerAmmoShouldBeDecreasedAndWeaponStateShouldBeLoaded(){
        Player player = new Player(validPlayerName, validActionCard, validCharacter);
        player.addAmmo(validPlayerAmmo);
        try {
            player.addWeapon(weaponCard);
        } catch (FullCapacityException e) {
            fail();
        }
        weaponCard.setState(new UnloadedState());
        weaponCard.getState().reload(player, weaponCard);
        Map<AmmoCube, Integer> emptyAmmo = new EnumMap<>(validPlayerAmmo);
        emptyAmmo.replaceAll((color, count) -> 0);
        assertEquals(emptyAmmo, player.getAmmo());
        assertEquals(LoadedState.class, weaponCard.getState().getClass());
    }
}

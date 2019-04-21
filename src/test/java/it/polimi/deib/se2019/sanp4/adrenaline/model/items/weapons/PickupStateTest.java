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
import static org.junit.Assert.assertEquals;

public class PickupStateTest {

    private static WeaponCard weaponCard;
    private static final String validId = "weaponone";
    private static final String validName = "Weapon One";
    private static List<AmmoCubeCost> validCost;

    private static ActionCard validActionCard;
    private static String validPlayerName = "player1";
    private static PlayerCharacter validCharacter;
    private static Map<AmmoCube, Integer> validPlayerAmmo;

    @BeforeClass
    public static void setup(){
        validCost = new ArrayList<>();
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
        assertFalse(new PickupState().isUsable());
    }

    @Test
    public void unloadWeapon_checkState_shouldBeUnloaded(){
        weaponCard.setState(new PickupState());
        weaponCard.getState().unload(weaponCard);
        assertEquals(UnloadedState.class, weaponCard.getState().getClass());
    }

    @Test
    public void resetWeapon_checkState_shouldBePickup(){
        weaponCard.setState(new PickupState());
        weaponCard.getState().reset(weaponCard);
        assertEquals(PickupState.class, weaponCard.getState().getClass());
    }

    @Test(expected = NullPointerException.class)
    public void reloadWeapon_nullPlayerProvided_shouldThrowNullPointerException(){
        weaponCard.setState(new PickupState());
        weaponCard.getState().reload(null, weaponCard);
    }

    @Test(expected = NullPointerException.class)
    public void reloadWeapon_nullWeaponProvided_shouldThrowNullPointerException() {
        Player player = new Player(validPlayerName, validActionCard, validCharacter);
        weaponCard.setState(new PickupState());
        weaponCard.getState().reload(player, null);
    }

    @Test
    public void reloadWeapon_playerDoesNotHaveWeapon_playerAmmoShouldNotBeDecreasedAndWeaponStateShouldBePickup(){
        Player player = new Player(validPlayerName, validActionCard, validCharacter);
        player.addAmmo(validPlayerAmmo);
        weaponCard.setState(new PickupState());
        weaponCard.getState().reload(player, weaponCard);
        assertEquals(validPlayerAmmo, player.getAmmo());
        assertEquals(PickupState.class, weaponCard.getState().getClass());
    }

    @Test
    public void reloadWeapon_playerHasWeaponButNotEnoughAmmo_playerAmmoShouldNotBeDecreasedAndWeaponStateShouldBePickup(){
        Player player = new Player(validPlayerName, validActionCard, validCharacter);
        player.addAmmo(Collections.emptyMap());
        weaponCard.setState(new PickupState());
        weaponCard.getState().reload(player, weaponCard);
        assertEquals(new HashMap<AmmoCube, Integer>(), player.getAmmo());
        assertEquals(PickupState.class, weaponCard.getState().getClass());
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
        weaponCard.setState(new PickupState());
        weaponCard.getState().reload(player, weaponCard);
        Map<AmmoCube, Integer> testAmmo = new EnumMap<>(validPlayerAmmo);
        testAmmo.replaceAll((color, count) -> 0);
        // Since the first cube is already loaded, I test that it is not removed from the player
        testAmmo.put(validCost.get(0).getCorrespondingCube(), 1);
        assertEquals(testAmmo, player.getAmmo());
        assertEquals(LoadedState.class, weaponCard.getState().getClass());
    }

    @Test
    public void reloadWeapon_playerHasWeaponAndEnoughAmmo_weaponCostsOnlyOneCube_playerAmmoShouldNotBeDecreasedAndWeaponStateShouldBeLoaded(){
        List<AmmoCubeCost> oneCubeCost = new ArrayList<>();
        oneCubeCost.add(AmmoCubeCost.YELLOW);
        WeaponCard oneCubecard = new WeaponCard(validId ,validName, oneCubeCost, new ArrayList<>());
        Player player = new Player(validPlayerName, validActionCard, validCharacter);
        player.addAmmo(validPlayerAmmo);
        try {
            player.addWeapon(oneCubecard);
        } catch (FullCapacityException e) {
            fail();
        }
        oneCubecard.setState(new PickupState());
        oneCubecard.getState().reload(player, oneCubecard);
        assertEquals(validPlayerAmmo, player.getAmmo());
        assertEquals(LoadedState.class, oneCubecard.getState().getClass());
    }
}
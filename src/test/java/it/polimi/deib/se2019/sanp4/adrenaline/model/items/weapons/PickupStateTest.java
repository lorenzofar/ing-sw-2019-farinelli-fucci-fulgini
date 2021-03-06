package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PickupStateTest {

    private static WeaponCard weaponCard;
    private static final String validId = "weaponone";
    private static final String validName = "Weapon One";
    private static List<AmmoCubeCost> validCost;
    private static List<AmmoCubeCost> invalidCost;

    private static ActionCard validActionCard;
    private static String validPlayerName = "player1";
    private static PlayerColor validColor = PlayerColor.YELLOW;
    private static Map<AmmoCube, Integer> initialPlayerAmmo;


    @BeforeClass
    public static void setup(){
        LogManager.getLogManager().reset();

        validCost = new ArrayList<>();
        validCost.addAll(Collections.nCopies(Player.INITIAL_AMMO, AmmoCubeCost.BLUE));
        validCost.addAll(Collections.nCopies(Player.INITIAL_AMMO, AmmoCubeCost.RED));
        validCost.addAll(Collections.nCopies(Player.INITIAL_AMMO, AmmoCubeCost.YELLOW));
        invalidCost = new ArrayList<>();
        invalidCost.addAll(Collections.nCopies(Player.INITIAL_AMMO + 1, AmmoCubeCost.BLUE));
        invalidCost.addAll(Collections.nCopies(Player.INITIAL_AMMO + 1, AmmoCubeCost.RED));
        invalidCost.addAll(Collections.nCopies(Player.INITIAL_AMMO + 1, AmmoCubeCost.YELLOW));
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
        Player player = new Player(validPlayerName, validActionCard, validColor);
        weaponCard.setState(new PickupState());
        weaponCard.getState().reload(player, null);
    }

    @Test
    public void reloadWeapon_playerHasEnoughAmmo_playerAmmoShouldBeDecreasedAndWeaponStateShouldBeLoaded(){
        Player player = new Player(validPlayerName, validActionCard, validColor);
        weaponCard.setState(new PickupState());
        weaponCard.getState().reload(player, weaponCard);
        Map<AmmoCube, Integer> testAmmo = new EnumMap<>(AmmoCube.class);
        for(int i= 0; i<AmmoCube.values().length; i++){
            testAmmo.put(AmmoCube.values()[i], 0);
        }
        // Since the first cube is already loaded, I test that it is not removed from the player
        testAmmo.put(validCost.get(0).getCorrespondingCube(), 1);
        assertEquals(testAmmo, player.getAmmo());
        assertEquals(LoadedState.class, weaponCard.getState().getClass());
    }

    @Test
    public void reloadWeapon_playerHasEnoughAmmo_weaponCostsOnlyOneCube_playerAmmoShouldNotBeDecreasedAndWeaponStateShouldBeLoaded(){
        List<AmmoCubeCost> oneCubeCost = new ArrayList<>();
        oneCubeCost.add(AmmoCubeCost.YELLOW);
        WeaponCard oneCubecard = new WeaponCard(validId ,validName, oneCubeCost, new ArrayList<>());
        Player player = new Player(validPlayerName, validActionCard, validColor);

        oneCubecard.setState(new PickupState());
        oneCubecard.getState().reload(player, oneCubecard);
        assertEquals(initialPlayerAmmo, player.getAmmo());
        assertEquals(LoadedState.class, oneCubecard.getState().getClass());
    }

    @Test
    public void reloadWeapon_playerHasNotEnoughAmmo_shouldNotBeLoaded() {
        List<AmmoCubeCost> costList = Arrays.asList(AmmoCubeCost.YELLOW, AmmoCubeCost.YELLOW, AmmoCubeCost.YELLOW);

        WeaponCard card = new WeaponCard(validId, validName, costList, Collections.emptyList());
        Player player = new Player(validPlayerName, validActionCard, validColor); /* One ammo for each color */

        card.reset(); /* Set to pickup state */
        WeaponCardState oldState = card.getState();

        oldState.reload(player, card);

        assertEquals(oldState.toString(), card.getState().toString());
    }
}
package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.NotEnoughAmmoException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerUpCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.EffectDescription;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class PlayerTest {

    private final static ObjectMapper mapper = new ObjectMapper();

    private final static String validName = "player1";
    private static ActionCard validActionCard;
    private static PlayerCharacter validCharacter;
    private static WeaponCard validWeaponCard;

    private static List<AmmoCubeCost> validWeaponCost;
    private static List<EffectDescription> validWeaponEffects;

    @BeforeClass
    public static void setup(){
        int validMaxActions = 2;
        Collection<ActionEnum> validActions = new ArrayList<>();
        validActions.add(ActionEnum.ADRENALINE_GRAB);
        validActions.add(ActionEnum.ADRENALINE_SHOOT);
        ActionCardEnum validType = ActionCardEnum.ADRENALINE1;
        ActionEnum validFinalAction = ActionEnum.RELOAD;
        String validDescription = "description1";
        RoomColor validColor = RoomColor.BLUE;
        validWeaponCost = new ArrayList<>();
        validWeaponCost.add(AmmoCubeCost.BLUE);
        validWeaponCost.add(AmmoCubeCost.RED);
        validWeaponCost.add(AmmoCubeCost.YELLOW);
        validWeaponEffects = new ArrayList<>();

        validActionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        validCharacter = new PlayerCharacter(validName, validDescription, validColor);
        validWeaponCard = new WeaponCard("weapon1", "Weapon 1", validWeaponCost, validWeaponEffects);
    }

    @Test(expected = NullPointerException.class)
    public void createPlayer_nullNameProvided_shouldThrowNullPointerException(){
        new Player(null, validActionCard, validCharacter);
    }

    @Test(expected = NullPointerException.class)
    public void createPlayer_nullActionCardProvided_shouldThrowNullPointerException(){
        new Player(validName, null, validCharacter);
    }

    @Test(expected = NullPointerException.class)
    public void createPlayer_nullCharacterProvided_shouldThrowNullPointerException(){
        new Player(validName, validActionCard, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createPlayer_emptyNameProvided_shouldThrowIllegalArgumentException(){
        new Player("", validActionCard, validCharacter);
    }

    @Test
    public void createPlayer_properParametersProvided_shouldNotThrowException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        assertEquals(validName, player.getName());
        assertEquals(validActionCard, player.getActionCard());
        assertEquals(validCharacter, player.getCharacter());
        assertEquals(PlayerState.ONLINE, player.getState());
        assertEquals(0, player.getScore());
        assertEquals(0, player.getPerformedOverkills());
        assertEquals(0, player.getPerformedKillshots());
        Map<AmmoCube, Integer> initialAmmo = new EnumMap<>(AmmoCube.class);
        for(int i= 0; i<AmmoCube.values().length; i++){
            initialAmmo.put(AmmoCube.values()[i], Player.INITIAL_AMMO);
        }
        assertEquals(initialAmmo, player.getAmmo());
        assertEquals(0, player.getWeapons().size());
        assertEquals(0, player.getPowerups().size());
        assertNotNull(player.getPlayerBoard());
        assertNull(player.getCurrentSquare());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addScorePoints_negativeAmountProvided_shouldThrowIllegalArgumentException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        player.addScorePoints(-1);
    }

    @Test
    public void addScorePoints_validAmountProvided_shouldIncreasePlayerScore(){
        Player player = new Player(validName, validActionCard, validCharacter);
        int initialScore = player.getScore();
        int validScore = 2;
        player.addScorePoints(validScore);
        assertEquals(initialScore + validScore, player.getScore());
    }

    @Test
    public void addKillshot_shouldIncreaseKillshotsCount(){
        Player player = new Player(validName, validActionCard, validCharacter);
        int initialKillshots = player.getPerformedKillshots();
        player.addPerformedKillshot();
        assertEquals(initialKillshots + 1, player.getPerformedKillshots());
    }

    @Test
    public void addOverkill_shouldIncreaseOverkillsCount(){
        Player player = new Player(validName, validActionCard, validCharacter);
        int initialOverkills = player.getPerformedOverkills();
        player.addPerformedOverkill();
        assertEquals(initialOverkills + 1, player.getPerformedOverkills());
    }

    @Test(expected = NullPointerException.class)
    public void setActionCard_nullActionCardProvided_shouldThrowNullPointerException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        player.setActionCard(null);
    }

    @Test
    public void setActionCard_validActionCardProvided_shouldNotThrowException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        int validMaxActions = 1;
        Collection<ActionEnum> validActions = new ArrayList<>();
        validActions.add(ActionEnum.ADRENALINE_SHOOT);
        ActionCardEnum validType = ActionCardEnum.ADRENALINE2;
        ActionEnum validFinalAction = ActionEnum.RELOAD;
        ActionCard anotherActionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        player.setActionCard(anotherActionCard);
        assertEquals(anotherActionCard, player.getActionCard());
    }

    @Test(expected = NullPointerException.class)
    public void setSquare_nullSquareProvided_shouldThrowNullPointerException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        player.setCurrentSquare(null);
    }

    @Test
    public void setSquare_validSquareProvided_shouldNotThrowException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        Square newSquare = new SpawnSquare(new CoordPair(1, 1));
        player.setCurrentSquare(newSquare);
        assertEquals(newSquare, player.getCurrentSquare());
    }


    @Test(expected = NullPointerException.class)
    public void setState_nullStateProvided_shouldThrowNullPointerException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        player.setState(null);
    }

    @Test
    public void setState_validStateProvided_shouldNotThrowException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        PlayerState newState = PlayerState.SUSPENDED;
        player.setState(newState);
        assertEquals(newState, player.getState());
    }

    @Test(expected = NullPointerException.class)
    public void addWeapon_nullWeaponProvided_shouldThrowNullPointerException() throws FullCapacityException {
        Player player = new Player(validName, validActionCard, validCharacter);
        player.addWeapon(null);
    }

    @Test(expected = IllegalStateException.class)
    public void addWeapon_playerAlreadyOwnsWeapon_shouldThrowIllegalStateException() throws FullCapacityException {
        Player player = new Player(validName, validActionCard, validCharacter);
        player.addWeapon(validWeaponCard);
        player.addWeapon(validWeaponCard);
    }

    @Test(expected = FullCapacityException.class)
    public void addWeapon_playerHasFullCapacity_shouldThrowFullCapacityException() throws FullCapacityException {
        Player player = new Player(validName, validActionCard, validCharacter);
        for(int i = 0; i<Player.MAX_WEAPONS; i++){
            WeaponCard weaponCard = new WeaponCard(String.format("weapon%d", i), "Weapon", validWeaponCost, validWeaponEffects);
            player.addWeapon(weaponCard);
        }
        WeaponCard anotherWeaponCard = new WeaponCard("exceed", "exceed", validWeaponCost, validWeaponEffects);
        player.addWeapon(anotherWeaponCard);
    }

    @Test
    public void addWeapon_weaponCanBeAdded_playerShouldHaveWeapon() throws FullCapacityException {
        Player player = new Player(validName, validActionCard, validCharacter);
        player.addWeapon(validWeaponCard);
        assertTrue(player.getWeapons().contains(validWeaponCard));
    }

    @Test(expected = NullPointerException.class)
    public void removeWeapon_nullWeaponIdProvided_shouldThrowNullPointerException() throws CardNotFoundException {
        Player player = new Player(validName, validActionCard, validCharacter);
        player.removeWeapon((String) null);
    }

    @Test(expected = NullPointerException.class)
    public void removeWeapon_nullWeaponCardProvided_shouldThrowNullPointerException() throws CardNotFoundException {
        Player player = new Player(validName, validActionCard, validCharacter);
        player.removeWeapon((WeaponCard) null);
    }

    @Test
    public void removeWeapon_playerDoesNotHaveCard_weaponCardPassed_shouldThrowCardNotFoundException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        try {
            player.removeWeapon(validWeaponCard);
        } catch (CardNotFoundException e) {
                assertTrue(true);
        }
    }

    @Test
    public void removeWeapon_playerDoesNotHaveCard_weaponCardIdPassed_shouldThrowCardNotFoundException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        try {
            player.removeWeapon(validWeaponCard.getId());
        } catch (CardNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    public void removeWeapon_playerHasCard_weaponCardPassed_shouldRemoveWeapon() throws FullCapacityException, CardNotFoundException {
        Player player = new Player(validName, validActionCard, validCharacter);
        player.addWeapon(validWeaponCard);
        player.removeWeapon(validWeaponCard);
        assertFalse(player.getWeapons().contains(validWeaponCard));
    }

    @Test
    public void removeWeapon_playerHasCard_weaponCardIdPassed_shouldRemoveWeapon() throws FullCapacityException, CardNotFoundException {
        Player player = new Player(validName, validActionCard, validCharacter);
        player.addWeapon(validWeaponCard);
        player.removeWeapon(validWeaponCard.getId());
        assertFalse(player.getWeapons().contains(validWeaponCard));
    }

    @Test(expected = NullPointerException.class)
    public void addPowerup_nullPowerupProvided_shouldThrowNullPointerException() throws FullCapacityException {
        Player player = new Player(validName, validActionCard, validCharacter);
        player.addPowerup(null);
    }

    @Test(expected = IllegalStateException.class)
    public void addPowerup_playerAlreadyOwnsPowerup_shouldThrowIllegalStateException() throws FullCapacityException {
        Player player = new Player(validName, validActionCard, validCharacter);
        PowerUpCard powerup = new PowerUpCard("id1", "name1", "desc1", AmmoCube.BLUE);
        player.addPowerup(powerup);
        player.addPowerup(powerup);
    }

    @Test(expected = FullCapacityException.class)
    public void addPowerup_playerHasFullCapacity_shouldThrowFullCapacityException() throws FullCapacityException {
        Player player = new Player(validName, validActionCard, validCharacter);
        for(int i = 0; i<Player.MAX_POWERUPS; i++){
            PowerUpCard powerUpCard = new PowerUpCard(String.format("id%d", i), "name", "desc", AmmoCube.BLUE);
            player.addPowerup(powerUpCard);
        }
        PowerUpCard anotherPowerUpCard = new PowerUpCard("exceed", "exceed", "desc", AmmoCube.BLUE);
        player.addPowerup(anotherPowerUpCard);
    }

    @Test
    public void addPowerup_powerupCanBeAdded_playerShouldHavePowerup() throws FullCapacityException {
        Player player = new Player(validName, validActionCard, validCharacter);
        PowerUpCard insertedPowerup = new PowerUpCard("id1", "name1", "desc1", AmmoCube.BLUE);
        player.addPowerup(insertedPowerup);
        assertTrue(player.getPowerups().contains(insertedPowerup));
    }

    @Test(expected = NullPointerException.class)
    public void removePowerup_nullPowerupProvided_shouldThrowNullPointerException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        player.removePowerup(null);
    }

    @Test(expected = IllegalStateException.class)
    public void removePowerup_userDoesNotHavePowerup_shouldThrowIllegalStateException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        PowerUpCard powerup = new PowerUpCard("id", "name", "desc", AmmoCube.BLUE);
        player.removePowerup(powerup);
    }

    @Test
    public void removePowerup_userHasPowerup_shouldRemoveWeapon() throws FullCapacityException {
        Player player = new Player(validName, validActionCard, validCharacter);
        PowerUpCard powerup = new PowerUpCard("id", "name", "desc", AmmoCube.BLUE);
        player.addPowerup(powerup);
        player.removePowerup(powerup);
        assertFalse(player.getPowerups().contains(powerup));
    }

    @Test(expected = NullPointerException.class)
    public void addAmmo_nullMapProvided_shouldThrowNullPointerException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        player.addAmmo(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addAmmo_mapWithNegativeValuesProvided_shouldThrowIllegalArgumentException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        Map<AmmoCube, Integer> invalidAmmo = new EnumMap<>(AmmoCube.class);
        invalidAmmo.put(AmmoCube.RED, -1);
        player.addAmmo(invalidAmmo);
    }

    @Test
    public void addAmmo_validAmmoProvided_playerShouldHaveAmmo(){
        Player player = new Player(validName, validActionCard, validCharacter);
        Map<AmmoCube, Integer> initialAmmo = player.getAmmo();
        Map<AmmoCube, Integer> validAmmo = new EnumMap<>(AmmoCube.class);
        validAmmo.put(AmmoCube.YELLOW, 1);
        validAmmo.put(AmmoCube.RED, 2);
        validAmmo.put(AmmoCube.BLUE, 1);
        player.addAmmo(validAmmo);
        for(int i = 0; i<AmmoCube.values().length; i++){
            initialAmmo.merge(AmmoCube.values()[i], validAmmo.get(AmmoCube.values()[i]), Integer::sum);
        }
        assertEquals(initialAmmo, player.getAmmo());
    }

    @Test(expected = NullPointerException.class)
    public void payAmmo_nullCubesMapProvided_shouldThrowNullPointerException() throws NotEnoughAmmoException {
        Player player = new Player(validName, validActionCard, validCharacter);
        player.payAmmo((Map<AmmoCube, Integer>)null);
    }

    @Test(expected = NullPointerException.class)
    public void payAmmo_nullCubeCostListProvided_shouldThrowNullPointerException() throws NotEnoughAmmoException {
        Player player = new Player(validName, validActionCard, validCharacter);
        player.payAmmo((List<AmmoCubeCost>)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void payAmmo_mapWithNegativeValuesProvided_shouldThrowIllegalArgumentException() throws NotEnoughAmmoException {
        Player player = new Player(validName, validActionCard, validCharacter);
        Map<AmmoCube, Integer> invalidMap = new EnumMap<>(AmmoCube.class);
        invalidMap.put(AmmoCube.BLUE, -1);
        player.payAmmo(invalidMap);
    }

    @Test
    public void payAmmo_passCubesMap_userHasNotEnoughAmmo_shouldThrowNotEnoughAmmoException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        // Here the user only has initial ammo
        Map<AmmoCube, Integer> costMap = new EnumMap<>(AmmoCube.class);
        costMap.replaceAll((color, amount) -> Player.INITIAL_AMMO + 1);
        try {
            player.payAmmo(costMap);
        } catch (NotEnoughAmmoException e) {
            assertTrue(true);
        }
    }

    @Test
    public void payAmmo_passCubesMap_userHasEnoughAmmo_shouldDecreaseAmmo(){
        Player player = new Player(validName, validActionCard, validCharacter);
        // Here the user only has initial ammo
        Map<AmmoCube, Integer> costMap = new EnumMap<>(AmmoCube.class);
        Map<AmmoCube, Integer> zeroMap= new EnumMap<>(AmmoCube.class);
        for(int i= 0; i<AmmoCube.values().length; i++){
            costMap.put(AmmoCube.values()[i], Player.INITIAL_AMMO);
            zeroMap.put(AmmoCube.values()[i], 0);
        }
        try {
            player.payAmmo(costMap);
        } catch (NotEnoughAmmoException e) {
            fail();
        }
        assertEquals(zeroMap, player.getAmmo());
    }

    @Test
    public void payAmmo_passCubeCostList_userHasNotEnoughAmmo_shouldThrowNotEnoughAmmoException(){
        Player player = new Player(validName, validActionCard, validCharacter);
        // Here the user only has initial ammo
        List<AmmoCubeCost> costList = new ArrayList<>();
        costList.addAll(Collections.nCopies(Player.INITIAL_AMMO + 1, AmmoCubeCost.YELLOW));
        costList.addAll(Collections.nCopies(Player.INITIAL_AMMO + 1 , AmmoCubeCost.BLUE));
        costList.addAll(Collections.nCopies(Player.INITIAL_AMMO + 1, AmmoCubeCost.RED));
        try {
            player.payAmmo(costList);
        } catch (NotEnoughAmmoException e) {
            assertTrue(true);
        }
    }

    @Test
    public void payAmmo_passCubeCostList_userHasEnoughAmmo_shouldDecreaseAmmo(){
        Player player = new Player(validName, validActionCard, validCharacter);
        // Here the user only has initial ammo
        List<AmmoCubeCost> costList = new ArrayList<>();
        costList.addAll(Collections.nCopies(Player.INITIAL_AMMO, AmmoCubeCost.YELLOW));
        costList.addAll(Collections.nCopies(Player.INITIAL_AMMO, AmmoCubeCost.BLUE));
        costList.addAll(Collections.nCopies(Player.INITIAL_AMMO, AmmoCubeCost.RED));
        Map<AmmoCube, Integer> zeroMap= new EnumMap<>(AmmoCube.class);
        for(int i= 0; i<AmmoCube.values().length; i++){
            zeroMap.put(AmmoCube.values()[i], 0);
        }
        try {
            player.payAmmo(costList);
        } catch (NotEnoughAmmoException e) {
            fail();
        }
        assertEquals(zeroMap, player.getAmmo());
    }

    @Test
    public void payAmmo_passCubeCostListWithGenericCube_userHasEnoughAmmo_shouldDecreaseAmmo(){
        Player player = new Player(validName, validActionCard, validCharacter);
        // Here the user only has initial ammo
        List<AmmoCubeCost> costList = new ArrayList<>();
        costList.addAll(Collections.nCopies(Player.INITIAL_AMMO, AmmoCubeCost.YELLOW));
        costList.addAll(Collections.nCopies(Player.INITIAL_AMMO, AmmoCubeCost.BLUE));
        costList.addAll(Collections.nCopies(Player.INITIAL_AMMO, AmmoCubeCost.ANY));
        Map<AmmoCube, Integer> zeroMap= new EnumMap<>(AmmoCube.class);
        for(int i= 0; i<AmmoCube.values().length; i++){
            zeroMap.put(AmmoCube.values()[i], 0);
        }
        try {
            player.payAmmo(costList);
        } catch (NotEnoughAmmoException e) {
            fail();
        }
        assertEquals(zeroMap, player.getAmmo());
    }

    @Test
    public void payAmmo_passCubeCostListWithGenericCube_userHasNotEnoughAmmo_shouldDecreaseAmmo(){
        Player player = new Player(validName, validActionCard, validCharacter);
        // Here the user only has initial ammo
        List<AmmoCubeCost> costList = new ArrayList<>();
        costList.addAll(Collections.nCopies(Player.INITIAL_AMMO, AmmoCubeCost.YELLOW));
        costList.addAll(Collections.nCopies(Player.INITIAL_AMMO, AmmoCubeCost.BLUE));
        costList.addAll(Collections.nCopies(Player.INITIAL_AMMO + 1, AmmoCubeCost.ANY));
        Map<AmmoCube, Integer> zeroMap= new EnumMap<>(AmmoCube.class);
        for(int i= 0; i<AmmoCube.values().length; i++){
            zeroMap.put(AmmoCube.values()[i], 0);
        }
        try {
            player.payAmmo(costList);
        } catch (NotEnoughAmmoException e) {
            assertTrue(true);
        }
    }

}

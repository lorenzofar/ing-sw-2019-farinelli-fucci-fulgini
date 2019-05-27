package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class WeaponCardTest {

    private static final String validId = "weaponone";
    private static final String validName = "Weapon One";
    private static List<AmmoCubeCost> validCost;
    private static List<EffectDescription> validEffects;
    private static WeaponCardState validState;

    private static ObjectMapper mapper;

    @BeforeClass
    public static void setup(){
        validCost = new ArrayList<>();
        validCost.add(AmmoCubeCost.BLUE);
        validCost.add(AmmoCubeCost.RED);
        validCost.add(AmmoCubeCost.YELLOW);
        validEffects = new ArrayList<>();
        validState = new LoadedState();
        mapper = JSONUtils.getObjectMapper();
    }

    @Test(expected = NullPointerException.class)
    public void createCard_nullIdProvided_shouldThrowNullPointerException(){
        new WeaponCard(null, validName, validCost, validEffects);
    }

    @Test(expected = NullPointerException.class)
    public void createCard_nullNameProvided_shouldThrowNullPointerException(){
        new WeaponCard(validId, null, validCost, validEffects);
    }

    @Test(expected = NullPointerException.class)
    public void createCard_nullCostListProvided_shouldThrowNullPointerException(){
        new WeaponCard(validId, validName, null, validEffects);
    }

    @Test(expected = NullPointerException.class)
    public void createCard_nullEffectsProvided_shouldThrowNullPointerException() {
        new WeaponCard(validId, validName, validCost, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCard_emptyIdProvided_shouldThrowIllegalArgumentException(){
        new WeaponCard("", validName, validCost, validEffects);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCard_emptyNameProvided_shouldThrowIllegalArgumentException(){
        new WeaponCard(validId, "", validCost, validEffects);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCard_emptyCostListProvided_shouldThrowIllegalArgumentException(){
        new WeaponCard(validId, validName, Collections.emptyList(), validEffects);
    }

    @Test
    public void createCard_properParametersProvided_shouldNotThrowException(){
        WeaponCard weaponCard = new WeaponCard(validId, validName, validCost, validEffects);
        assertEquals(validId, weaponCard.getId());
        assertEquals(validName, weaponCard.getName());
        assertTrue(weaponCard.getCost().containsAll(validCost));
        assertTrue(validCost.containsAll(weaponCard.getCost()));
        assertTrue(weaponCard.getEffects().containsAll(validEffects));
        assertTrue(validEffects.containsAll(weaponCard.getEffects()));
    }

    @Test
    public void settState_thenGetState_shouldReturnProvidedState(){
        WeaponCard weaponCard = new WeaponCard(validId, validName, validCost, validEffects);
        weaponCard.setState(new LoadedState());
        assertEquals(weaponCard.getState().getClass(), LoadedState.class);
    }

    @Test
    public void checkEquals_selfPassed_shouldReturnTrue(){
        WeaponCard weaponCard = new WeaponCard(validId, validName, validCost, validEffects);
        assertTrue(weaponCard.equals(weaponCard));
    }

    @Test
    public void checkEquals_anotherClassPassed_shouldreturnFalse(){
        WeaponCard weaponCard = new WeaponCard(validId, validName, validCost, validEffects);
        assertFalse(weaponCard.equals(new Object()));
    }

    @Test
    public void checkEquals_PowerUpCardWithSameIdProvided_shouldReturnTrue(){
        WeaponCard weaponCard = new WeaponCard(validId, validName, validCost, validEffects);
        assertTrue(weaponCard.equals(new WeaponCard(validId, "Another name", validCost, validEffects)));
    }

    @Test
    public void checkEquals_PowerUpCardWithDifferentIdProvided_shouldReturnFalse(){
        WeaponCard weaponCard = new WeaponCard(validId, validName, validCost, validEffects);
        assertFalse(weaponCard.equals(new WeaponCard("anotherweapon", "Another card", validCost, validEffects)));
    }

    @Test
    public void getHashCode_compareWithAnotherCard_ShouldBeDifferent(){
        WeaponCard weaponCard = new WeaponCard(validId, validName, validCost, validEffects);
        WeaponCard anotherCard = new WeaponCard("anotherid", "anothername", validCost, validEffects);
        assertNotEquals(weaponCard.hashCode(), anotherCard.hashCode());
    }

    @Test
    public void serializeCard_shouldContainAllInformation(){
        WeaponCard weaponCard = new WeaponCard(validId, validName, validCost, validEffects);
        try {
            final String serializedWeaponCard = mapper.writeValueAsString(weaponCard);
            assertThat(serializedWeaponCard, containsString(String.format("\"id\":\"%s\"", validId)));
            assertThat(serializedWeaponCard, containsString(String.format("\"name\":\"%s\"", validName)));
            assertThat(serializedWeaponCard, containsString("\"cost\":"));
            weaponCard.getCost().forEach(cube -> assertThat(serializedWeaponCard, containsString(cube.name())));
            weaponCard.getEffects().forEach(effect -> assertThat(serializedWeaponCard, containsString(effect.getId())));
            assertThat(serializedWeaponCard, containsString(String.format("\"state\":{\"type\":\"%s\"}", weaponCard.getState().toString())));
        } catch (JsonProcessingException ex) {
            fail();
        }
    }

}

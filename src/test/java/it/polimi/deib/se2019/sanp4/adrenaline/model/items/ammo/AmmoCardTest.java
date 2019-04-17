package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class AmmoCardTest {
    private static int validId = 1;
    private static boolean powerup = true;
    private static Map<AmmoCube, Integer> validCubes;
    private static Map<AmmoCube, Integer> invalidCubes;

    private static ObjectMapper mapper;

    @BeforeClass
    public static void setup(){
        validCubes = new EnumMap<>(AmmoCube.class);
        invalidCubes = new EnumMap<>(AmmoCube.class);
        validCubes.put(AmmoCube.BLUE, 1);
        validCubes.put(AmmoCube.RED, 1);
        validCubes.put(AmmoCube.YELLOW, 1);

        invalidCubes.put(AmmoCube.BLUE, 1);
        invalidCubes.put(AmmoCube.YELLOW, -1);
        invalidCubes.put(AmmoCube.RED, -1);

        mapper = new ObjectMapper();
    }

    @Test(expected = NullPointerException.class)
    public void createAmmoCard_nullCubesMapProvided_shouldThrowNullPointerException(){
        new AmmoCard(validId, null, powerup);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createAmmoCard_negativeCubesMapProvided_shouldThrowIllegalArgumentException(){
        new AmmoCard(validId, invalidCubes, powerup);
    }

    @Test
    public void createAmmoCard_validCubesMapProvided_shouldNotThrowException(){
        new AmmoCard(validId, validCubes, powerup);
    }

    @Test
    public void getId_shouldReturnProvidedId(){
        AmmoCard validAmmoCard = new AmmoCard(validId, validCubes, powerup);
        assertEquals(validAmmoCard.getId(), validId);
    }

    @Test
    public void getCubes_shouldReturnMapContainingProvidedCubes(){
        AmmoCard validAmmoCard = new AmmoCard(validId, validCubes, powerup);
        Map<AmmoCube, Integer> retrievedCubes = validAmmoCard.getCubes();
        retrievedCubes.forEach((key, value) -> assertEquals(value, validCubes.get(key)));
    }

    @Test
    public void getHoldingPowerup_shouldReturnProvidedBoolean(){
        AmmoCard validAmmoCard = new AmmoCard(validId, validCubes, powerup);
        assertEquals(validAmmoCard.isHoldingPowerup(), powerup);
    }

    @Test
    public void checkEquals_selfPassed_shouldReturnTrue(){
        AmmoCard validAmmoCard = new AmmoCard(validId, validCubes, powerup);
        assertTrue(validAmmoCard.equals(validAmmoCard));
    }

    @Test
    public void checkEquals_anotherClassPassed_shouldreturnFalse(){
        AmmoCard validAmmoCard = new AmmoCard(validId, validCubes, powerup);
        assertFalse(validAmmoCard.equals(new Object()));
    }

    @Test
    public void checkEquals_ammoCardWithSameIdProvided_shouldReturnTrue(){
        AmmoCard validAmmoCard = new AmmoCard(validId, validCubes, powerup);
        assertTrue(validAmmoCard.equals(new AmmoCard(validId, validCubes, powerup)));
    }

    @Test
    public void checkEquals_ammoCardWithDifferentIdProvided_shouldReturnFalse(){
        AmmoCard validAmmoCard = new AmmoCard(validId, validCubes, powerup);
        assertFalse(validAmmoCard.equals(new AmmoCard(validId + 1, validCubes, powerup)));
    }

    @Test
    public void getHashCode_shouldReturnValidHashCode(){
        AmmoCard validAmmoCard = new AmmoCard(validId, validCubes, powerup);
        assertEquals(validAmmoCard.hashCode(), 17 + 31*validId);
    }

    @Test
    public void serializeCard_shouldContainAllInformation(){
        AmmoCard validAmmoCard = new AmmoCard(validId, validCubes, powerup);
        try {
            final String serializedAmmoCard = mapper.writeValueAsString(validAmmoCard);
            assertThat(serializedAmmoCard, containsString(String.format("\"id\":%s", validId)));
            assertThat(serializedAmmoCard, containsString("\"cubes\":"));
            validCubes.forEach((key, value) -> assertThat(serializedAmmoCard, containsString(String.format("\"%s\":%d", key.name(), value))));
            assertThat(serializedAmmoCard, containsString(String.format("\"holdingPowerup\":%s", powerup)));
        } catch (JsonProcessingException e) {
            fail();
        }
    }
}
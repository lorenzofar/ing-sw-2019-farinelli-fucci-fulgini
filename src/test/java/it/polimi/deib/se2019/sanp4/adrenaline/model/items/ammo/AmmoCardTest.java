package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.JSONUtils;
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

        mapper = JSONUtils.getObjectMapper();
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
        AmmoCard ammoCard = new AmmoCard(validId, validCubes, powerup);
        assertEquals(validId, ammoCard.getId());
        assertEquals(validCubes, ammoCard.getCubes());
        assertEquals(powerup, ammoCard.isHoldingPowerup());
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
    public void getHashCode_compareWithAnotherCard_shouldBeDifferent(){
        AmmoCard validAmmoCard = new AmmoCard(validId, validCubes, powerup);
        AmmoCard anotherCard = new AmmoCard(5, validCubes, false);
        assertNotEquals(anotherCard.hashCode(), validAmmoCard.hashCode());
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
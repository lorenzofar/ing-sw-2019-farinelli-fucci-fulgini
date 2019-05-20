package it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class PowerupCardTest {

    private static PowerupEnum validType = PowerupEnum.NEWTON;
    private static AmmoCube validColor = AmmoCube.BLUE;

    private static ObjectMapper mapper;

    @BeforeClass
    public static void setup(){
        mapper = JSONUtils.getObjectMapper();
    }


    @Test(expected = NullPointerException.class)
    public void createPowerUpCard_nullParametersProvided_shouldThrowNullPointerException(){
        new PowerupCard(null, null);
    }
    @Test(expected = NullPointerException.class)
    public void createPowerUpCard_nullTypeProvided_shouldThrowNullPointerException(){
        new PowerupCard(null, validColor);
    }
    @Test(expected = NullPointerException.class)
    public void createPowerUpCard_nullColorProvided_shouldThrowNullPointerException(){
        new PowerupCard(validType, null);
    }

    @Test
    public void createPowerUpCard_validParametersProvided_shouldNotThrowException(){
        PowerupCard powerUpCard = new PowerupCard(validType, validColor);
        assertEquals(validType, powerUpCard.getType());
        assertEquals(validColor, powerUpCard.getCubeColor());
        assertEquals(validType.getName(), powerUpCard.getName());
        assertEquals(validType.getDescription(), powerUpCard.getDescription());
    }

    @Test
    public void checkEquals_selfPassed_shouldReturnTrue(){
        PowerupCard validPowerUpCard = new PowerupCard(validType, validColor);
        assertEquals(validPowerUpCard, validPowerUpCard);
    }

    @Test
    public void checkEquals_anotherClassPassed_shouldreturnFalse(){
        PowerupCard validPowerUpCard = new PowerupCard(validType, validColor);
        assertNotEquals(validPowerUpCard, new Object());
    }

    @Test
    public void checkEquals_PowerUpCardWithSameTypeAndColorProvided_shouldReturnTrue(){
        PowerupCard validPowerUpCard = new PowerupCard(validType, validColor);
        assertEquals(validPowerUpCard, new PowerupCard(validType, validColor));
    }

    @Test
    public void checkEquals_PowerUpCardWithSameIdAndDifferentColorProvided_shouldReturnFalse(){
        PowerupCard validPowerUpCard = new PowerupCard(validType, validColor);
        assertNotEquals(validPowerUpCard, new PowerupCard(validType, AmmoCube.YELLOW));
    }

    @Test
    public void checkEquals_PowerUpCardWithDifferentIdAndSameColorProvided_shouldReturnFalse(){
        PowerupCard validPowerUpCard = new PowerupCard(validType, validColor);
        assertNotEquals(validPowerUpCard, new PowerupCard(PowerupEnum.TARGETING_SCOPE, validColor));
    }

    @Test
    public void checkEquals_PowerUpCardWithDifferentIdAndColorProvided_shouldReturnFalse(){
        PowerupCard validPowerUpCard = new PowerupCard(validType, validColor);
        assertNotEquals(validPowerUpCard, new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.YELLOW));
    }

    @Test
    public void getHashCode_compareWithAnotherCard_ShouldBeDifferent(){
        PowerupCard validPowerUpCard = new PowerupCard(validType, validColor);
        PowerupCard anotherCard = new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.YELLOW);
        assertNotEquals(validPowerUpCard.hashCode(), anotherCard.hashCode());
    }

    @Test
    public void serializeCard_shouldContainAllInformation(){
        PowerupCard validPowerUpCard = new PowerupCard(validType, validColor);
        try {
            final String serializedPowerUpCard = mapper.writeValueAsString(validPowerUpCard);
            assertThat(serializedPowerUpCard, containsString(String.format("\"type\":\"%s\"", validType.name())));
            assertThat(serializedPowerUpCard, containsString(String.format("\"cubeColor\":\"%s\"", validColor.name())));
        } catch (JsonProcessingException e) {
            fail();
        }
    }

}

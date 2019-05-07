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

    private static String validId = "powerupOne";
    private static String validName = "Powerup One";
    private static String validDescription = "Description of Powerup One";
    private static AmmoCube validColor = AmmoCube.BLUE;

    private static ObjectMapper mapper;

    @BeforeClass
    public static void setup(){
        mapper = JSONUtils.getObjectMapper();
    }


    @Test(expected = NullPointerException.class)
    public void createPowerUpCard_nullParametersProvided_shouldThrowNullPointerException(){
        new PowerUpCard(null, null, null, null);
    }
    @Test(expected = NullPointerException.class)
    public void createPowerUpCard_nullIdProvided_shouldThrowNullPointerException(){
        new PowerUpCard(null, validName, validDescription, validColor);
    }
    @Test(expected = NullPointerException.class)
    public void createPowerUpCard_nullNameProvided_shouldThrowNullPointerException(){
        new PowerUpCard(validId, null, validDescription, validColor);
    }
    @Test(expected = NullPointerException.class)
    public void createPowerUpCard_nullDescriptionProvided_shouldThrowNullPointerException(){
        new PowerUpCard(validId, validName, null, validColor);
    }
    @Test(expected = NullPointerException.class)
    public void createPowerUpCard_nullColorProvided_shouldThrowNullPointerException(){
        new PowerUpCard(validId, validName, validDescription, null);
    }

    @Test
    public void createPowerUpCard_validParametersProvided_shouldNotThrowException(){
        PowerUpCard powerUpCard = new PowerUpCard(validId, validName, validDescription, validColor);
        assertEquals(validId, powerUpCard.getId());
        assertEquals(validDescription, powerUpCard.getDescription());
        assertEquals(validName, powerUpCard.getName());
        assertEquals(validColor, powerUpCard.getCubeColor());
    }

    @Test
    public void checkEquals_selfPassed_shouldReturnTrue(){
        PowerUpCard validPowerUpCard = new PowerUpCard(validId, validName, validDescription, validColor);
        assertTrue(validPowerUpCard.equals(validPowerUpCard));
    }

    @Test
    public void checkEquals_anotherClassPassed_shouldreturnFalse(){
        PowerUpCard validPowerUpCard = new PowerUpCard(validId, validName, validDescription, validColor);
        assertFalse(validPowerUpCard.equals(new Object()));
    }

    @Test
    public void checkEquals_PowerUpCardWithSameIdAndColorProvided_shouldReturnTrue(){
        PowerUpCard validPowerUpCard = new PowerUpCard(validId, validName, validDescription, validColor);
        assertTrue(validPowerUpCard.equals(new PowerUpCard(validId, "Another card", "Another description", validColor)));
    }

    @Test
    public void checkEquals_PowerUpCardWithSameIdAndDifferentColorProvided_shouldReturnFalse(){
        PowerUpCard validPowerUpCard = new PowerUpCard(validId, validName, validDescription, validColor);
        assertFalse(validPowerUpCard.equals(new PowerUpCard(validId, "Another card", "Another description", AmmoCube.YELLOW)));
    }

    @Test
    public void checkEquals_PowerUpCardWithDifferentIdAndSameColorProvided_shouldReturnFalse(){
        PowerUpCard validPowerUpCard = new PowerUpCard(validId, validName, validDescription, validColor);
        assertFalse(validPowerUpCard.equals(new PowerUpCard("anotherid", "Another card", "Another description", validColor)));
    }

    @Test
    public void checkEquals_PowerUpCardWithDifferentIdAndColorProvided_shouldReturnFalse(){
        PowerUpCard validPowerUpCard = new PowerUpCard(validId, validName, validDescription, validColor);
        assertFalse(validPowerUpCard.equals(new PowerUpCard("anotherid", validName, validDescription, AmmoCube.YELLOW)));
    }

    @Test
    public void getHashCode_compareWithAnotherCard_ShouldBeDifferent(){
        PowerUpCard validPowerUpCard = new PowerUpCard(validId, validName, validDescription, validColor);
        PowerUpCard anotherCard = new PowerUpCard("anotherid", "anothername", "anotherdescription", AmmoCube.YELLOW);
        assertNotEquals(validPowerUpCard.hashCode(), anotherCard.hashCode());
    }

    @Test
    public void serializeCard_shouldContainAllInformation(){
        PowerUpCard validPowerUpCard = new PowerUpCard(validId, validName, validDescription, validColor);
        try {
            final String serializedPowerUpCard = mapper.writeValueAsString(validPowerUpCard);
            assertThat(serializedPowerUpCard, containsString(String.format("\"id\":\"%s\"", validId)));
            assertThat(serializedPowerUpCard, containsString(String.format("\"name\":\"%s\"", validName)));
            assertThat(serializedPowerUpCard, containsString(String.format("\"description\":\"%s\"", validDescription)));
            assertThat(serializedPowerUpCard, containsString(String.format("\"cubeColor\":\"%s\"", validColor.name())));
        } catch (JsonProcessingException e) {
            fail();
        }
    }

}

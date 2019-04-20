package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class EffectDescriptionTest {

    private static String validId = "effect1";
    private static String validName = "effect one";
    private static String validDescription = "Effect description";
    private static List<AmmoCubeCost> validCost;
    private static List<AmmoCubeCost> invalidCost;

    private static ObjectMapper mapper;

    @BeforeClass
    public static void setup() {
        validCost = new ArrayList<>();
        invalidCost = new ArrayList<>();
        validCost.add(AmmoCubeCost.BLUE);
        validCost.add(AmmoCubeCost.YELLOW);
        invalidCost.add(null);

        mapper = new ObjectMapper();
    }

    @Test(expected = NullPointerException.class)
    public void createEffect_nullIdProvided_shouldThrowNullPointerException() {
        new EffectDescription(null, validName, validDescription, validCost);
    }

    @Test(expected = NullPointerException.class)
    public void createEffect_nullNameProvided_shouldThrowNullPointerException() {
        new EffectDescription(validId, null, validDescription, validCost);
    }

    @Test(expected = NullPointerException.class)
    public void createEffect_nullDescriptionProvided_shouldThrowNullPointerException() {
        new EffectDescription(validId, validName, null, validCost);
    }

    @Test(expected = NullPointerException.class)
    public void createEffect_nullCostProvided_shouldThrowNullPointerException() {
        new EffectDescription(validId, validName, validDescription, null);
    }

    @Test(expected = NullPointerException.class)
    public void createEffect_costWithNullObjectsProvided_shouldThrowNullPointerException() {
        new EffectDescription(validId, validName, validDescription, invalidCost);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEffect_emptyIdProvided_shouldThrowIllegalArgumentException() {
        new EffectDescription("", validName, validDescription, validCost);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEffect_emptyNameProvided_shouldThrowIllegalArgumentException() {
        new EffectDescription(validId, "", validDescription, validCost);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEffect_emptyDescriptionProvided_shouldThrowIllegalArgumentException() {
        new EffectDescription(validId, validName, "", validCost);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEffect_emptyCostListProvided_shouldThrowIllegalArgumentException(){
        new EffectDescription(validId, validName, validDescription, emptyList());
    }

    @Test
    public void createEffect_properParametersProvided_shouldNotThrowException(){
        EffectDescription validEffect = new EffectDescription(validId, validName, validDescription, validCost);
        assertEquals(validId, validEffect.getId());
        assertEquals(validName, validEffect.getName());
        assertEquals(validDescription, validEffect.getDescription());
        assertTrue(validCost.containsAll(validEffect.getCost()));
        assertTrue(validEffect.getCost().containsAll(validCost));
    }


    @Test
    public void checkEquals_selfPassed_shouldReturnTrue(){
        EffectDescription validEffect = new EffectDescription(validId, validName, validDescription, validCost);
        assertTrue(validEffect.equals(validEffect));
    }

    @Test
    public void checkEquals_anotherClassPassed_shouldreturnFalse(){
        EffectDescription validEffect = new EffectDescription(validId, validName, validDescription, validCost);
        assertFalse(validEffect.equals(new Object()));
    }

    @Test
    public void checkEquals_PowerUpCardWithSameIdProvided_shouldReturnTrue(){
        EffectDescription validEffect = new EffectDescription(validId, validName, validDescription, validCost);
        assertTrue(validEffect.equals(new EffectDescription(validId, "Another effect name", "Another description", validCost)));
    }

    @Test
    public void checkEquals_PowerUpCardWithDifferentIdProvided_shouldReturnFalse(){
        EffectDescription validEffect = new EffectDescription(validId, validName, validDescription, validCost);
        assertFalse(validEffect.equals(new EffectDescription("anothereffect", "Another Effect", "Another effect description", validCost)));
    }

    @Test
    public void getHashCode_compareWithAnotherCard_ShouldBeDifferent(){
        EffectDescription validEffect = new EffectDescription(validId, validName, validDescription, validCost);
        EffectDescription anotherEffect= new EffectDescription("anothereffect", "Another", "Another description", validCost);
        assertNotEquals(validEffect.hashCode(), anotherEffect.hashCode());
    }

    @Test
    public void serializeCard_shouldContainAllInformation(){
        EffectDescription validEffect = new EffectDescription(validId, validName, validDescription, validCost);
        try {
            final String serializedEffect = mapper.writeValueAsString(validEffect);
            assertThat(serializedEffect, containsString(String.format("\"id\":\"%s\"", validId)));
            assertThat(serializedEffect, containsString(String.format("\"name\":\"%s\"", validName)));
            assertThat(serializedEffect, containsString(String.format("\"description\":\"%s\"", validDescription)));
            assertThat(serializedEffect, containsString("\"cost\":"));
            validEffect.getCost().forEach(cube -> assertThat(serializedEffect, containsString(cube.name())));
        } catch (IOException e) {
            fail();
        }
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ChoiceRequestTest {

    private static String validMessage = "message";
    private static List<Object> validChoices;

    @BeforeClass
    public static void setup(){
        validChoices = new ArrayList<>();
        validChoices.add(new Object());
    }

    @Test(expected = NullPointerException.class)
    public void createRequest_nullMessageProvided_shouldThrowNullPointerException(){
        new ChoiceRequest<Object>(null, validChoices, true, Object.class) {};
    }

    @Test(expected = NullPointerException.class)
    public void createRequest_nullChoicesProvided_shouldThrowNullPointerException(){
        new ChoiceRequest<Object>(validMessage, null, true, Object.class) {};
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRequest_emptyMessageProvided_shouldThrowIllegalArgumentException(){
        new ChoiceRequest<Object>("", validChoices, true, Object.class) {};
    }

    @Test
    public void createRequest_properParametersProvided_shouldNotThrowException(){
        ChoiceRequest<Object> request =
                new ChoiceRequest<Object>(validMessage, validChoices, true, Object.class) {};
        assertEquals(validMessage, request.getMessage());
        assertTrue(validChoices.containsAll(request.getChoices()));
        assertTrue(request.getChoices().containsAll(validChoices));
        assertTrue(request.isOptional());
    }

    @Test
    public void createRequest_uuidProvided_shouldBeSet() {
        ChoiceRequest<Object> request =
                new ChoiceRequest<Object>(validMessage, validChoices, true, Object.class, "bzoto") {};
        assertEquals("bzoto", request.getUuid());
    }

    @Test
    public void createRequest_noUuidProvided_shouldBeGenerated() {
        /* Use the constructor with no uuid argument */
        ChoiceRequest<Object> request =
                new ChoiceRequest<Object>(validMessage, validChoices, true, Object.class) {};
        /* Check that the uuid has been generated */
        assertNotNull(request.getUuid());
        assertFalse(request.getUuid().isEmpty());
    }

    @Test
    public void createRequest_nullUuidProvided_shouldBeGenerated() {
        ChoiceRequest<Object> request =
                new ChoiceRequest<Object>(validMessage, validChoices, true, Object.class, null) {};
        /* Check that the uuid has been generated */
        assertNotNull(request.getUuid());
        assertFalse(request.getUuid().isEmpty());
    }

    @Test
    public void createRequest_emptyUuidProvided_shouldBeGenerated() {
        ChoiceRequest<Object> request =
                new ChoiceRequest<Object>(validMessage, validChoices, true, Object.class, "") {};
        /* Check that the uuid has been generated */
        assertNotNull(request.getUuid());
        assertFalse(request.getUuid().isEmpty());
    }

    @Test
    public void isChoiceValid_validChoiceProvided_shouldReturnTrue(){
        ChoiceRequest<Object> request =
                new ChoiceRequest<Object>(validMessage, validChoices, true, Object.class) {};
        assertTrue(request.isChoiceValid(validChoices.get((0))));
    }

    @Test
    public void isChoiceValid_inexistentChoiceProvided_shouldReturnFalse(){
        ChoiceRequest<Object> request =
                new ChoiceRequest<Object>(validMessage, validChoices, true, Object.class) {};
        assertFalse(request.isChoiceValid(new Object()));
    }

    @Test
    public void isChoiceValid_optional_nullChoiceProvided_shouldReturnTrue() {
        ChoiceRequest<Object> request =
                new ChoiceRequest<Object>(validMessage, validChoices, true, Object.class) {};
        assertTrue(request.isChoiceValid(null));
    }

    @Test
    public void isChoiceValid_nonOptional_nullChoiceProvided_shouldReturnFalse() {
        ChoiceRequest<Object> request =
                new ChoiceRequest<Object>(validMessage, validChoices, false, Object.class) {};
        assertFalse(request.isChoiceValid(null));
    }
}
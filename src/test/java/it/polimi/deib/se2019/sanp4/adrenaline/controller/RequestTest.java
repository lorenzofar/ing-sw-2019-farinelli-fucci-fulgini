package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RequestTest {

    private static String validMessage = "message";
    private static List<Object> validChoices;

    @BeforeClass
    public static void setup(){
        validChoices = new ArrayList<>();
        validChoices.add(new Object());
    }

    @Test(expected = NullPointerException.class)
    public void createRequest_nullMessageProvided_shouldThrowNullPointerException(){
        Request<Object> request = new Request<Object>(null, validChoices, true) {};
    }

    @Test(expected = NullPointerException.class)
    public void createRequest_nullChoicesProvided_shouldThrowNullPointerException(){
        Request<Object> request = new Request<Object>(validMessage, null, true) {};
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRequest_emptyMessageProvided_shouldThrowIllegalArgumentException(){
        Request<Object> request = new Request<Object>("", validChoices, true) {};
    }

    @Test(expected = NullPointerException.class)
    public void createRequest_choicesWithNullValuesProvided_shouldThrowNullPointerException(){
        List<Object> invalidChoices = new ArrayList<>();
        invalidChoices.add(null);
        Request<Object> request = new Request<Object>(validMessage, invalidChoices, true) {};
    }

    @Test
    public void createRequest_properParametersProvided_shouldNotThrowException(){
        Request<Object> request = new Request<Object>(validMessage, validChoices, true) {};
        assertEquals(validMessage, request.getMessage());
        assertTrue(validChoices.containsAll(request.getChoices()));
        assertTrue(request.getChoices().containsAll(validChoices));
        assertTrue(request.isOptional());
    }

    @Test(expected = NullPointerException.class)
    public void checkChoiceValid_nullChoiceProvided_shouldThrowNullPointerException(){
        Request<Object> request = new Request<Object>(validMessage, validChoices, true){};
        request.isValid(null);
    }

    @Test
    public void checkChoiceValid_validChoiceProvided_shouldReturnTrue(){
        Request<Object> request = new Request<Object>(validMessage, validChoices, true) {};
        assertTrue(request.isValid(validChoices.get((0))));
    }

    @Test
    public void checkChoiceValid_invalidChoiceProvided_shouldReturnFalse(){
        Request<Object> request = new Request<Object>(validMessage, validChoices, true) {};
        assertFalse(request.isValid(new Object()));
    }
}
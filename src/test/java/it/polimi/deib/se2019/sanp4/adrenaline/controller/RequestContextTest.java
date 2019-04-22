package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class RequestContextTest {

    private static ChoiceHandler<Object> validHandler;
    private static Request<Object> validRequest;

    @BeforeClass
    public static void setup(){
        validHandler = new ChoiceHandler<Object>() {
            @Override
            public void handleChoice(Object choice) {}

            @Override
            public void cancel() {}
        };
        validRequest = new Request<Object>("message", new ArrayList<>(), true) {
            @Override
            public boolean isValid(Object choice) {
                return true;
            }
        };
    }

    @Test(expected = NullPointerException.class)
    public void createContext_nullRequestProvided_shouldThrowNullPointerException(){
        new RequestContext<>(null, validHandler);
    }

    @Test(expected = NullPointerException.class)
    public void createContext_nullHandlerProvided_shouldThrowNullPointerException(){
        new RequestContext<>(validRequest, null);
    }

    @Test
    public void createContext_properParametersProvided_shouldNotThrowException(){
        RequestContext<Object> context = new RequestContext<>(validRequest, validHandler);
        assertEquals(validRequest, context.getRequest());
        assertEquals(validHandler, context.getHandler());
    }
}

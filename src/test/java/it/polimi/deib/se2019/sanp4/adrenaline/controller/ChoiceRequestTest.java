package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ChoiceRequestTest {

    private static String validMessage = "message";
    private static List<CoordPair> validChoices;

    @BeforeClass
    public static void setup(){
        validChoices = new ArrayList<>();
        validChoices.add(new CoordPair(0, 0));
    }

    @Test(expected = NullPointerException.class)
    public void createRequest_nullMessageProvided_shouldThrowNullPointerException(){
        new ChoiceRequest<CoordPair>(null, validChoices, true, CoordPair.class) {
            private static final long serialVersionUID = 5232365475286656472L;
        };
    }

    @Test(expected = NullPointerException.class)
    public void createRequest_nullChoicesProvided_shouldThrowNullPointerException(){
        new ChoiceRequest<CoordPair>(validMessage, null, true, CoordPair.class) {
            private static final long serialVersionUID = 5232365475286656472L;
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRequest_emptyMessageProvided_shouldThrowIllegalArgumentException(){
        new ChoiceRequest<CoordPair>("", validChoices, true, CoordPair.class) {
            private static final long serialVersionUID = 5232365475286656472L;
        };
    }

    @Test
    public void createRequest_properParametersProvided_shouldNotThrowException(){
        ChoiceRequest<CoordPair> request =
                new ChoiceRequest<CoordPair>(validMessage, validChoices, true, CoordPair.class) {
                    private static final long serialVersionUID = 5232365475286656472L;
                };
        assertEquals(validMessage, request.getMessage());
        assertTrue(validChoices.containsAll(request.getChoices()));
        assertTrue(request.getChoices().containsAll(validChoices));
        assertTrue(request.isOptional());
    }

    @Test
    public void createRequest_uuidProvided_shouldBeSet() {
        ChoiceRequest<CoordPair> request =
                new ChoiceRequest<CoordPair>(validMessage, validChoices, true, CoordPair.class, "bzoto") {
                    private static final long serialVersionUID = 5232365475286656472L;
                };
        assertEquals("bzoto", request.getUuid());
    }

    @Test
    public void createRequest_noUuidProvided_shouldBeGenerated() {
        /* Use the constructor with no uuid argument */
        ChoiceRequest<CoordPair> request =
                new ChoiceRequest<CoordPair>(validMessage, validChoices, true, CoordPair.class) {
                    private static final long serialVersionUID = 5232365475286656472L;
                };
        /* Check that the uuid has been generated */
        assertNotNull(request.getUuid());
        assertFalse(request.getUuid().isEmpty());
    }

    @Test
    public void createRequest_nullUuidProvided_shouldBeGenerated() {
        ChoiceRequest<CoordPair> request =
                new ChoiceRequest<CoordPair>(validMessage, validChoices, true, CoordPair.class, null) {
                    private static final long serialVersionUID = 5232365475286656472L;
                };
        /* Check that the uuid has been generated */
        assertNotNull(request.getUuid());
        assertFalse(request.getUuid().isEmpty());
    }

    @Test
    public void createRequest_emptyUuidProvided_shouldBeGenerated() {
        ChoiceRequest<CoordPair> request =
                new ChoiceRequest<CoordPair>(validMessage, validChoices, true, CoordPair.class, "") {
                    private static final long serialVersionUID = 5232365475286656472L;
                };
        /* Check that the uuid has been generated */
        assertNotNull(request.getUuid());
        assertFalse(request.getUuid().isEmpty());
    }

    @Test
    public void isChoiceValid_validChoiceProvided_shouldReturnTrue(){
        ChoiceRequest<CoordPair> request =
                new ChoiceRequest<CoordPair>(validMessage, validChoices, true, CoordPair.class) {
                    private static final long serialVersionUID = 5232365475286656472L;
                };
        assertTrue(request.isChoiceValid(validChoices.get((0))));
    }

    @Test
    public void isChoiceValid_inexistentChoiceProvided_shouldReturnFalse(){
        ChoiceRequest<CoordPair> request =
                new ChoiceRequest<CoordPair>(validMessage, validChoices, true, CoordPair.class) {
                    private static final long serialVersionUID = 5232365475286656472L;
                };
        assertFalse(request.isChoiceValid(new CoordPair(1,1)));
    }

    @Test
    public void isChoiceValid_optional_nullChoiceProvided_shouldReturnTrue() {
        ChoiceRequest<CoordPair> request =
                new ChoiceRequest<CoordPair>(validMessage, validChoices, true, CoordPair.class) {
                    private static final long serialVersionUID = 5232365475286656472L;
                };
        assertTrue(request.isChoiceValid(null));
    }

    @Test
    public void isChoiceValid_nonOptional_nullChoiceProvided_shouldReturnFalse() {
        ChoiceRequest<CoordPair> request =
                new ChoiceRequest<CoordPair>(validMessage, validChoices, false, CoordPair.class) {
                    private static final long serialVersionUID = 5232365475286656472L;
                };
        assertFalse(request.isChoiceValid(null));
    }
}
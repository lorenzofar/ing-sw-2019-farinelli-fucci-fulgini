package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerOperationEnumTest {

    @Test
    public void toString_message_shouldBeSame() {
        PlayerOperationEnum op = PlayerOperationEnum.PERFORM_ACTION;

        assertNotNull(op.getMessage());
        assertEquals(op.getMessage(), op.toString());
    }
}
package it.polimi.deib.se2019.sanp4.adrenaline.common.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChoiceResponseTest {

    @Mock
    private static ViewEventVisitor visitor;

    private static String sender = "sender";

    private static String uuid = "uuid";

    private static String choice = "choice";

    @Test(expected = NullPointerException.class)
    public void create_nullSender_shouldThrow() {
        new ChoiceResponse<>(null, uuid, choice);
    }

    @Test(expected = NullPointerException.class)
    public void create_NullId_shouldThrow() {
        new ChoiceResponse<>(sender, null, choice);
    }

    @Test
    public void create_validParams_shouldCreate() {
        ChoiceResponse<String> res = new ChoiceResponse<>(sender, uuid, choice);
        assertEquals(sender, res.getSender());
        assertEquals(uuid, res.getUuid());
        assertEquals(choice, res.getChoice());
    }

    @Test
    public void accept_visitor_shouldBeAccepted() {
        ChoiceResponse<String> res = new ChoiceResponse<>(sender, uuid, choice);
        res.accept(visitor);
        verify(visitor).visit(res);
    }
}
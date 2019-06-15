package it.polimi.deib.se2019.sanp4.adrenaline.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChoiceResponseTest {

    @Mock
    private static ViewEventVisitor visitor;

    private static String sender = "sender";

    private static String uuid = "uuid";

    private static String choice = "choice";

    private static ObjectMapper mapper = new ObjectMapper();

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

    @Test
    public void serialize_shouldSucceeed() {
        ChoiceResponse<String> response = new ChoiceResponse<>(sender, uuid, choice);
        try {
            String serializedRes = mapper.writeValueAsString(response);
            System.out.println(serializedRes);
            ChoiceResponse deserializedResponse = mapper.readValue(serializedRes, ChoiceResponse.class);
            assertEquals(sender, deserializedResponse.getSender());
            assertEquals(uuid, deserializedResponse.getUuid());
            assertEquals(choice, deserializedResponse.getChoice());
        } catch (IOException e) {
            fail();
        }
    }
}
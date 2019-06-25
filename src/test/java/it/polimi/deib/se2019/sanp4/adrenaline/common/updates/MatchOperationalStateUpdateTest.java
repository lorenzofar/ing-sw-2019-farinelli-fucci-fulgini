package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.model.MatchOperationalState;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class MatchOperationalStateUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();
    private MatchOperationalState state;

    @Test
    public void serialize_shouldSucceed() throws IOException {
        state = MatchOperationalState.ACTIVE;
        MatchOperationalStateUpdate update = new MatchOperationalStateUpdate(state);

        String s = objectMapper.writeValueAsString(update);
        MatchOperationalStateUpdate stateUpdate = objectMapper.readValue(s, MatchOperationalStateUpdate.class);

        assertEquals(state, update.getState());
    }

    @Test
    public void getState_setState_shouldSucceed() {
        state = MatchOperationalState.ACTIVE;
        MatchOperationalStateUpdate update = new MatchOperationalStateUpdate(state);

        update.setState(MatchOperationalState.FINISHED);
        assertEquals(MatchOperationalState.FINISHED, update.getState());
    }
}
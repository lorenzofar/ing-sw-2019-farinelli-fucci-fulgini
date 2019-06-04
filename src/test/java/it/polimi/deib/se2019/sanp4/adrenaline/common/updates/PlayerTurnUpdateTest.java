package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerTurnView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurnState;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PlayerTurnUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();
    private PlayerTurnView playerTurnView;
    private String player = "Renato";

    @Test
    public void serialize_ShouldSucceed() throws IOException {
        playerTurnView = new PlayerTurnView(player);

        PlayerTurnUpdate update = new PlayerTurnUpdate(playerTurnView);
        String s = objectMapper.writeValueAsString(update);

        PlayerTurnUpdate playerTurnUpdate = objectMapper.readValue(s, PlayerTurnUpdate.class);

        assertEquals(playerTurnView.getPlayer(), playerTurnUpdate.getPlayerTurn().getPlayer());
        assertEquals(playerTurnView.getState(), playerTurnUpdate.getPlayerTurn().getState());
    }
}
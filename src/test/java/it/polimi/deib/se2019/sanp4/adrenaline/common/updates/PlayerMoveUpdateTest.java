package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PlayerMoveUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();

    private CoordPair start = new CoordPair(2,2);
    private CoordPair end = new CoordPair(1,2);
    private String player = "bzoto";

    @Test
    public void serialize_ShouldSucceed() throws IOException {
        PlayerMoveUpdate update = new PlayerMoveUpdate(player, start, end);
        String s = objectMapper.writeValueAsString(update);
        PlayerMoveUpdate playerMoveUpdate = objectMapper.readValue(s, PlayerMoveUpdate.class);
        assertEquals(start, playerMoveUpdate.getStart());
        assertEquals(end, playerMoveUpdate.getEnd());
    }

    @Test
    public void setPlayer_ShouldSucceed() {
        PlayerMoveUpdate update = new PlayerMoveUpdate(player, start, end);
        String player = "Akiller";
        update.setPlayer(player);
        assertEquals(player, update.getPlayer());
    }

    @Test
    public void setStart_ShouldSucceed() {
        PlayerMoveUpdate update = new PlayerMoveUpdate(player, start, end);
        CoordPair start = new CoordPair(0,4);
        update.setStart(start);
        assertEquals(start, update.getStart());
    }

    @Test
    public void setEnd_ShouldSucceed() {
        PlayerMoveUpdate update = new PlayerMoveUpdate(player, start, end);
        CoordPair end = new CoordPair(3,0);
        update.setEnd(end);
        assertEquals(end, update.getEnd());
    }
}
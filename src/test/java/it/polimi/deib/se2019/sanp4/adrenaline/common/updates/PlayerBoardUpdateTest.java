package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerBoardView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.JSONUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PlayerBoardUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();
    private PlayerBoardView playerBoard;
    private List<String> damages = new LinkedList<>();
    private String player = "player";
    private int deaths = 2;
    private int marks = 1;

    @Test
    public void serialize_ShouldSucceed() throws IOException {
        playerBoard = new PlayerBoardView();

        PlayerBoardUpdate update = new PlayerBoardUpdate(playerBoard, player);
        String s = objectMapper.writeValueAsString(update);
        PlayerBoardUpdate playerBoardUpdate = objectMapper.readValue(s, PlayerBoardUpdate.class);

        assertEquals(player, playerBoardUpdate.getPlayer());
        assertEquals(0, playerBoardUpdate.getPlayerBoard().getDamages().size());
        assertEquals(0, playerBoardUpdate.getPlayerBoard().getMarks());
        assertEquals(0, playerBoardUpdate.getPlayerBoard().getDeaths());

        /* Test setters */
        playerBoard.setDamages(damages);
        playerBoard.setDeaths(deaths);
        playerBoard.setMarks(marks);
        update = new PlayerBoardUpdate(playerBoard, player);
        s = objectMapper.writeValueAsString(update);
        playerBoardUpdate = objectMapper.readValue(s, PlayerBoardUpdate.class);

        assertEquals(player, playerBoardUpdate.getPlayer());
        assertEquals(damages, playerBoardUpdate.getPlayerBoard().getDamages());
        assertEquals(damages, playerBoardUpdate.getPlayerBoard().getDamages());
        assertEquals(deaths, playerBoardUpdate.getPlayerBoard().getDeaths());
    }
}
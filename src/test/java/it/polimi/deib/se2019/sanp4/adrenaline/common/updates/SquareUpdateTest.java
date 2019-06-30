package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.AmmoSquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.SpawnSquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CardinalDirection;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.SquareConnectionType;
import it.polimi.deib.se2019.sanp4.adrenaline.common.JSONUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class SquareUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();

    private AmmoSquareView ammoSquareView;
    private CoordPair location = new CoordPair(2,3);
    private Set<String> players = new HashSet<>();
    private RoomColor roomColor = RoomColor.BLUE;
    private Map adjacentMap = new HashMap<>();

    @Test
    public void ammoSquareView_Serialize_ShouldSucceed() throws IOException {
        ammoSquareView = new AmmoSquareView(location, roomColor);

        /* Test setters */
        players.add("Lorenzo");
        players.add("Alessandro");
        ammoSquareView.setPlayers(players);
        adjacentMap.put(CardinalDirection.N, SquareConnectionType.DOOR);
        adjacentMap.put(CardinalDirection.S, SquareConnectionType.DOOR);
        adjacentMap.put(CardinalDirection.W, SquareConnectionType.WALL);
        adjacentMap.put(CardinalDirection.E, SquareConnectionType.FLOOR);
        ammoSquareView.setAdjacentMap(adjacentMap);

        /* Create update and serialize */
        SquareUpdate update = new SquareUpdate(ammoSquareView);
        String s = objectMapper.writeValueAsString(update);

        SquareUpdate ammoSquareUpdate = objectMapper.readValue(s, SquareUpdate.class);

        assertEquals(players, ammoSquareUpdate.getSquare().getPlayers());
        assertEquals(roomColor, ammoSquareUpdate.getSquare().getRoomColor());
        assertEquals(adjacentMap, ammoSquareUpdate.getSquare().getAdjacentMap());

        /* Test addPlayer and removePlayer */
        players.add("Tiziano");
        ammoSquareView.addPlayer("Tiziano");
        update = new SquareUpdate(ammoSquareView);
        s = objectMapper.writeValueAsString(update);
        ammoSquareUpdate = objectMapper.readValue(s, SquareUpdate.class);

        assertEquals(players, ammoSquareUpdate.getSquare().getPlayers());

        players.remove("Tiziano");
        ammoSquareView.removePlayer("Tiziano");
        update = new SquareUpdate(ammoSquareView);
        s = objectMapper.writeValueAsString(update);
        ammoSquareUpdate = objectMapper.readValue(s, SquareUpdate.class);

        assertEquals(players, ammoSquareUpdate.getSquare().getPlayers());

    }

    @Test
    public void spawnSquareView_Serialize_ShouldSucceed() throws IOException {
        SpawnSquareView spawnSquareView = new SpawnSquareView(location, roomColor);

        /* Test setters */
        players.add("Lorenzo");
        players.add("Alessandro");
        spawnSquareView.setPlayers(players);
        adjacentMap.put(CardinalDirection.N, SquareConnectionType.DOOR);
        adjacentMap.put(CardinalDirection.S, SquareConnectionType.DOOR);
        adjacentMap.put(CardinalDirection.W, SquareConnectionType.WALL);
        adjacentMap.put(CardinalDirection.E, SquareConnectionType.FLOOR);
        spawnSquareView.setAdjacentMap(adjacentMap);

        /* Create update and serialize */
        SquareUpdate update = new SquareUpdate(spawnSquareView);
        String s = objectMapper.writeValueAsString(update);

        SquareUpdate spawnSquareUpdate = objectMapper.readValue(s, SquareUpdate.class);

        assertEquals(players, spawnSquareUpdate.getSquare().getPlayers());
        assertEquals(roomColor, spawnSquareUpdate.getSquare().getRoomColor());
        assertEquals(adjacentMap, spawnSquareUpdate.getSquare().getAdjacentMap());

        /* Test addPlayer and removePlayer */
        players.add("Tiziano");
        spawnSquareView.addPlayer("Tiziano");
        update = new SquareUpdate(spawnSquareView);
        s = objectMapper.writeValueAsString(update);
        spawnSquareUpdate = objectMapper.readValue(s, SquareUpdate.class);

        assertEquals(players, spawnSquareUpdate.getSquare().getPlayers());

        players.remove("Tiziano");
        spawnSquareView.removePlayer("Tiziano");
        update = new SquareUpdate(spawnSquareView);
        s = objectMapper.writeValueAsString(update);
        spawnSquareUpdate = objectMapper.readValue(s, SquareUpdate.class);

        assertEquals(players, spawnSquareUpdate.getSquare().getPlayers());

    }
}
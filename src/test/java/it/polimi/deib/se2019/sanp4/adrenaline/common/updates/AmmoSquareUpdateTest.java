package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.AmmoSquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.*;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class AmmoSquareUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();

    private AmmoSquareView ammoSquareView;
    private CoordPair location = new CoordPair(2,3);
    private Set<String> players = new HashSet<>();
    private RoomColor roomColor = RoomColor.BLUE;
    private Map adjacentMap = new HashMap<>();

    @Test
    public void serialize_ShouldSucceed() throws IOException {
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
        AmmoSquareUpdate update = new AmmoSquareUpdate(ammoSquareView);
        String s = objectMapper.writeValueAsString(update);

        AmmoSquareUpdate ammoSquareUpdate = objectMapper.readValue(s, AmmoSquareUpdate.class);

        assertEquals(players, ammoSquareUpdate.getAmmoSquare().getPlayers());
        assertEquals(roomColor, ammoSquareUpdate.getAmmoSquare().getRoomColor());
        assertEquals(adjacentMap, ammoSquareUpdate.getAmmoSquare().getAdjacentMap());

        /* Test addPlayer and removePlayer */
        players.add("Tiziano");
        ammoSquareView.addPlayer("Tiziano");
        update = new AmmoSquareUpdate(ammoSquareView);
        s = objectMapper.writeValueAsString(update);
        ammoSquareUpdate = objectMapper.readValue(s, AmmoSquareUpdate.class);

        assertEquals(players, ammoSquareUpdate.getAmmoSquare().getPlayers());

        players.remove("Tiziano");
        ammoSquareView.removePlayer("Tiziano");
        update = new AmmoSquareUpdate(ammoSquareView);
        s = objectMapper.writeValueAsString(update);
        ammoSquareUpdate = objectMapper.readValue(s, AmmoSquareUpdate.class);

        assertEquals(players, ammoSquareUpdate.getAmmoSquare().getPlayers());

    }
}
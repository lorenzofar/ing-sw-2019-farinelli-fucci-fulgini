package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Leaderboard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class LeaderboardUpdateTest {

    private static ObjectMapper objectMapper = JSONUtils.getObjectMapper();

    private static Leaderboard generateLeaderboard() {
        List<Player> playerList = Arrays.stream(new String[]{"p1", "p2", "p3"})
                .map(ModelTestUtil::generatePlayer)
                .collect(Collectors.toList());

        return Leaderboard.generate(playerList);
    }

    @Test
    public void create_thenSerialize_shouldDeserializeCorrectly() throws Exception {
        /* Create a sample leaderboard with players in initial state */
        Leaderboard leaderboard = generateLeaderboard();

        /* Create the update */
        LeaderboardUpdate update = new LeaderboardUpdate(leaderboard);

        /* Serialize and de-serialize it */
        String s = objectMapper.writeValueAsString(update);
        update = objectMapper.readValue(s, LeaderboardUpdate.class);

        /* Check that the fields are set correctly */
        leaderboard = update.getLeaderboard();
        assertNotNull(leaderboard);

        /* Check the entries */
        List<Leaderboard.Entry> entries = leaderboard.getEntries();
        assertEquals(3, entries.size());

        entries.forEach(entry -> {
            assertFalse(entry.getName().isEmpty());
            assertEquals(0, entry.getScore());
            assertEquals(0, entry.getDeaths());
            assertEquals(0, entry.getPerformedKillshots());
            assertEquals(0, entry.getPerformedOverkills());
        });
    }
}
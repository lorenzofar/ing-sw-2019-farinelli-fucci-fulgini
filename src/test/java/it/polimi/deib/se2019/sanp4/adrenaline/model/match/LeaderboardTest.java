package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class LeaderboardTest {

    private static ObjectMapper objectMapper = JSONUtils.getObjectMapper();

    private static Player preparePlayer(String name, int score, int killshots, int overkills, int deaths) {
        Player p;
        p = ModelTestUtil.generatePlayer(name);
        p.addScorePoints(score);
        for (int i = 0; i < killshots; i++) {
            p.addPerformedKillshot();
        }
        for (int i = 0; i < overkills; i++) {
            p.addPerformedOverkill();
        }
        for (int i = 0; i < deaths; i++) {
            p.getPlayerBoard().addDeath();
        }
        return p;
    }

    @Test
    public void leaderboard_generate_fromPlayers_shouldGenerateAndSortEntries() {
        Map<String, Player> playerMap = new HashMap<>(3);
        Player p;
        /* Prepare each player with unique data */
        /* bzoto: score 1 */
        p = preparePlayer("bzoto", 1, 2, 3, 4);
        playerMap.put(p.getName(), p);

        /* loSqualo: score 15 */
        p = preparePlayer("loSqualo", 15, 6, 7, 8);
        playerMap.put(p.getName(), p);

        /* zoni: score 5 */
        p = preparePlayer("zoniMyLord", 5, 9, 10, 11);
        playerMap.put(p.getName(), p);

        /* Prepare the list of players */
        List<Player> playerList = new ArrayList<>(playerMap.values());

        /* Generate the leaderboard */
        Leaderboard leaderboard = Leaderboard.generate(playerList);

        /* Check the entries */
        List<Leaderboard.Entry> entries = leaderboard.getEntries();
        assertEquals(3, leaderboard.getEntries().size());
        assertTrue(entries.stream().anyMatch(e -> e.getName().equals("bzoto")));
        assertTrue(entries.stream().anyMatch(e -> e.getName().equals("loSqualo")));
        assertTrue(entries.stream().anyMatch(e -> e.getName().equals("zoniMyLord")));

        /* Check that the values and the ordering are correct */
        int previousScore = 500;
        for (Leaderboard.Entry entry : entries) {
            /* Get the corresponding player */
            p = playerMap.get(entry.getName());

            assertEquals(p.getScore(), entry.getScore());
            assertEquals(p.getPerformedKillshots(), entry.getPerformedKillshots());
            assertEquals(p.getPerformedOverkills(), entry.getPerformedOverkills());
            assertEquals(p.getPlayerBoard().getDeaths(), entry.getDeaths());

            assertTrue(entry.getScore() <= previousScore);
            previousScore = entry.getScore();
        }
    }

    @Test
    public void leaderboard_serialize_deserialize_shouldHaveCorrectAttributes() throws Exception {
        Map<String, Player> playerMap = new HashMap<>(3);
        Player p;
        /* Prepare each player with unique data */
        /* bzoto: score 1 */
        p = preparePlayer("bzoto", 1, 2, 3, 4);
        playerMap.put(p.getName(), p);

        /* loSqualo: score 15 */
        p = preparePlayer("loSqualo", 15, 6, 7, 8);
        playerMap.put(p.getName(), p);

        /* zoni: score 5 */
        p = preparePlayer("zoniMyLord", 5, 9, 10, 11);
        playerMap.put(p.getName(), p);

        /* Prepare the list of players */
        List<Player> playerList = new ArrayList<>(playerMap.values());

        /* Generate the leaderboard */
        Leaderboard leaderboard = Leaderboard.generate(playerList);

        /* Serialize and de-serialize it */
        String s = objectMapper.writeValueAsString(leaderboard);
        leaderboard = objectMapper.readValue(s, Leaderboard.class);

        /* Check the entries */
        List<Leaderboard.Entry> entries = leaderboard.getEntries();
        assertEquals(3, leaderboard.getEntries().size());
        assertTrue(entries.stream().anyMatch(e -> e.getName().equals("bzoto")));
        assertTrue(entries.stream().anyMatch(e -> e.getName().equals("loSqualo")));
        assertTrue(entries.stream().anyMatch(e -> e.getName().equals("zoniMyLord")));

        /* Check that the values and the ordering are correct */
        int previousScore = 500;
        for (Leaderboard.Entry entry : entries) {
            /* Get the corresponding player */
            p = playerMap.get(entry.getName());

            assertEquals(p.getScore(), entry.getScore());
            assertEquals(p.getPerformedKillshots(), entry.getPerformedKillshots());
            assertEquals(p.getPerformedOverkills(), entry.getPerformedOverkills());
            assertEquals(p.getPlayerBoard().getDeaths(), entry.getDeaths());

            assertTrue(entry.getScore() <= previousScore);
            previousScore = entry.getScore();
        }
    }
}
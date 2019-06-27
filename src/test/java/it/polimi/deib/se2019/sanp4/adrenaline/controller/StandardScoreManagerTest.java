package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class StandardScoreManagerTest {

    private static Match match;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static int skulls = 5;
    private static StandardScoreManager scoreManager = new StandardScoreManager();

    @BeforeClass
    public static void classSetup() {
        /* Disable logging */
        ModelTestUtil.disableLogging();

        /* Load schemas and assets */
        ModelTestUtil.loadCreatorResources();

        validConfig = new MatchConfiguration(0, skulls);

        /* Create list of unique users */
        validNames = new HashSet<>();
        validNames.add("mango");
        validNames.add("avocado");
        validNames.add("papaya");
        validNames.add("durian");
    }

    @Before
    public void setUp() throws Exception {
        /* Use match creator to create a mock match */
        match = MatchCreator.createMatch(validNames, validConfig);
    }

    public void shoot(Player shooter, Player shot, int damage) {
        shot.getPlayerBoard().addDamage(shooter, damage);
    }

    @Test
    public void scoreTurn_scoreFinal_ShouldSucceed() {
        shoot(match.getPlayerByName("mango"),
                match.getPlayerByName("papaya"),
                4);
        shoot(match.getPlayerByName("durian"),
                match.getPlayerByName("papaya"),
                5);
        shoot(match.getPlayerByName("avocado"),
                match.getPlayerByName("papaya"),
                5);
        scoreManager.scoreTurn(match);

        assertEquals(8, match.getPlayerByName("durian").getScore());
        assertEquals(7, match.getPlayerByName("mango").getScore());
        assertEquals(4, match.getPlayerByName("avocado").getScore());
        assertEquals(0, match.getPlayerByName("papaya").getScore());

        scoreManager.scoreFinal(match);

        assertEquals(8, match.getPlayerByName("durian").getScore());
        assertEquals(7, match.getPlayerByName("mango").getScore());
        assertEquals(12, match.getPlayerByName("avocado").getScore());
        assertEquals(0, match.getPlayerByName("papaya").getScore());
    }

    @Test
    public void noShots_ScoreFinal_shouldSucceed() {
        scoreManager.scoreTurn(match);
        scoreManager.scoreFinal(match);

        assertEquals(0, match.getPlayerByName("durian").getScore());
        assertEquals(0, match.getPlayerByName("mango").getScore());
        assertEquals(0, match.getPlayerByName("avocado").getScore());
        assertEquals(0, match.getPlayerByName("papaya").getScore());
    }

    @Test
    public void tieBreaker_shouldSucceed() {
        shoot(match.getPlayerByName("mango"),
                match.getPlayerByName("papaya"),
                11);
        shoot(match.getPlayerByName("durian"),
                match.getPlayerByName("mango"),
                12);
        shoot(match.getPlayerByName("avocado"),
                match.getPlayerByName("durian"),
                11);
        scoreManager.scoreTurn(match);
        assertEquals(9, match.getPlayerByName("durian").getScore());
        assertEquals(9, match.getPlayerByName("mango").getScore());
        assertEquals(9, match.getPlayerByName("avocado").getScore());
        assertEquals(0, match.getPlayerByName("papaya").getScore());

        /* The player with the overkill takes 8 points in the final scoring, the tie of the two
        players with a single kill is broken in favour of the first to kill */
        scoreManager.scoreFinal(match);
        assertEquals(17, match.getPlayerByName("durian").getScore());
        assertEquals(15, match.getPlayerByName("mango").getScore());
        assertEquals(9, match.getPlayerByName("avocado").getScore());
        assertEquals(0, match.getPlayerByName("papaya").getScore());
    }
}
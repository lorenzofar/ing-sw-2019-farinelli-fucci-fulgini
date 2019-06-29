package it.polimi.deib.se2019.sanp4.adrenaline.controller.match;

import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class StandardScoreManagerTest {

    private static Match match;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static int skulls = 5;
    private static StandardScoreManager scoreManager;

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
    public void setUp() {
        /* Use match creator to create a mock match */
        match = MatchCreator.createMatch(validNames, validConfig);
        scoreManager = new StandardScoreManager();
    }

    public void shoot(Player shooter, Player shot, int damage) {
        shot.getPlayerBoard().addDamage(shooter, damage);
    }

    @Test
    public void scoreTurn_scoreFinal_ShouldSucceed() {
        Player papaya = match.getPlayerByName("papaya");
        Player mango = match.getPlayerByName("mango");
        Player avocado = match.getPlayerByName("avocado");
        Player durian = match.getPlayerByName("durian");

        shoot(mango, papaya,4);
        shoot(durian, papaya, 5);
        shoot(avocado, papaya, 5);
        scoreManager.scoreTurn(match);

        assertEquals(8, durian.getScore());
        assertEquals(7, mango.getScore());
        assertEquals(4, avocado.getScore());
        assertEquals(0, papaya.getScore());
        assertEquals("avocado", match.getKillshotsTrack().get(0).getName());

        /* Reset boards */
        for (Player player : new Player[]{papaya, mango, avocado, durian}) {
            try {
                player.getPlayerBoard().updateDeathsAndReset();
            } catch (PlayerException e) {
                /* OK, just flip */
            }
        }

        scoreManager.scoreFinal(match);

        assertEquals(8, durian.getScore());
        assertEquals(7, mango.getScore());
        assertEquals(12, avocado.getScore());
        assertEquals(0, papaya.getScore());
    }

    @Test
    public void noShots_ScoreFinal_shouldSucceed() {
        Player papaya = match.getPlayerByName("papaya");
        Player mango = match.getPlayerByName("mango");
        Player avocado = match.getPlayerByName("avocado");
        Player durian = match.getPlayerByName("durian");

        scoreManager.scoreTurn(match);
        scoreManager.scoreFinal(match);

        assertEquals(0, durian.getScore());
        assertEquals(0, mango.getScore());
        assertEquals(0, avocado.getScore());
        assertEquals(0, papaya.getScore());
    }

    @Test
    public void tieBreaker_shouldSucceed() {
        Player papaya = match.getPlayerByName("papaya");
        Player mango = match.getPlayerByName("mango");
        Player avocado = match.getPlayerByName("avocado");
        Player durian = match.getPlayerByName("durian");

        shoot(mango, papaya, 11);
        shoot(durian, mango, 12);
        shoot(avocado, durian,11);
        scoreManager.scoreTurn(match);
        assertEquals(9, match.getPlayerByName("durian").getScore());
        assertEquals(9, match.getPlayerByName("mango").getScore());
        assertEquals(9, match.getPlayerByName("avocado").getScore());
        assertEquals(0, match.getPlayerByName("papaya").getScore());

        /* Reset boards */
        for (Player player : new Player[]{papaya, mango, avocado, durian}) {
            try {
                player.getPlayerBoard().updateDeathsAndReset();
            } catch (PlayerException e) {
                /* OK, just flip */
            }
        }

        /* The player with the overkill takes 8 points in the final scoring, the tie of the two
        players with a single kill is broken in favour of the first to kill */
        scoreManager.scoreFinal(match);
        assertEquals(17, match.getPlayerByName("durian").getScore());
        assertEquals(15, match.getPlayerByName("mango").getScore());
        assertEquals(9, match.getPlayerByName("avocado").getScore());
        assertEquals(0, match.getPlayerByName("papaya").getScore());
    }

    @Test
    public void scoreFinal_someFrenzy_shouldScoreBoardsWithDamage() throws Exception {
        Player papaya = match.getPlayerByName("papaya");
        Player mango = match.getPlayerByName("mango");
        Player avocado = match.getPlayerByName("avocado");
        Player durian = match.getPlayerByName("durian");

        /* Turn a couple boards to frenzy mode */
        mango.getPlayerBoard().turnFrenzy();
        avocado.getPlayerBoard().turnFrenzy();

        /* Add damages to each */
        shoot(durian, papaya, 1);
        shoot(mango, papaya, 2);
        shoot(durian, papaya, 2);

        shoot(avocado, mango, 3);
        shoot(papaya, mango, 2);
        shoot(avocado, mango, 3);

        scoreManager.scoreFinal(match);

        /* Check the points */
        assertEquals(1, papaya.getScore());
        assertEquals(6, mango.getScore());
        assertEquals(2, avocado.getScore());
        assertEquals(9, durian.getScore());
    }
}
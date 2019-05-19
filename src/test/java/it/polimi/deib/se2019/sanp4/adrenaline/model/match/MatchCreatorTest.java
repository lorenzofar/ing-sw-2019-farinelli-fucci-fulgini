package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.BoardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class MatchCreatorTest {

    private static MatchConfiguration validConfig;
    private static Set<String> validNames;

    @BeforeClass
    public static void classSetup() {
        /* Disable logging */
        ModelTestUtil.disableLogging();

        /* Load schemas and assets */
        ModelTestUtil.loadCreatorResources();

        validConfig = new MatchConfiguration(0, 5);

        /* Create list of unique users */
        validNames = new HashSet<>();
        validNames.add("bzoto");
        validNames.add("slinky");
        validNames.add("zoniMyLord");
    }

    /* ======== TEST CREATOR ========= */

    @Test(expected = NullPointerException.class)
    public void createMatch_nullUsernames_shouldThrow() {
        MatchCreator.createMatch(null, validConfig);
    }

    @Test(expected = NullPointerException.class)
    public void createMatch_nullConfig_shouldThrow() {
        MatchCreator.createMatch(validNames, null);
    }

    @Test
    public void createMatch_tooManyUsers_shouldThrow() {
        /* Generate a set with more usernames than colors */
        Set<String> usernames = Arrays.stream(PlayerColor.values())
                .map(PlayerColor::toString)
                .collect(Collectors.toSet());
        usernames.add("I won't get a color :(");

        try {
            MatchCreator.createMatch(usernames, validConfig);
            fail(); /* Fail if creation succeeds */
        } catch (Exception e) {
            /* Check that the message is as expected */
            assertTrue(e.getMessage().startsWith("Too many players"));
        }
    }

    @Test
    public void createMatch_invalidBoardId_shouldThrow() {
        MatchConfiguration invalidConfig = new MatchConfiguration(-10, 5);

        try {
            MatchCreator.createMatch(validNames, invalidConfig);
            fail(); /* Fail if creation succeeds */
        } catch (IllegalArgumentException e) {
            /* Check that the cause is as expected */
            assertThat(e.getCause(), instanceOf(BoardNotFoundException.class));
        }
    }

    @Test
    public void createMatch_negativeSkulls_shouldThrow() {
        MatchConfiguration invalidConfig = new MatchConfiguration(0, -5);

        try {
            MatchCreator.createMatch(validNames, invalidConfig);
            fail(); /* Fail if creation succeeds */
        } catch (IllegalArgumentException e) {
            /* Check that the message is as expected */
            assertTrue(e.getMessage().startsWith("Skulls count"));
        }
    }

    @Test
    public void createMatch_validParameters_shouldCreate() {
        Match match = MatchCreator.createMatch(validNames, validConfig);

        /* Check that the method returned something */
        assertNotNull(match);

        List<Player> players = match.getPlayers();
        /* Check that the players have been added */
        assertEquals(validNames.size(), players.size());

        assertTrue(players.stream()
                /* Map to names */
                .map(Player::getName)
                .collect(Collectors.toList())
                /* Check that all the names have been added */
                .containsAll(validNames)
        );

        /* Check that all player colors are distinct */
        assertEquals(players.size(), players.stream()
                /* Map to colors */
                .map(Player::getColor)
                /* Remove duplicates */
                .distinct().count()
        );

        /* Check the number of skulls */
        assertEquals(validConfig.getSkulls(), match.getSkulls());

        /* Check that the killshot track is empty */
        assertTrue(match.getKillshotsTrack().isEmpty());

        /* Check there is no turn set */
        assertNull(match.getCurrentTurn());

        /* Check that there is a board */
        assertNotNull(match.getBoard());

        /* Check the card stacks */
        assertNotNull(match.getAmmoStack());
        assertNotNull(match.getPowerupStack());
        assertNotNull(match.getWeaponStack());
    }
}
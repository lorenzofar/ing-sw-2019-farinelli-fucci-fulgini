package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerState;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class MatchTest {

    private static Match mockMatch;
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

        /* Use match creator to create a mock match */
        mockMatch = MatchCreator.createMatch(validNames, validConfig);
    }

    /* ======== TEST CREATION ========= */

    @Test
    public void create_positiveSkulls_shouldCreate() {
        /* Try to create a new match */
        Match match = new Match(5);

        assertEquals(5, match.getSkulls());
        assertTrue(match.getKillshotsTrack().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_negativeSkulls_shouldThrow() {
        new Match(-5);
    }

    /* ======== TEST FAILING SETTERS ========= */

    @Test(expected = NullPointerException.class)
    public void setBoard_null_shouldThrow() {
        Match match = new Match(5);

        match.setBoard(null);
    }

    @Test(expected = NullPointerException.class)
    public void setAmmoStack_null_shouldThrow() {
        Match match = new Match(5);

        match.setAmmoStack(null);
    }

    @Test(expected = NullPointerException.class)
    public void setPowerupStack_null_shouldThrow() {
        Match match = new Match(5);

        match.setPowerupStack(null);
    }

    @Test(expected = NullPointerException.class)
    public void setWeaponStack_null_shouldThrow() {
        Match match = new Match(5);

        match.setWeaponStack(null);
    }

    @Test(expected = NullPointerException.class)
    public void setPlayers_null_shouldThrow() {
        Match match = new Match(5);

        match.setPlayers(null);
    }

    /* ======== PLAYER-RELATED METHODS ========= */

    @Test
    public void getPlayerByName_nullName_shouldReturnNull() {
        Player player = mockMatch.getPlayerByName(null);
        assertNull(player);
    }

    @Test
    public void getPlayerByName_emptyName_shouldReturnNull() {
        Player player = mockMatch.getPlayerByName("");
        assertNull(player);
    }

    @Test
    public void getPlayerByname_existentName_shouldReturnPlayer() {
        Player player = mockMatch.getPlayerByName("bzoto");
        assertEquals("bzoto", player.getName());
    }

    @Test
    public void getPlayerByName_inexistentName_shouldReturnNull() {
        Player player = mockMatch.getPlayerByName("inexistent");
        assertNull(player);
    }

    @Test(expected = NullPointerException.class)
    public void suspendPlayer_null_shouldThrow() {
        mockMatch.suspendPlayer(null);
    }

    @Test(expected = IllegalStateException.class)
    public void suspendPlayer_inexistent_shouldThrow() {
        mockMatch.suspendPlayer("inexistent");
    }

    @Test
    public void suspendPlayer_existent_shouldSuspend() {
        mockMatch.suspendPlayer("bzoto");

        /* Check that the player has been suspended */
        Player player = mockMatch.getPlayerByName("bzoto");
        assertEquals(PlayerState.SUSPENDED, player.getState());

        /* Unsuspend the player in order not to have side-effects */
        player.setState(PlayerState.ONLINE);
    }
}
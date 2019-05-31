package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.MatchView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.AmmoSquare;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.SpawnSquare;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCardCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerState;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class MatchTest {

    private static Match mockMatch;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static int skulls = 5;

    @BeforeClass
    public static void classSetup() {
        /* Disable logging */
        ModelTestUtil.disableLogging();

        /* Load schemas and assets */
        ModelTestUtil.loadCreatorResources();

        validConfig = new MatchConfiguration(0, skulls);

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
    public void isPlayerTurn_nullTurn_shouldReturnFalse() {
        assertFalse(mockMatch.isPlayerTurn(mockMatch.getPlayerByName("bzoto")));
        assertFalse(mockMatch.isPlayerTurn("bzoto"));
    }

    @Test
    public void isPlayerTurn_checkConsistency() {
        Player player = mockMatch.getPlayers().get(0);
        mockMatch.setCurrentTurn(new PlayerTurn(player));

        /* Check that the overloaded methods return the same value */
        assertTrue(mockMatch.isPlayerTurn(player));
        assertTrue(mockMatch.isPlayerTurn(player.getName()));
    }

    @Test
    public void isFinalPlayer_nullPlayer_shouldReturnFalse() {
        assertFalse(mockMatch.isFinalPlayer(null));
    }

    @Test
    public void isFinalPlayer_killShotsTrackIsNotFull_shouldReturnFalse() {
        /* Make sure that this is empty */
        mockMatch.setKillshotsTrack(new ArrayList<>());

        assertFalse(mockMatch.isFinalPlayer(mockMatch.getPlayerByName("bzoto")));
    }

    @Test
    public void isFinalPlayer_thisIsTheFinalPlayer_shouldReturnTrue() {
        Player other = mockMatch.getPlayerByName("slinky");
        for (int i = 0; i < skulls - 1; i++) {
            mockMatch.addKillshot(other);
        }

        Player last = mockMatch.getPlayerByName("bzoto");
        mockMatch.addKillshot(last);
        assertTrue(mockMatch.isFinalPlayer(last));

        mockMatch.setKillshotsTrack(new ArrayList<>());
    }

    @Test
    public void isFinalPlayer_killShotsTrackIsFull_playerIsNotTheLast_shouldReturnFalse() {
        Player other = mockMatch.getPlayerByName("slinky");
        for (int i = 0; i < skulls - 1; i++) {
            mockMatch.addKillshot(other);
        }

        Player last = mockMatch.getPlayerByName("bzoto");
        mockMatch.addKillshot(last);
        assertFalse(mockMatch.isFinalPlayer(other));

        mockMatch.setKillshotsTrack(new ArrayList<>());
    }

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

    @Test(expected = NullPointerException.class)
    public void unsuspendPlayer_null_shouldThrow() {
        mockMatch.unsuspendPlayer(null);
    }

    @Test(expected = IllegalStateException.class)
    public void unsuspendPlayer_inexistent_shouldThrow() {
        mockMatch.unsuspendPlayer("inexistent");
    }

    @Test
    public void suspendPlayer_suspended_shouldBecomeOnline() {
        mockMatch.suspendPlayer("bzoto");

        /* Set it back online */
        mockMatch.unsuspendPlayer("bzoto");
        assertTrue(mockMatch.getPlayerByName("bzoto").getState().canPlay());
    }

    @Test
    public void suspendPlayer_notSuspended_shouldRemainUnsuspended() {
        mockMatch.unsuspendPlayer("bzoto");
        assertTrue(mockMatch.getPlayerByName("bzoto").getState().canPlay());
    }

    /* ======== TURN-RELATED METHODS ========= */

    @Test
    public void refillAmmoSquare_empty_ShouldBeRefilled() {
        /* Create an empty match instance */
        Match match = new Match(5);

        /* Create an ammo card stack with a bunch of cards */
        CardStack<AmmoCard> stack = new AutoShufflingStack<>(AmmoCardCreator.getAmmoCardDeck());
        match.setAmmoStack(stack);

        /* Create a new AmmoSquare (empty) and refill it from the stack */
        AmmoSquare square = new AmmoSquare(new CoordPair(2, 3));
        square.refill(match);

        /* Check that it has been refilled */
        assertTrue(square.isFull());

        /* Now try to refill it again and check that the ammo card is not changed */
        AmmoCard card = square.getAmmoCard();
        square.refill(match);
        assertEquals(card, square.getAmmoCard());
    }

    @Test
    public void refillSpawnSquare_empty_shouldBeRefilled() throws IOException {
        /* Create an empty match instance */
        Match match = new Match(5);

        /* Create a weapon card stack with a bunch of cards */
        CardStack<WeaponCard> stack = new AutoShufflingStack<>(WeaponCreator.createWeaponCardDeck());
        match.setWeaponStack(stack);

        /* Now refill an empty SpawnSquare */
        SpawnSquare square = new SpawnSquare(new CoordPair(2, 3));
        square.refill(match);

        /* Check that it is full */
        assertTrue(square.isFull());
    }

    @Test
    public void refillBoard_shouldRefillSquares() {
        /* Use the mock match */
        mockMatch.refillBoard();

        /* Check that all the squares in the board are now full */
        Collection<Square> squares = mockMatch.getBoard().getSquares();
        squares.forEach(sq -> assertTrue(sq.isFull()));
    }

    /* ========== KILLSHOT TRACK =========== */

    @Test(expected = NullPointerException.class)
    public void setKillshotsTrack_null_shouldThrow() {
        mockMatch.setKillshotsTrack(null);
    }

    @Test(expected = NullPointerException.class)
    public void addKillshot_nullPlayer_shouldThrow() {
        mockMatch.addKillshot(null);
    }

    @Test
    public void addKillshot_notFull_shouldAdd() {
        Player p = mockMatch.getPlayerByName("bzoto");

        mockMatch.addKillshot(p);

        assertEquals(p, mockMatch.getKillshotsTrack().get(0));
        assertEquals(1, mockMatch.getKillshotsTrack().size());

        mockMatch.setKillshotsTrack(new LinkedList<>()); /* Reset */
    }

    @Test
    public void addKillshot_full_shouldNotAdd() {
        Player b = mockMatch.getPlayerByName("bzoto");
        Player z = mockMatch.getPlayerByName("zoniMyLord");

        /* Refill killshot track */
        for (int i = 0; i < skulls; i++) {
            mockMatch.addKillshot(z);
        }

        /* Try to add another killshot */
        mockMatch.addKillshot(b);

        /* Check that it has not been added */
        assertEquals(skulls, mockMatch.getKillshotsTrack().size());
        assertFalse(mockMatch.getKillshotsTrack().contains(b));

        mockMatch.setKillshotsTrack(new LinkedList<>()); /* Reset */
    }

    @Test
    public void generateView_ShouldSucceed() {
        MatchView view = mockMatch.generateView();
        assertEquals(mockMatch.isFrenzy(), view.isFrenzy());
        int matchSkulls = mockMatch.getSkulls() + mockMatch.getKillshotsTrack().size();
        assertEquals(matchSkulls, view.getTotalSkulls());
        assertEquals(mockMatch.getKillshotsTrack().size(), view.getKillshotsCount());

    }
}
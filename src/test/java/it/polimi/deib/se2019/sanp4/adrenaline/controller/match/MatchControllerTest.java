package it.polimi.deib.se2019.sanp4.adrenaline.controller.match;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.FirstChoiceAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerBoard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum.*;
import static it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerState.SUSPENDED;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MatchControllerTest {

    private static Match match;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static int skulls = 5;
    private static Map<String, PersistentView> views;
    private static MatchController matchController;

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
        validNames.add("loSqualo");
        validNames.add("zoniMyLord");
    }

    @Before
    public void setUp() {
        /* Create views of players which respond with their names */
        views = new HashMap<>();
        validNames.forEach(n -> {
            PersistentView v = mock(PersistentView.class);
            when(v.getUsername()).thenReturn(n);
            views.put(n,v);
        });
        match = MatchCreator.createMatch(validNames, validConfig);
        matchController = new MatchController(match, views);
    }

    /* ============================= SELECT NEXT TURN =============================== */

    @Test
    public void selectNextTurn_noCurrentTurn_shouldSelectFirstPlayer() {
        /* Check that there is no current turn */
        assertNull(match.getCurrentTurn());

        /* Ask to select the next turn */
        matchController.selectNextTurn();

        /* Check that the first player has been selected */
        Player first = match.getPlayers().get(0);
        assertTrue(match.isPlayerTurn(first));
    }

    @Test
    public void selectNextTurn_suspendedPlayer_shouldBeSkipped() {
        assertNull(match.getCurrentTurn()); /* Check that there is no current turn */
        matchController.selectNextTurn(); /* Selects the first player */

        /* Now we suspend the second player */
        Player suspended = match.getPlayers().get(1);
        suspended.setState(SUSPENDED);

        matchController.selectNextTurn();

        /* Check that the third player has been picked */
        Player third = match.getPlayers().get(2);
        assertTrue(match.isPlayerTurn(third));

        /* Select the next player again and check that the first player is selected */
        matchController.selectNextTurn();
        Player first = match.getPlayers().get(0);
        assertTrue(match.isPlayerTurn(first));
    }

    @Test
    public void selectNextTurn_noOnlinePlayers_shouldSetFinished() {
        /* Suspend all players */
        match.getPlayers().forEach(p -> p.setState(SUSPENDED));

        matchController.selectNextTurn();

        assertTrue(matchController.isMatchFinished());
    }

    @Test
    public void selectNextTurn_finalPlayerIsSuspended_shouldSetFinished() {
        /* Give the turn to the third player */
        matchController.selectNextTurn();
        matchController.selectNextTurn();
        matchController.selectNextTurn();

        /* Pick the first player */
        Player p = match.getPlayers().get(0);
        p.setState(SUSPENDED);

        /* Fill the killshot track with his marks to make it the final player */
        for (int i = 0; i < skulls; i++) {
            match.addKillshot(p);
        }

        /* The final player cannot play, so the match ends */
        matchController.selectNextTurn();

        assertTrue(matchController.isMatchFinished());
    }

    /* ============================= MATCH FINISHED AND RESPAWN =============================== */

    @Test
    public void checkIfMatchIsFinished_killShotTrackNotFull_noSuspendedPlayers_shouldNotBeFinished() {
        /* Set up the first turn */
        matchController.selectNextTurn();

        matchController.checkIfMatchIsFinished();

        assertFalse(matchController.isMatchFinished());
    }

    @Test
    public void checkIfMatchIsFinished_belowMinPlayers_shouldBeFinished() {
        matchController.selectNextTurn();

        /* Suspend a player, active will be less than 3 */
        match.getPlayerByName("bzoto").setState(SUSPENDED);

        matchController.checkIfMatchIsFinished();

        assertTrue(matchController.isMatchFinished());
    }

    @Test
    public void checkIfMatchIsFinished_finalPlayerHasJustPlayed_shouldBeFinished() {
        Player p = match.getPlayerByName("bzoto");

        /* Fill the killshot track and go frenzy */
        for (int i = 0; i < skulls; i++) {
            match.addKillshot(p);
        }
        match.goFrenzy();

        /* Set the player as the owner of the current turn */
        PlayerTurn t = new PlayerTurn(p);
        t.setTurnState(PlayerTurnState.OVER);
        match.setCurrentTurn(t);

        matchController.checkIfMatchIsFinished();

        assertTrue(matchController.isMatchFinished());
    }

    @Test
    public void respawnDeadPlayers_allShouldRespawn() throws InterruptedException {
        /* Artificially put a dead player */
        Player p = match.getPlayerByName("bzoto");
        matchController.getDeadPlayers().add(p);

        /* Answer with the first choice when asked for something */
        doAnswer(new FirstChoiceAnswer()).when(views.get("bzoto")).sendChoiceRequest(any());

        /* Move the player to a non-spawn location */
        Board board = match.getBoard();
        board.movePlayer(p, board.getSquare(new CoordPair(1,1)));

        matchController.respawnDeadPlayers();

        /* Now he should be on a spawn point */
        assertTrue(board.getSpawnPoints().values().contains(p.getCurrentSquare()));
    }


    /* =========================== END CURRENT TURN ============================= */

    @Test
    public void endCurrentTurn_notFrenzy_shouldHandleDeadPlayers() {
        /* SET UP THE SCENARIO */
        Player bzoto = match.getPlayerByName("bzoto");
        Player loSqualo = match.getPlayerByName("loSqualo");
        Player zoni = match.getPlayerByName("zoniMyLord");

        /* bzoto is dead */
        bzoto.getPlayerBoard().addDamage(zoni, PlayerBoard.KILLSHOT_DAMAGE);

        /* loSqualo has enough damage to use the ADRENALINE1 action card */
        loSqualo.getPlayerBoard().addDamage(bzoto, 4);

        /* zoniMyLord has no damage */

        matchController.selectNextTurn(); /* Set up the turn */
        matchController.endCurrentTurn(); /* End the turn */

        /* CHECK THE STATE */
        assertThat(match.getCurrentTurn().getTurnState(), is(PlayerTurnState.OVER));

        /* Dead players have been detected */
        assertTrue(matchController.getDeadPlayers().contains(bzoto));
        assertEquals(1, matchController.getDeadPlayers().size());

        /* The boards of dead players have been reset */
        assertEquals(1, bzoto.getPlayerBoard().getDeaths());
        assertThat(bzoto.getPlayerBoard().getDamageCount(), is(0));

        /* The other boards should still have their damages */
        assertThat(loSqualo.getPlayerBoard().getDamageCount(), is(4));
        assertThat(zoni.getPlayerBoard().getDamageCount(), is(0));

        /* Points should have been given to the killer */
        assertTrue(zoni.getScore() > 0);

        /* The kill should have been tracked */
        assertThat(match.getSkulls(), is(skulls - 1));
        assertThat(match.getKillshotsTrack().get(0), is(zoni));
        assertThat(zoni.getPerformedKillshots(), is(1));

        /* Nothing should be in frenzy mode */
        assertFalse(match.isFrenzy());
        match.getPlayers().forEach(p -> assertThat(p.getPlayerBoard().getState().toString(), is("regular")));

        /* The action cards should be correct */
        assertThat(bzoto.getActionCard().getType(), is(REGULAR)); /* because he's dead */
        assertThat(loSqualo.getActionCard().getType(), is(ADRENALINE1)); /* it's been given thanks to damages */
        assertThat(zoni.getActionCard().getType(), is(REGULAR)); /* because he has no damage */
    }

    @Test
    public void endCurrentTurn_startFrenzy_shouldFlipBoardsWithNoDamage() {
        /* SET UP THE SCENARIO */
        Player bzoto = match.getPlayerByName("bzoto");
        Player loSqualo = match.getPlayerByName("loSqualo");
        Player zoni = match.getPlayerByName("zoniMyLord");

        /* The killshot track only has one empty space */
        for (int i = 0; i < skulls - 1; i++) {
            match.addKillshot(bzoto);
        }

        /* zoni has been overkilled by loSqualo */
        zoni.getPlayerBoard().addDamage(loSqualo, PlayerBoard.OVERKILL_DAMAGE);

        /* bzoto has no damage */

        /* loSqualo still has damage */
        loSqualo.getPlayerBoard().addDamage(zoni, 4);

        match.setCurrentTurn(new PlayerTurn(loSqualo)); /* Set up the turn of the killer */
        matchController.endCurrentTurn(); /* End the turn */

        /* CHECK THE STATE */
        assertThat(match.getCurrentTurn().getTurnState(), is(PlayerTurnState.OVER));

        /* The match is in frenzy mode */
        assertTrue(match.isFrenzy());

        /* Dead players have been detected */
        assertTrue(matchController.getDeadPlayers().contains(zoni));
        assertEquals(1, matchController.getDeadPlayers().size());

        /* The board of the dead player and the board with no damage should be reset and in frenzy mode */
        assertEquals(1, zoni.getPlayerBoard().getDeaths());
        assertThat(zoni.getPlayerBoard().getDamageCount(), is(0));
        assertThat(zoni.getPlayerBoard().getState().toString(), is("frenzy"));
        assertThat(bzoto.getPlayerBoard().getDamageCount(), is(0));
        assertThat(bzoto.getPlayerBoard().getState().toString(), is("frenzy"));

        /* The other board should still have damage and be in regular mode */
        assertThat(loSqualo.getPlayerBoard().getDamageCount(), is(4));
        assertThat(loSqualo.getPlayerBoard().getState().toString(), is("regular"));

        /* The overkill should have been counted */
        assertThat(loSqualo.getPerformedKillshots(), is(1));
        assertThat(loSqualo.getPerformedOverkills(), is(1));

        /* Check that players have the correct action cards */
        match.getPlayers().forEach(p -> assertThat(p.getActionCard().getType(), anyOf(is(FRENZY1), is(FRENZY2))));
    }
}
package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PowerupCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.CancelRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.match.SpawnController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerState;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SpawnControllerTest {

    @Mock
    private static PersistentView mockView;

    @Captor
    private static ArgumentCaptor<PowerupCardRequest> captor;

    private static Match match;

    private static SpawnController spawnController; /* UUT */

    @BeforeClass
    public static void classSetup() {
        ModelTestUtil.loadCreatorResources();
        ModelTestUtil.disableLogging();
        /* Use the MatchCreator to create a new match */
        Set<String> usernames = new HashSet<>(3);
        usernames.add("a");
        usernames.add("b");
        usernames.add("c");
        match = MatchCreator.createMatch(usernames, new MatchConfiguration(0, 5));
        spawnController = new SpawnController(match);
    }

    @Test
    public void initialSpawn_userResponds_shouldUseHisChoice() throws InterruptedException {
        /* Set up the username of the player */
        when(mockView.getUsername()).thenReturn("a");
        final AmmoCube[] selectedSpawnPointColor = new AmmoCube[1];
        /* Mock the user's answer */
        doAnswer(invocationOnMock -> {
            /* Intercept the request */
            PowerupCardRequest req = (PowerupCardRequest) invocationOnMock.getArguments()[0];

            CompletableChoice<PowerupCard> c = new CompletableChoice<>(req);
            /* Choose the first option */
            selectedSpawnPointColor[0] = req.getChoices().get(0).getCubeColor();
            return c.complete(req.getChoices().get(0));
        }).when(mockView).sendChoiceRequest(captor.capture());

        /* Test the first spawn */
        spawnController.initialSpawn(mockView);

        /* Check that the user is in the correct position */
        Square spawnPoint = match.getBoard().getSpawnPoints().get(selectedSpawnPointColor[0]);
        Player player = match.getPlayerByName(mockView.getUsername());
        assertEquals(spawnPoint, player.getCurrentSquare());

        /* Check that it has a powerup card */
        assertEquals(1, player.getPowerups().size());

        /* Remove the powerup from player's hands */
        player.removePowerup(player.getPowerups().get(0));
    }

    @Test
    public void initialSpawn_userCancels_shouldNotSpawnAndThrow() throws InterruptedException {
        /* Set up the username of the player (different from the other test) */
        when(mockView.getUsername()).thenReturn("b");
        /* Mock the user's answer */
        doAnswer(new CancelRequestAnswer()).when(mockView).sendChoiceRequest(captor.capture());

        try {
            /* Test the first spawn */
            spawnController.initialSpawn(mockView);
            fail();
        } catch (CancellationException e) {
            /* Check that the user is not spawned */
            Player player = match.getPlayerByName(mockView.getUsername());
            assertNull(player.getCurrentSquare());
            assertTrue(player.getPowerups().isEmpty());
        }
    }

    @Test
    public void respawn_userSelectsDrawnCard_shouldSpawnAndKeepHisCards() throws InterruptedException, FullCapacityException {
        /* Set up the username of the player */
        when(mockView.getUsername()).thenReturn("a");
        final AmmoCube[] selectedSpawnPointColor = new AmmoCube[1];
        /* Mock the user's answer */
        doAnswer(invocationOnMock -> {
            /* Intercept the request */
            PowerupCardRequest req = (PowerupCardRequest) invocationOnMock.getArguments()[0];

            CompletableChoice<PowerupCard> c = new CompletableChoice<>(req);
            /* Choose the first option */
            selectedSpawnPointColor[0] = req.getChoices().get(0).getCubeColor();
            return c.complete(req.getChoices().get(0));
        }).when(mockView).sendChoiceRequest(captor.capture());

        /* Add a powerup to the player */
        Player player = match.getPlayerByName(mockView.getUsername());
        PowerupCard previous = new PowerupCard(PowerupEnum.TELEPORTER, AmmoCube.BLUE);
        player.addPowerup(previous);

        /* Test the respawn */
        spawnController.respawn(mockView);

        /* Check that the user is in the correct position */
        Square spawnPoint = match.getBoard().getSpawnPoints().get(selectedSpawnPointColor[0]);
        assertEquals(spawnPoint, player.getCurrentSquare());

        /* Check that the user still has his powerups */
        assertTrue(player.getPowerups().contains(previous));

        /* Remove the powerups */
        player.removePowerup(previous);
    }

    @Test
    public void respawn_userSelectsCardFromHisHand_shouldGetTheDrawnCard() throws FullCapacityException, InterruptedException {
        /* Set up the username of the player */
        when(mockView.getUsername()).thenReturn("a");
        final PowerupCard[] otherPowerups = new PowerupCard[2];
        /* Mock the user's answer */
        doAnswer(invocationOnMock -> {
            /* Intercept the request */
            PowerupCardRequest req = (PowerupCardRequest) invocationOnMock.getArguments()[0];

            CompletableChoice<PowerupCard> c = new CompletableChoice<>(req);
            /* Choose the last option */
            otherPowerups[0] = req.getChoices().get(0);
            otherPowerups[1] = req.getChoices().get(1);
            return c.complete(req.getChoices().get(2));
        }).when(mockView).sendChoiceRequest(captor.capture());

        /* Add a couple to the player */
        Player player = match.getPlayerByName(mockView.getUsername());
        List<PowerupCard> previous = Arrays.asList(
                new PowerupCard(PowerupEnum.TELEPORTER, AmmoCube.BLUE),
                new PowerupCard(PowerupEnum.TELEPORTER, AmmoCube.RED));
        player.addPowerup(previous.get(0));
        player.addPowerup(previous.get(1));

        /* Test the respawn */
        spawnController.respawn(mockView);

        /* Check that the user has the powerups he did not use ti spawn */
        assertTrue(player.getPowerups().contains(otherPowerups[0]));
        assertTrue(player.getPowerups().contains(otherPowerups[1]));

        /* Remove the powerups */
        player.getPowerups().forEach(player::removePowerup);
    }

    @Test
    public void respawn_userIsSuspended_shouldAutoRespawn() throws InterruptedException {
        when(mockView.getUsername()).thenReturn("c");
        Player player = match.getPlayerByName(mockView.getUsername());

        /* Set player's state to suspended */
        player.setState(PlayerState.SUSPENDED);

        /* Move it out of a spawn point */
        Board board = match.getBoard();
        board.movePlayer(player, board.getSquare(new CoordPair(1,1)));

        /* Try to respawn */
        spawnController.respawn(mockView);

        /* Now it should be on a spawn point */
        assertTrue(board.getSpawnPoints().values().contains(player.getCurrentSquare()));

        /* Reset the player's state */
        player.setState(PlayerState.ONLINE);
    }

    @Test
    public void respawn_requestCancelled_shouldAutoRespawn() throws InterruptedException {
        when(mockView.getUsername()).thenReturn("c");
        Player player = match.getPlayerByName(mockView.getUsername());

        /* Mock the user's answer */
        doAnswer(new CancelRequestAnswer()).when(mockView).sendChoiceRequest(captor.capture());

        /* Move the player out of a spawn point */
        Board board = match.getBoard();
        board.movePlayer(player, board.getSquare(new CoordPair(1,1)));

        /* Try to respawn */
        spawnController.respawn(mockView);

        /* Now it should be on a spawn point */
        assertTrue(board.getSpawnPoints().values().contains(player.getCurrentSquare()));
    }
}
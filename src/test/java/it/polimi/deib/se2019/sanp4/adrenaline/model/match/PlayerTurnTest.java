package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerTurnView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;

import static it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurnState.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayerTurnTest {

    private static int maxActions = 2;
    private static Player notSpawnedPlayer;
    private static Player spawnedPlayer;
    private static ActionCard actionCard;
    private static Collection<ActionEnum> actionList;
    private static ActionEnum finalAction;

    @BeforeClass
    public static void setup(){
        actionList = Arrays.asList(ActionEnum.ADRENALINE_GRAB, ActionEnum.ADRENALINE_SHOOT);
        ActionCardEnum actionCardType = ActionCardEnum.ADRENALINE1;
        finalAction = ActionEnum.RELOAD;

        actionCard = new ActionCard(maxActions, actionCardType, actionList, finalAction);

        String playerName = "Fiziano";
        notSpawnedPlayer = new Player(playerName, actionCard, PlayerColor.BLUE);
        spawnedPlayer = new Player(playerName, actionCard, PlayerColor.BLUE);
        spawnedPlayer.setCurrentSquare(mock(Square.class));
    }

    @Test(expected = NullPointerException.class)
    public void create_nullPlayer_shouldThrow() {
        new PlayerTurn(null);
    }

    @Test
    public void create_playerNotSpawned_shouldSetInitialSpawn() {
        PlayerTurn turn = new PlayerTurn(notSpawnedPlayer);

        assertEquals(maxActions, turn.getRemainingActions());
        assertEquals(notSpawnedPlayer, turn.getTurnOwner());
        assertTrue(turn.getDamagedPlayers().isEmpty());
        assertEquals(INITIAL_SPAWN, turn.getTurnState());
        assertEquals(INITIAL_SPAWN.toString(), turn.getTurnState().toString());
    }

    @Test
    public void create_playerSpawned_shouldSetActionSelection() {
        PlayerTurn turn = new PlayerTurn(spawnedPlayer);

        assertEquals(maxActions, turn.getRemainingActions());
        assertEquals(spawnedPlayer, turn.getTurnOwner());
        assertTrue(turn.getDamagedPlayers().isEmpty());
        assertEquals(SELECTING, turn.getTurnState());
        assertEquals(SELECTING.toString(), turn.getTurnState().toString());
    }

    @Test
    public void getAvailableActions_noActionExecuted_actionCardWithFinalAction_shouldReturnAllActions() {
        /* The initial action card is ok */

        PlayerTurn turn = new PlayerTurn(spawnedPlayer);

        Collection<ActionEnum> availableActions = turn.getAvailableActions();

        assertEquals(3, availableActions.size());
        assertTrue(availableActions.containsAll(actionList));
        assertTrue(availableActions.contains(finalAction));
        assertTrue(turn.canPerformAction(finalAction));
        assertTrue(turn.canPerformAction(actionList.iterator().next()));
    }

    @Test
    public void getAvailableActions_normalActionsFinished_actionCardWithFinalAction_shouldReturnFinalAction() {
        /* The initial action card is ok */

        PlayerTurn turn = new PlayerTurn(spawnedPlayer);
        turn.setRemainingActions(0);

        Collection<ActionEnum> availableActions = turn.getAvailableActions();

        assertEquals(1, availableActions.size());
        assertTrue(availableActions.contains(finalAction));
        assertTrue(turn.canPerformAction(finalAction));
    }

    @Test
    public void getAvailableActions_normalActionsFinished_actionCardWithNoFinalAction_shouldReturnEmpty() {
        /* Set up the action card */
        ActionCard noFinalAction = new ActionCard(2, ActionCardEnum.ADRENALINE1, actionList, null);
        spawnedPlayer.setActionCard(noFinalAction);

        PlayerTurn turn = new PlayerTurn(spawnedPlayer);
        turn.setRemainingActions(0);

        Collection<ActionEnum> availableActions = turn.getAvailableActions();

        assertTrue(availableActions.isEmpty());
        assertFalse(turn.canPerformAction(finalAction));

        /* Give the player back the old action card */
        spawnedPlayer.setActionCard(actionCard);
    }

    @Test
    public void getAvailableActions_turnIsOver_shouldReturnEmpty() {
        PlayerTurn turn = new PlayerTurn(spawnedPlayer);
        turn.setTurnState(OVER);

        Collection<ActionEnum> availableActions = turn.getAvailableActions();

        assertTrue(availableActions.isEmpty());
        assertFalse(turn.canPerformAction(finalAction));
    }

    @Test(expected = NullPointerException.class)
    public void canPerformAction_nullValue_shouldThrow() {
        PlayerTurn turn = new PlayerTurn(spawnedPlayer);

        turn.canPerformAction(null);
    }

    @Test
    public void setTurnState_validValue_shouldBeSet() {
        PlayerTurn turn = new PlayerTurn(spawnedPlayer);

        turn.setTurnState(BUSY);
        assertEquals(BUSY, turn.getTurnState());
    }

    @Test(expected = NullPointerException.class)
    public void setTurnState_nullValue_shouldThrow() {
        PlayerTurn turn = new PlayerTurn(spawnedPlayer);

        turn.setTurnState(null);
    }

    @Test
    public void addDamagedPlayer_shouldBeAdded() {
        PlayerTurn turn = new PlayerTurn(spawnedPlayer);

        Player damagedPlayer = mock(Player.class);
        turn.addDamagedPlayer(damagedPlayer);

        assertTrue(turn.getDamagedPlayers().contains(damagedPlayer));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setRemainingActions_negativeValue_shouldThrow() {
        PlayerTurn turn = new PlayerTurn(spawnedPlayer);

        turn.setRemainingActions(-5);
    }

    @Test
    public void setRemainingActions_validValue_shouldSet() {
        PlayerTurn turn = new PlayerTurn(spawnedPlayer);

        turn.setRemainingActions(1);
        assertEquals(1, turn.getRemainingActions());
    }

    @Test
    public void generateView_ShouldSucceed() {
        PlayerTurn playerTurn = new PlayerTurn(notSpawnedPlayer);
        playerTurn.setTurnState(BUSY);
        playerTurn.setRemainingActions(1);
        PlayerTurnView view = playerTurn.generateView();

        assertEquals(playerTurn.getTurnOwner().getName(), view.getPlayer());
        assertEquals(playerTurn.getRemainingActions(), view.getRemainingActions());
        assertEquals(playerTurn.getTurnState(), view.getState());
    }
}
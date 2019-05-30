package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerTurnView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerBoard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

public class PlayerTurnTest {

    private static String validName = "Fiziano";
    private static PlayerTurn playerTurn;
    private static Player player;
    private static ActionCard validActionCard;


    @BeforeClass
    public static void setup(){
        int validMaxActions = 2;
        Collection<ActionEnum> validActions = new ArrayList<>();
        validActions.add(ActionEnum.ADRENALINE_GRAB);
        validActions.add(ActionEnum.ADRENALINE_SHOOT);
        ActionCardEnum validType = ActionCardEnum.ADRENALINE1;
        ActionEnum validFinalAction = ActionEnum.RELOAD;
        String validDescription = "description1";
        RoomColor validColor = RoomColor.BLUE;

        validActionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);

        player = new Player(validName, validActionCard, PlayerColor.BLUE);

    }

    @Test
    public void generateView_ShouldSucceed() {
        playerTurn = new PlayerTurn(player);
        playerTurn.setTurnState(PlayerTurnState.BUSY);
        playerTurn.setRemainingActions(1);
        PlayerTurnView view = playerTurn.generateView();

        assertEquals(playerTurn.getTurnOwner().getName(), view.getPlayer());
        assertEquals(playerTurn.getRemainingActions(), view.getRemainingActions());
        assertEquals(playerTurn.getTurnState(), view.getState());
    }
}
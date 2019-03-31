package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.model.action.Action;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.BasicAction;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.List;

public class PlayerTurn{
    private Action currentAction;
    private int remainingActions;
    private Player turnOwner;

    private List<Player> hitPlayers; // Players that received damages in the current turn

    PlayerTurn(Player player){};

    public List<Action> getAvailableActions(){
        return null;
    }

    public void selectAction(Action action){};
    public BasicAction nextBasicAction(){
        return null;
    };
    public boolean isActionActive(){
        return false;
    }
    public BasicAction getCurrentBasicAction(){
        return null;
    }
    public void endTurn(){};
}
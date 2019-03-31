package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.CubeInterface;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.Weapon;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;

import java.util.List;

public class Player{
    private String name;
    private ActionCard actionCard;
    private boolean suspended;
    private int score;
    private int inflictedOverkills;
    private int inflictedKillshots;

    private List<Weapon> weapons;

    private Square currentSquare;

    private PlayerCharacter character;

    //TODO: Add reference to current square

    Player(String name, ActionCard actionCard){
        this.name = name;
        this.actionCard = actionCard;
    }

    public void setActionCard(ActionCard card){};
    public void addAmmo(List<CubeInterface> ammo){};
    public void payAmmo(List<CubeInterface> ammo){};
    public void addPowerup(Object powerup){}; //TODO: define powerupcard class
    public void removePowerUp(Object powerup){}; //TODO: same here
    public void addScorePoints(int points){};
    public void setSuspended(boolean suspended){
        this.suspended = suspended;
    }

}
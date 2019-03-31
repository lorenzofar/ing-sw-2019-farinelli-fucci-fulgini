package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerUpCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.Weapon;

import java.util.List;

public class Match {

    private PlayerTurn currentTurn;

    private Board board;

    private CardStack<AmmoCard> ammoStack;
    private CardStack<Weapon> weaponStack;
    private CardStack<PowerUpCard> powerupStack;

    private int skulls;
    private boolean frenzy;


    Match(List<Player> players, CardStack<AmmoCard> ammoStack, CardStack<Weapon> weaponStack, CardStack<PowerUpCard> powerupStack, int skulls){
        this.ammoStack = ammoStack;
        this.weaponStack = weaponStack;
        this.powerupStack = powerupStack;
        this.skulls = skulls;
        //TODO: Create players according to provided usernames
    }

    public boolean isPlayerTurn(String player){
        return false;
    }
    public boolean isPlayerTurn(Player player){
        return false;
    }

    public void suspendPlayer(String player){};

    public void removePlayer(String player){};

    public PlayerTurn getCurrentTurn(){
        return null;
    };

    public void endCurrentTurn(){};

    public List<Player> getPlayers(){
        return null;
    }

    public boolean isFrenzy(){
        return this.frenzy;
    }

    public void goFrenzy(){};
}

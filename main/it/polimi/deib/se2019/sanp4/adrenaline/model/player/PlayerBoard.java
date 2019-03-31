package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.ArrayList;
import java.util.Map;

public class PlayerBoard{
    private ArrayList<String> hits;
    private Map<String, Integer> marks;
    private int deaths;

    PlayerBoard(){

    }

    public void addDamage(Player shooter, int count){};
    public void addmark(Player shooter, int count){};
    public Player getKillshot(){
        return null;
    }
    public Player getOverkill(){
        return null;
    }

    public Map<Player, Integer> getPlayerScores(){
        return null;
    }
    public void resetDamage(){};
    public int getMarksByPlayer(Player player){
        return 0;
    };
    public boolean isDead(){
        return false;
    }
}
package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.List;

public class Room {

    private RoomColor color;

    private List<Square> squares;

    Room(List<Square> squares){
        this.squares = squares;
    }

    public List<Player> getPlayers(){
        return null;
    }

    public RoomColor getColor(){
        return this.color;
    };
}

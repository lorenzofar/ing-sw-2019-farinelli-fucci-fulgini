package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.List;
import java.util.Map;

public class Square {

    private CoordPair location;
    private AdjacentMap adjacentSquares;

    private Map<CardinalDirection, SquareConnection> adjacentConnection;
    private Map<CardinalDirection, CoordPair> adjacentSquare;

    public void addPlayer(Player player){};
    public void removePlayer(Player player){};
    public List<Player> getPlayers(){
        return null;
    };
    public Room getRoom(){
        return null;
    }
}

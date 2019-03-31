package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.Collection;
import java.util.List;

public class Board {

    private List<Square> squares; //FIXME Change squares representation

    Board(Collection<Square> squares){
    }

    private boolean isReachable(Square start, Square end, int maxMoves){
        return false;
    }

    public List<Player> getVisiblePlayers(Player player){
        return null;
    };

    public List<Player> getNotVisiblePlayers(Player player){
        return null;
    }

    public List<Square> getReachableSquares(CoordPair square, int maxMoves, int minMoves){
        return null;
    }

    public List<Square> getAheadSquares(CoordPair square, CardinalDirection direction, int minMoves, int maxMoves){
        return null;
    }

    public List<Square> getPath(Square start, Square end){
        return null;
    }

    public Square getSquare(CoordPair coords){
        return null;
    };
}

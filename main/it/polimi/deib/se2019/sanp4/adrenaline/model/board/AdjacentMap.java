package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import java.util.Collection;
import java.util.Map;

public class AdjacentMap {
    private Map<CardinalDirection, SquareConnection> map;

    AdjacentMap(Map<CardinalDirection, SquareConnection> map){
        this.map = map;
    }

    public SquareConnection getConnection(CardinalDirection direction){
        return null;
    }

    public Collection<Square> getSquares(){
        return null;
    }
}

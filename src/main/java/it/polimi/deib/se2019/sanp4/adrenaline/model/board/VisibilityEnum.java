package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.TriFunction;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Specifies the visibility condition for querying squares
 */
public enum VisibilityEnum{
    /** Any square can be queried */
    ANY("Any", (start, board, squares) -> squares),
    /** Only visible squares can be queried */
    VISIBLE("Visible",(start, board, squares) -> {
        Set<Square> visibleSquares = board.getVisibleSquares(start);
        return squares.stream().filter(visibleSquares::contains).collect(Collectors.toSet());
    }),
    /** Only non-visible squares can be queried */
    NOT_VISIBLE("Not visible", (start, board, squares) -> {
        Set<Square> visibleSquares = board.getVisibleSquares(start);
        return squares.stream().filter(square -> !visibleSquares.contains(square)).collect(Collectors.toSet());
    }),
    /** Squares are queried by ignoring walls */
    IGNORE_WALLS("Ignore walls", (start, board, squares) -> {
        //TODO: Check what this method has to retrieve
        return squares;
    });

    private String message;
    private TriFunction<Square, Board, Set<Square>, Set<Square>> filterFunction;

    VisibilityEnum(String message, TriFunction<Square, Board, Set<Square>, Set<Square>>  filterFunction){
        this.message = message;
        this.filterFunction= filterFunction;
    }

    public TriFunction<Square, Board, Set<Square>, Set<Square>> getFilterFunction(){
        return filterFunction;
    }

    @Override
    public String toString(){
        return message;
    }
}

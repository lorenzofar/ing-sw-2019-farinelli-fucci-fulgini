package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.BoardView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;

/**
 * An update sent when the {@link Board} changes.
 */
public class BoardUpdate extends ModelUpdate {

    private static final long serialVersionUID = 3349178317335347106L;
    private BoardView board;

    /**
     * Creates a board update that will be sent in broadcast.
     * @param board the board to send as update.
     */
    @JsonCreator
    public BoardUpdate(
            @JsonProperty("board") BoardView board) {
        super();
        this.board = board;
    }

    public BoardView getBoard() {
        return board;
    }

    public void setBoard(BoardView board) {
        this.board = board;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

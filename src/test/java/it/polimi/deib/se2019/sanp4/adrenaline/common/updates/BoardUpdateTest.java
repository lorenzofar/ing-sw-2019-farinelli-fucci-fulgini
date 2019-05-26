package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.AmmoSquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.BoardView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.SpawnSquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.SquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BoardUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();
    private BoardView board;
    private int rows = 2;
    private int columns = 1;
    private SquareView[][] squares = new SquareView[columns][rows];



    @Test
    public void serialize_ShouldSucceed() throws IOException {
        board = new BoardView(rows,columns);
        AmmoSquareView ammoSquareView = new AmmoSquareView(new CoordPair(0,0),
                                        RoomColor.BLUE);
        SpawnSquareView spawnSquareView = new SpawnSquareView(new CoordPair(0,1),
                                        RoomColor.BLUE);
        squares[0][0] = ammoSquareView;
        squares[0][1] = spawnSquareView;
        board.setSquares(squares);

        BoardUpdate update = new BoardUpdate(board);
        String s = objectMapper.writeValueAsString(update);

        BoardUpdate boardUpdate = objectMapper.readValue(s, BoardUpdate.class);

        assertEquals(rows, boardUpdate.getBoard().getRowsCount());
        assertEquals(columns, boardUpdate.getBoard().getColumnsCount());
        for(int i = 0; i<squares.length; i++){
            for(int j = 0; j<squares[i].length; j++){
                assertEquals(squares[i][j].getLocation(), boardUpdate.getBoard().getSquares()[i][j].getLocation());
            }
        }
        assertEquals(ammoSquareView.getLocation(), boardUpdate.getBoard().getSquare(new CoordPair(0,0)).getLocation());
        assertEquals(spawnSquareView.getLocation(), boardUpdate.getBoard().getSquare(new CoordPair(0,1)).getLocation());
    }
}
package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import java.util.Iterator;

/**
 * Regular state of the player board. Scores are given based on player's deaths (skulls) on the board.
 */
public class RegularPlayerBoardState implements PlayerBoardState {
    private static final int[] POINTS = {8, 6, 4, 2, 1, 1};

    /**
     * Returns iterator for scoring player boards. The player with most damage
     * will get the first value and so on.
     * This iterator never stops.
     *
     * @param board player board to read useful data
     * @return never-ending iterator with scores
     */
    @Override
    public Iterator<Integer> getDamageScores(PlayerBoard board) {
        return new ScoresIterator(POINTS, board.getDeaths());
    }
}

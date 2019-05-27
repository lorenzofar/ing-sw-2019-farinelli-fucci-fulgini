package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import java.util.Iterator;

/**
 * Regular state of the player board. Scores do not depend on player's deaths.
 */
public class FrenzyPlayerBoardState implements PlayerBoardState {

    private static final int[] POINTS = {2, 1, 1, 1};

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
        return new ScoresIterator(POINTS, 0);
    }

    /**
     * Returns a string representation suitable for identifying the state
     * @return a string representation
     */
    @Override
    public String toString() {
        return "frenzy";
    }
}

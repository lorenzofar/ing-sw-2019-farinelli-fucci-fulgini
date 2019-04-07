package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Regular state of the player board. Scores do not depend on player's deaths.
 */
public class FrenzyPlayerBoardState implements PlayerBoardState {

    private class FrenzyScoresIterator implements Iterator<Integer> {
        private int[] points = {2, 1, 1, 1};
        private int i;

        private FrenzyScoresIterator() {
            i = 0;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Integer next() {
            if (i > points.length) {
                throw new NoSuchElementException();
            }
            if (i < points.length) {
                i++;
            }
            return points[i-1];
        }
    }

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
        return new FrenzyScoresIterator();
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The state of the {@link PlayerBoard} in frenzy mode
 * <p>
 * Scores do not depend on player's deaths and there is no extra point for first blood
 * </p>
 *
 * @author Alessandro Fulgini
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
     * Returns the iterator for giving extra points to players who performed
     * damage, bases on the damage position: the player with first blood gets
     * the first value and so on.
     * Here none of the players gets extra points
     * The iterator never stops.
     *
     * @return never-ending iterator with extra points
     */
    @Override
    public Iterator<Integer> getExtraPoints() {
        return new Iterator<Integer>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return 0;
            }
        };
    }

    /**
     * Returns a string representation suitable for identifying the state
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        return "frenzy";
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import java.util.Iterator;

/**
 * The state of the {@link PlayerBoard} in frenzy mode
 * <p>
 * Scores are given depending on the number of skulls on the board, which represent the deaths.
 * There is one extra point for first blood.
 * </p>
 */
public class RegularPlayerBoardState implements PlayerBoardState {
    private static final int[] POINTS = {8, 6, 4, 2, 1, 1};

    private static final int[] EXTRA_POINTS = {1, 0};

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

    /**
     * Returns the iterator for giving extra points to players who performed
     * damage, bases on the damage position: the player with first blood gets
     * the first value and so on.
     * Here the player with first blood gets one point, while the others get none
     * The iterator never stops.
     *
     * @return never-ending iterator with extra points
     */
    @Override
    public Iterator<Integer> getExtraPoints() {
        return new ScoresIterator(EXTRA_POINTS, 0);
    }

    /**
     * Returns a string representation suitable for identifying the state
     * @return a string representation
     */
    @Override
    public String toString() {
        return "regular";
    }
}

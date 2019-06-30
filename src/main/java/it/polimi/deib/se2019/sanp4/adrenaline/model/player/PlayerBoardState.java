package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import java.util.Iterator;

/**
 * Changes {@link PlayerBoard} behavior based on its state.
 */
public interface PlayerBoardState {

    /**
     * Returns iterator for scoring player boards. The player with most damage
     * will get the first value and so on.
     * This iterator never stops.
     *
     * @param board player board to read useful data
     * @return never-ending iterator with scores
     */
    Iterator<Integer> getDamageScores(PlayerBoard board);

    /**
     * Returns the iterator for giving extra points to players who performed
     * damage, bases on the damage position: the player with first blood gets
     * the first value and so on.
     * The iterator never stops.
     *
     * @return never-ending iterator with extra points
     */
    Iterator<Integer> getExtraPoints();
}

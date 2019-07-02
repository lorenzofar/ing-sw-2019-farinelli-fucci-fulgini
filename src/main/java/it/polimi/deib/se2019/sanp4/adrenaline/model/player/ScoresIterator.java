package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  A class providing an iterator to access scores
 *  <p>
 *  It can provide access also to a subset of scores by providing a starting index
 *  </p>
 *  The iteration works as follows:
 *  <ul>
 *      <li>The first value returned by {@link #next()} is {@code points[index]}</li>
 *      <li>Subsequent calls to {@link #next()} will return the next values in the array</li>
 *      <li>When {@link #next()} has been called for {@code points.length} times, {@link #next()}
 *      will always return the last value of the array</li>
 *  </ul>
 */
public class ScoresIterator implements Iterator<Integer> {
    /** List of assignable scores */
    private int[] points;
    /** Index from which the points subarray should start */
    private int startIndex;

    /**
     * Creates a new scores iterator based on given points array
     * @param points The array of points, not null and not empty
     * @param startIndex The index of the array from which to start iterating, must be in the array bounds
     */
    public ScoresIterator(int[] points, int startIndex){
        if(points.length == 0){
            throw new IllegalArgumentException("Points array cannot be empty");
        }
        if(startIndex >= points.length){
            throw new IllegalArgumentException("Start index cannot be outside points array bounds");
        }
        this.points = points;
        this.startIndex = startIndex;
    }

    @Override
    public boolean hasNext(){
        return true;
    }

    @Override
    public Integer next(){
        if(!hasNext()){
            throw new NoSuchElementException();
        }
        startIndex = startIndex >= points.length ? points.length : startIndex + 1;
        return points[startIndex-1];
    }
}

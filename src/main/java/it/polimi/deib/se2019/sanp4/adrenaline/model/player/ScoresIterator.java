package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  A class providing an iterator to access scores
 *  It can provide access also to a subset of scores by providing a starting index
 */
public class ScoresIterator implements Iterator<Integer> {
    /** List of assignable scores */
    private int[] points;
    /** Index from which the points subarray should start */
    private int startIndex;

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

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

    ScoresIterator(int[] points, int startIndex){
        this.points = points;
        this.startIndex = startIndex;
    }

    @Override
    public boolean hasNext(){
        return true;
    }

    @Override
    public Integer next(){
        if(startIndex > points.length){
            throw new NoSuchElementException();
        }
        else{
            startIndex++;
        }
        return points[startIndex-1];
    }
}

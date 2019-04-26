package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import java.util.Objects;

/** A class representing a location in cartesian coordinates */
public class CoordPair {
    /** The X coordinate */
    private int x;
    /** The Y coordinate */
    private int y;

    /** Default constructor only to be used by Jackson */
    protected CoordPair(){}

    /**
     * Creates a new pair of coordinates with the provided components
     * @param x The X coordinate, must be positive
     * @param y The Y coordinate, must be positive
     */
    public CoordPair(int x, int y){
        if(x < 0 || y < 0){
            throw new IllegalArgumentException("Coordinates must be positive");
        }
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieves the X coordinate
     * @return The X coordinate
     */
    public int getX(){
        return this.x;
    }

    /**
     * Retrieves the Y coordinate
     * @return The Y coordinate
     */
    public int getY(){
        return this.y;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == this) return true;
        if(!(obj instanceof CoordPair)) return false;
        return ((CoordPair) obj).getX() == this.x && ((CoordPair)obj).getY() == this.y;
    }

    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }

}

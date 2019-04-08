package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

/** A class representing a location in cartesian coordinates */
public class CoordPair {
    /** The X coordinate */
    private int x;
    /** The Y coordinate */
    private int y;

    /**
     * Creates a new pair of coordinates with the provided components
     * @param x The X coordinate
     * @param y The Y coordinate
     */
    CoordPair(int x, int y){
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

}

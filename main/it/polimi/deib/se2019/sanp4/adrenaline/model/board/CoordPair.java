package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

public class CoordPair {
    private int x;
    private int y;

    CoordPair(int x, int y){
        this.x = x;
        this.y = y;
    };

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

}

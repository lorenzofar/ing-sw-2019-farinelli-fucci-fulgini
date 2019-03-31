package it.polimi.deib.se2019.sanp4.adrenaline.model.action;

public class MoveAction extends BasicAction {
    private int maxMoves;
    MoveAction(int maxMoves){
        this.maxMoves = maxMoves;
    }

    public int getMaxMoves() {
        return maxMoves;
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

/** Represents the four cardinal directions */
public enum CardinalDirection {
    /* North cardinal direction */
    N("North"),
    /* East cardinal direction */
    E("East"),
    /* West cardinal direction */
    W("West"),
    /* South cardinal direction */
    S("South");

    private String message;

    CardinalDirection(String message){
        this.message = message;
    }

    @Override
    public String toString(){
        return this.message;
    }
}

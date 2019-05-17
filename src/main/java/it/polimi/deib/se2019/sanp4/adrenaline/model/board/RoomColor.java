package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

/** Represents the possible colors a room or a player can have */
public enum RoomColor {
    /** Blue color */
    BLUE("Blue"),
    /** Red color */
    RED("Red"),
    /** Gray color */
    GRAY("Gray"),
    /** Yellow color */
    YELLOW("Yellow"),
    /** Purple color */
    PURPLE("Purple"),
    /** Green color */
    GREEN("Green");

    private String message;

    RoomColor(String message){
        this.message = message;
    }

    @Override
    public String toString(){
        return this.message;
    }
}

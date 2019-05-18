package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

/** Represents the possible colors a player can have */
public enum PlayerColor {
    /** Blue color */
    BLUE("Blue"),
    /** Gray color */
    GRAY("Gray"),
    /** Yellow color */
    YELLOW("Yellow"),
    /** Purple color */
    PURPLE("Purple"),
    /** Green color */
    GREEN("Green");

    private String message;

    PlayerColor(String message){
        this.message = message;
    }

    @Override
    public String toString(){
        return this.message;
    }
}

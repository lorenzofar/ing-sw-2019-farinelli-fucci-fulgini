package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

/** Identifies the color of an ammo cube */
public enum AmmoCube{

    /** Red color */
    RED("Red"),
    /** Yellow color */
    YELLOW("Yellow"),
    /** Blue color */
    BLUE("Blue");

    private String message;

    AmmoCube(String message){
        this.message = message;
    }

    @Override
    public String toString(){
        return this.message;
    }

}
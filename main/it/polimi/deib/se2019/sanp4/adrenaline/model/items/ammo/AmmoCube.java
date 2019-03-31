package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

public enum AmmoCube{

    RED, YELLOW, BLUE;

    private String message;

    AmmoCube() {
        this.message = "";
    }

    AmmoCube(String message){
        this.message = message;
    };

    private String getMessage(){
        return this.message;
    };

}
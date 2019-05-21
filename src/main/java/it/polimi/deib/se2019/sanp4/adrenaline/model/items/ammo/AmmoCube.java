package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

/**
 * Identifies the color of an ammo cube
 */
public enum AmmoCube {

    /**
     * Blue color
     */
    BLUE("Blue", "\u001B[34m", "#3779ff"),
    /**
     * Yellow color
     */
    YELLOW("Yellow", "\u001B[33m", "#f9cc00"),
    /**
     * Red color
     */
    RED("Red", "\u001B[31m", "#f63a30");

    private String message;
    private String ansiCode;
    private String hexCode;

    AmmoCube(String message, String ansiCode, String hexCode) {
        this.message = message;
        this.ansiCode = ansiCode;
        this.hexCode = hexCode;
    }

    @Override
    public String toString() {
        return message;
    }

    public String getANSICode() {
        return ansiCode;
    }

    public String getHexCode() {
        return hexCode;
    }
}
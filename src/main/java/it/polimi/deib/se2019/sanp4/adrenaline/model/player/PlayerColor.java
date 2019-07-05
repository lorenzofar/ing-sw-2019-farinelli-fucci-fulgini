package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;

/**
 * Represents the possible colors a player can have
 *
 * @author Lorenzo Farinelli
 */
public enum PlayerColor implements ColoredObject {
    /**
     * Blue color
     */
    BLUE("Blue", "\u001B[34m", "#0A84FF"),
    /**
     * Gray color
     */
    GRAY("Gray", "\u001B[37m", "#D6D6D6"),
    /**
     * Yellow color
     */
    YELLOW("Yellow", "\u001B[33m", "#FFD60A"),
    /**
     * Purple color
     */
    PURPLE("Purple", "\u001B[35m", "#BF5AF2"),
    /**
     * Green color
     */
    GREEN("Green", "\u001B[32m", "#30D158");

    private String message;
    private String ansiCode;
    private String hexCode;

    PlayerColor(String message, String ansiCode, String hexCode) {
        this.message = message;
        this.ansiCode = ansiCode;
        this.hexCode = hexCode;
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public String getAnsiCode() {
        return ansiCode;
    }

    @Override
    public String getHexCode() {
        return hexCode;
    }
}

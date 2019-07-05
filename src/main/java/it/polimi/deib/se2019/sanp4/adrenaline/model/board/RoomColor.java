package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;

/**
 * Represents the possible colors a room can have
 *
 * @author Alessandro Fulgini, Lorenzo Farinelli, Tiziano Fucci
 */
public enum RoomColor implements ColoredObject {
    /**
     * Blue color
     */
    BLUE("Blue", "\u001B[34m", "#3779ff"),
    /**
     * Red color
     */
    RED("Red", "\u001B[31m", "#f63a30"),
    /**
     * Gray color
     */
    GRAY("Gray", "\u001B[37m", "#d6d6d6"),
    /**
     * Yellow color
     */
    YELLOW("Yellow", "\u001B[33m", "#f9cc00"),
    /**
     * Purple color
     */
    PURPLE("Purple", "\u001B[35m", "#5856d6"),
    /**
     * Green color
     */
    GREEN("Green", "\u001B[32m", "#4cd963");

    private String message;
    private String ansiCode;
    private String hexCode;

    RoomColor(String message, String ansiCode, String hexCode) {
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

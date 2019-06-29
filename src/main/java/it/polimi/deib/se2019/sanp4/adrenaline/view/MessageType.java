package it.polimi.deib.se2019.sanp4.adrenaline.view;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;

/**
 * Describes the importance level of a message
 */
public enum MessageType implements ColoredObject {
    /**
     * Informational message to show information about the ongoing game
     */
    INFO("Info", "\u001B[34m"),
    /**
     * Warning message to warn players about potential issues
     */
    WARNING("Warning", "\u001B[33m"),
    /**
     * Error message to warn about errors or illegal choices
     */
    ERROR("Error", "\u001B[31m");

    private String message;
    private String ansiCode;

    MessageType(String message, String ansiCode) {
        this.message = message;
        this.ansiCode = ansiCode;
    }

    @Override
    public String toString() {
        return this.message;
    }

    public String getAnsiCode() {
        return ansiCode;
    }

    @Override
    public String getHexCode() {
        return "";
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.view;

import javafx.scene.control.Alert;

/** Describes the importance level of a message */
public enum MessageType {
    /** Informational message to show information about the ongoing game */
    INFO("Info", "\u001B[34m", Alert.AlertType.INFORMATION),
    /** Warning message to warn players about potential issues */
    WARNING("Warning", "\u001B[33m", Alert.AlertType.WARNING),
    /** Error message to warn about errors or illegal choices */
    ERROR("Error", "\u001B[31m", Alert.AlertType.ERROR);

    private String message;
    private String ansiCode;
    private Alert.AlertType alertType;

    MessageType(String message, String ansiCode, Alert.AlertType alertType) {
        this.message = message;
        this.ansiCode = ansiCode;
        this.alertType = alertType;
    }

    @Override
    public String toString(){
        return this.message;
    }

    public String getAnsiCode() {
        return ansiCode;
    }

    public Alert.AlertType getAlertType() {
        return alertType;
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.view;

/** Describes the importance level of a message */
public enum MessageType {
    /** Informational message to show information about the ongoing game */
    INFO("Info"),
    /** Warning message to warn players about potential issues */
    WARNING("Warning"),
    /** Error message to warn about errors or illegal choices */
    ERROR("Error");

    private String message;

    MessageType(String message){
        this.message = message;
    }

    @Override
    public String toString(){
        return this.message;
    }
}

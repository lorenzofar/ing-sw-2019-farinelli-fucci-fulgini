package it.polimi.deib.se2019.sanp4.adrenaline.view;

public enum MessageType {
    INFO("Info"),
    WARNING("Warning"),
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

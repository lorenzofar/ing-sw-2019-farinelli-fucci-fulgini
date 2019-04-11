package it.polimi.deib.se2019.sanp4.adrenaline.view;

public enum ViewState {
    LOGIN("Login"),
    SPAWN_FORM("Spawn point selection");

    private String message;

    ViewState(String message){
        this.message = message;
    }

    @Override
    public String toString(){
        return this.message;
    }
}

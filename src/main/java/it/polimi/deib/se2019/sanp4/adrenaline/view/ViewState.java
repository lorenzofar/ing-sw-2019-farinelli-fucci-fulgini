package it.polimi.deib.se2019.sanp4.adrenaline.view;

/** Describes the state a view is into, to determine what it's currently doing */
public enum ViewState {
    /** The view is in the login state */
    LOGIN("Login"),
    /** The view is asking the player to select a spawn point */
    SPAWN_FORM("Spawn point selection");

    //TODO: Add more states

    private String message;

    ViewState(String message){
        this.message = message;
    }

    @Override
    public String toString(){
        return this.message;
    }
}

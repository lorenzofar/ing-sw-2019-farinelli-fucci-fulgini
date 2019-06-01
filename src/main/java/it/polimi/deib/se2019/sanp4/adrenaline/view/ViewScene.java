package it.polimi.deib.se2019.sanp4.adrenaline.view;

/** Describes the state a view is into, to determine what it's currently doing */
public enum ViewScene {
    /** The view is in the login state */
    LOGIN("Login"),
    /** The view is in the lobby, waiting for the match to start */
    LOBBY("Lobby"),
    /** The match is about to start */
    MATCH_STARTING("Match starting"),
    /** Match start has been cancelled (e.g. lack of players) */
    MATCH_START_CANCELLED("Match cancelled"),
    /** The view is asking the player to select a spawn point */
    SPAWN_FORM("Spawn point selection"),
    /** Scene of the current player */
    TURN_PLAYING("Current turn"),
    /** Scene of the players waiting for their turn */
    TURN_IDLE("Waiting for turn");

    //TODO: Add more states

    private String message;

    ViewScene(String message){
        this.message = message;
    }

    @Override
    public String toString(){
        return this.message;
    }
}

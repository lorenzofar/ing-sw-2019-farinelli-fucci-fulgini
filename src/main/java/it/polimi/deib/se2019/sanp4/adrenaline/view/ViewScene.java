package it.polimi.deib.se2019.sanp4.adrenaline.view;

/**
 * Describes the state a view is into, to determine what it's currently doing
 */
public enum ViewScene {
    /**
     * The view is in the login state
     */
    LOGIN("Login"),
    /**
     * The view is in the lobby, waiting for the match to start
     */
    LOBBY("Lobby"),
    /**
     * The view has been reconnected to the server but is waiting to rejoin the match
     */
    WAITING_REJOIN("Waiting rejoin"),
    /**
     * Scene of the current player
     */
    TURN_PLAYING("Current turn"),
    /**
     * Scene of the players waiting for their turn
     */
    TURN_IDLE("Waiting for turn"),
    /**
     * Scene where the leaderboard with the final scores is shown
     */
    FINAL_SCORES("Final scores"),
    /**
     * The user is disconnected from the server
     */
    DISCONNECTED("Disconnected");

    private String message;

    ViewScene(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

    /**
     * Determines whether the scene represents a view in which the user is actually
     * playing and hence its content should be refreshed to match the latest incoming updates
     *
     * @return {@code true} if the view should be refreshed, {@code false} otherwise
     */
    public boolean isGameScene() {
        switch (this) {
            case TURN_PLAYING:
            case TURN_IDLE:
                return true;
            default:
                return false;
        }
    }

}

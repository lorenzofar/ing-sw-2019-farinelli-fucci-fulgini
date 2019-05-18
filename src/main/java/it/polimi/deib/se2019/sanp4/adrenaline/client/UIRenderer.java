package it.polimi.deib.se2019.sanp4.adrenaline.client;

public interface UIRenderer {

    /**
     * Initialize the renderer and show the launch screen of the game,
     * where the user can set up the network connection and username
     */
    void initialize();

    /**
     * Show the waiting screen when the match has yet to be started
     */
    void showLobby();

    /**
     * Notify the user about the imminent start of the game
     */
    void startWaitingMatch();

    /**
     * Cancel the waiting indicators when the game start is cancelled (e.g. due to lack of players)
     */
    void cancelWaitingMatch();

    /**
     * Prepare the client for the game
     * Triggered when the match starts
     */
    void showMatchScreen();

    //TODO: Add more methods
}

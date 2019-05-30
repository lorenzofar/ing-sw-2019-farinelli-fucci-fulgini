package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.util.Collection;

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
     * Updates the lobby with current information about connected players
     */
    void updateLobby(Collection<String> waitingPlayers, boolean matchStarting);

    /**
     * Prepare the client for the game
     * Triggered when the match starts
     */
    void showMatchScreen();

    /**
     * Shows a message to the user
     * @param text The text of the message
     * @param type The type of the message
     */
    void showMessage(String text, MessageType type);

    //TODO: Add more methods
}

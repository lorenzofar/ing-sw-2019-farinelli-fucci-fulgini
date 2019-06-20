package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequestVisitor;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.util.Collection;

public interface UIRenderer extends ChoiceRequestVisitor {

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
     *
     * @param text The text of the message
     * @param type The type of the message
     */
    void showMessage(String text, MessageType type);

    /**
     * Cancels the current selection request (if present)
     */
    void cancelSelection();

    /**
     * Shows a screen to tell the user that he is disconnected, letting him to reconnect
     */
    void showDisconnectedScreen();

    /* ====== REFRESH ENDPOINTS ===== */

    /**
     * Refreshes the rendered killshots track
     */
    public void refreshKillshotsTrack();

    /**
     * Refreshes a rendered player board
     *
     * @param boardOwner The player owning the board to refresh
     */
    public void refreshPlayerBoard(String boardOwner);

    /**
     * Refreshes the rendered actions track of the user
     */
    public void refreshActionsTrack();

    /**
     * Refreshes the whole rendered game board
     */
    public void refreshGameBoard();

    /**
     * Refreshes the rendered game board by updating the provided squares
     *
     * @param squares The collection of squares to refresh
     */
    public void refreshGameBoard(CoordPair... squares);

    /**
     * Refreshes the rendered information about owned ammo
     */
    public void refreshAmmoInfo();

    /**
     * Refreshes the rendered information about the match
     */
    public void refreshMatchInfo();

    //TODO: Add more methods
}

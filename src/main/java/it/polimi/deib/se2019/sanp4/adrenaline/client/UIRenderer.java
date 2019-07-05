package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequestVisitor;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Leaderboard;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.util.Collection;

/**
 * An interface extending ChoiceRequestVisitor describing a class that renders or refreshes the screens shown to the user.
 * It provides methods both to show screens and to refresh UI elements in a granular way
 *
 * @author Lorenzo Farinelli
 */
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
     *
     * @param waitingPlayers The list of names of the connected players
     * @param matchStarting  {@code true} if the match is about to start with the connected players, {@code false} otherwise
     */
    void updateLobby(Collection<String> waitingPlayers, boolean matchStarting);

    /**
     * Prepare the client for the game
     * Triggered when the match starts
     */
    void showMatchScreen();

    /**
     * Shows the user the weapon card he has just drawn
     *
     * @param weapon The object representing the drawn weapon card
     */
    void showDrawnWeapon(WeaponCard weapon);

    /**
     * Shows the user the powerup he has just drawn
     *
     * @param powerup The object representing the drawn powerup card
     */
    void showDrawnPowerup(PowerupCard powerup);

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
     * Shows a screen to tell the user that the client is going to be closed due to a fault
     *
     * @param title   The title of the screen
     * @param message The message to show to the user
     */
    void showPreemptionScreen(String title, String message);

    /**
     * Shows a screen to tell the user that he is about to rejoin the match
     */
    void showRejoinScreen();

    /**
     * Shows a screen to display the final scores and match stats
     */
    void showLeaderBoard();

    /**
     * Updates the content of the displayed leaderboard
     *
     * @param leaderboard The object representing the leaderboard
     */
    void updateLeaderBoard(Leaderboard leaderboard);

    /**
     * Updates the screen when the player is in IDLE mode
     */
    void setIdleScreen();

    /**
     * Updates the screen when the player is playing the current turn
     */
    void setActiveScreen();

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

    /**
     * Refreshes the rendered list of weapon cards contained in each spawn square
     */
    public void refreshSpawnWeapons();

    /**
     * Refreshes the rendered list of weapons owned by each player participating in the match
     */
    public void refreshOwnedWeapons();

    /**
     * Refreshes the rendered list of weapons owned by the user
     */
    public void refreshOwnedPowerups();
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import java.util.ArrayList;
import java.util.List;

/**
 * A lightweight representation of the match in the view
 */
public class MatchView {
    /**
     * The game board
     */
    private BoardView board;

    /**
     * The count of killshots in the killshots track
     */
    private int killshotsCount;

    /**
     * A flag indicating whether the game is in frenzy mode or not
     */
    private boolean frenzy;

    /**
     * The turn that is currently being played
     */
    private PlayerTurnView currentTurn;

    /**
     * The list of players participating in the match
     */
    private List<PlayerView> players;

    public MatchView() {
        //TODO: Check whether to pass the board as a constructor parameter
        this.board = null;
        this.killshotsCount = 0;
        this.frenzy = false;
        this.players = new ArrayList<>();
        this.currentTurn = null;
    }

    /**
     * Retrieves the board used in the match
     *
     * @return The object representing the match
     */
    public BoardView getBoard() {
        return board;
    }

    /**
     * Sets the board used in the match
     * If the provided board is {@code null}, nothing happens
     *
     * @param board The object representing the board
     */
    public void setBoard(BoardView board) {
        if (board != null) {
            this.board = board;
        }
    }

    /**
     * Get the current count of skulls in the killshots track
     *
     * @return The number of skulls
     */
    public int getKillshotsCount() {
        return killshotsCount;
    }

    /**
     * Sets the count of skulls in the killshots track
     * If a negative value is passed, nothing happens
     *
     * @param killshotsCount The number of skulls
     */
    public void setKillshotsCount(int killshotsCount) {
        if (killshotsCount >= 0) {
            this.killshotsCount = killshotsCount;
        }
    }

    /**
     * Determines whether the match is in frenzy mode
     *
     * @return {@code true} if the match is in frenzy mode, {@code false} otherwise
     */
    public boolean isFrenzy() {
        return frenzy;
    }

    /**
     * Sets the flag telling whether the match is in frenzy mode
     *
     * @param frenzy {@code true} if the match is in frenzy mode, {@code false} otherwise
     */
    public void setFrenzy(boolean frenzy) {
        this.frenzy = frenzy;
    }

    /**
     * Retrieves the list of players participating in the match
     *
     * @return The list of objects representing the players
     */
    public List<PlayerView> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Sets the list of players participating in the match
     * If the provided list is null or contains null values, nothing happens
     *
     * @param players The list of objects representing the players
     */
    public void setPlayers(List<PlayerView> players) {
        if (players != null && !players.contains(null)) {
            this.players = players;
        }
    }

    /**
     * Retrieves the player having the provided username
     *
     * @param playerName The username of the player
     * @return The object representing the player, {@code null} if it does not exist or the provided username is {@code null}
     */
    public PlayerView getPlayerByName(String playerName) {
        if (playerName == null) {
            return null;
        }
        return players.stream().filter(player -> player.getName().equals(playerName)).findFirst().orElse(null);
    }

    /**
     * Retrieves the turn that is actually being played in the match
     *
     * @return The object representing the turn
     */
    public PlayerTurnView getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Sets the turn that is currently being played in the match
     * If the provided turn is null, nothing happens
     *
     * @param currentTurn The object representing the turn
     */
    public void setCurrentTurn(PlayerTurnView currentTurn) {
        if (currentTurn != null) {
            this.currentTurn = currentTurn;
        }
    }
}

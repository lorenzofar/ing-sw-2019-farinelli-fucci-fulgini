package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.*;

import java.util.Map;

/**
 * A general update sent when a player connects to the server
 */
public class InitialUpdate extends ModelUpdate {

    private static final long serialVersionUID = 8708056761273440582L;

    private Map<String, PlayerView> players;
    private Map<String, PlayerBoardView> playerBoards;
    private Map<String, ActionCardView> actionCards;
    private MatchView match;
    private BoardView board;
    private PlayerTurnView currentTurn;

    public Map<String, PlayerView> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, PlayerView> players) {
        this.players = players;
    }

    public Map<String, PlayerBoardView> getPlayerBoards() {
        return playerBoards;
    }

    public void setPlayerBoards(Map<String, PlayerBoardView> playerBoards) {
        this.playerBoards = playerBoards;
    }

    public Map<String, ActionCardView> getActionCards() {
        return actionCards;
    }

    public void setActionCards(Map<String, ActionCardView> actionCards) {
        this.actionCards = actionCards;
    }

    public MatchView getMatch() {
        return match;
    }

    public void setMatch(MatchView match) {
        this.match = match;
    }

    public BoardView getBoard() {
        return board;
    }

    public void setBoard(BoardView board) {
        this.board = board;
    }

    public PlayerTurnView getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(PlayerTurnView currentTurn) {
        this.currentTurn = currentTurn;
    }

    /**
     * Creates a new initial update
     * @param players the players names and their respective views
     * @param playerBoards the players names and their respective player board views
     * @param actionCards the players names and their respective action cards
     * @param match the match view
     * @param board the board view
     * @param currentTurn the current turn view, null if there is not a turn yet
     */
    @JsonCreator
    public InitialUpdate(
            @JsonProperty("players") Map<String, PlayerView> players,
            @JsonProperty("playerBoards") Map<String, PlayerBoardView> playerBoards,
            @JsonProperty("actionCards") Map<String, ActionCardView> actionCards,
            @JsonProperty("match") MatchView match,
            @JsonProperty("board") BoardView board,
            @JsonProperty("currentTurn") PlayerTurnView currentTurn) {
        this.players = players;
        this.playerBoards = playerBoards;
        this.actionCards = actionCards;
        this.match = match;
        this.board = board;
        this.currentTurn = currentTurn;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

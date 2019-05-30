package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.*;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A class responsible of managing the local model and of handling model updates
 */
public class ModelManager implements ModelUpdateVisitor {

    private ClientView clientView;

    private Map<String, PlayerView> players;
    private Map<String, PlayerBoardView> playerBoards;
    private Map<String, ActionCardView> actionCards;
    private MatchView match;
    private BoardView board;
    private PlayerTurnView currentTurn;

    public ModelManager(ClientView clientView) {
        this.clientView = clientView;
        players = new HashMap<>();
        playerBoards = new HashMap<>();
        actionCards = new HashMap<>();
    }

    public Map<String, PlayerView> getPlayers() {
        return players;
    }

    public Map<String, ColoredObject> getPlayersColors() {
        return players.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getColor()));
    }

    public void setPlayers(Map<String, PlayerView> players) {
        if (players != null && !players.entrySet().contains(null)) {
            this.players = players;
        }
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

    @Override
    public void handle(AddedWeaponUpdate update) {
        //TODO: Implement this method
    }

    @Override
    public void handle(DamageUpdate update) {
        //TODO: Implement this method
    }

    @Override
    public void handle(KillUpdate update) {
        PlayerBoardView playerBoardView = playerBoards.get(update.getKilled());
        if (playerBoardView == null) {
            return;
        }
        playerBoardView.setDeaths(update.getDeaths());
        //TODO: Notify players about killing
    }

    @Override
    public void handle(LobbyUpdate update) {
        clientView.getRenderer().updateLobby(update.getWaitingPlayers(), update.isStarting());
    }

    @Override
    public void handle(OverkillUpdate update) {
        //TODO: Implement this method
    }

    @Override
    public void handle(PlayerMoveUpdate update) {
        if (update.getStart() != null) {
            SquareView start = board.getSquare(update.getStart());
            if (start == null) {
                return;
            }
            start.removePlayer(update.getPlayer());
        }
        SquareView end = board.getSquare(update.getEnd());
        if (end == null) {
            return;
        }
        end.addPlayer(update.getPlayer());
        //TODO: Check whether to refresh something
    }

    @Override
    public void handle(ReloadUpdate update) {
        //TODO: Implement this method
    }

    @Override
    public void handle(RemovedWeaponUpdate update) {
        //TODO: Implement this method
    }

    @Override
    public void handle(PlayerUpdate update) {
        // Retrieve the player view associated to the player
        PlayerView playerView = players.get(update.getPlayer().getName());
        if (playerView == null) {
            return;
        }
        // Set properties according to the received update
        playerView.setAmmo(update.getPlayer().getAmmo());
        playerView.setPowerups(update.getPlayer().getPowerups());
        playerView.setWeapons(update.getPlayer().getWeapons());
        //TODO: Check whether to refresh something
    }

    @Override
    public void handle(ActionCardUpdate update) {
        ActionCardView actionCardView = actionCards.get(update.getPlayer());
        if (actionCardView == null) {
            return;
        }
        actionCardView.setActions(update.getActionCard().getActions());
        actionCardView.setFinalAction(update.getActionCard().getFinalAction());
        actionCardView.setType(update.getActionCard().getType());
        //TODO: Check whether to refresh something
    }

    @Override
    public void handle(SquareUpdate update) {
        SquareView square = board.getSquare(update.getSquare().getLocation());
        if (square == null) {
            return;
        }
        square.setAdjacentMap(update.getSquare().getAdjacentMap());
        square.setPlayers(update.getSquare().getPlayers());
        //TODO: Refresh the board
    }

    @Override
    public void handle(BoardUpdate update) {
        board.setSquares(update.getBoard().getSquares());
        //TODO: Refresh the displayed board
    }

    @Override
    public void handle(MatchUpdate update) {
        match.setFrenzy(update.getMatch().isFrenzy());
        match.setKillshotsCount(update.getMatch().getKillshotsCount());
        match.setTotalSkulls(update.getMatch().getTotalSkulls());
        //TODO Refresh the match screen
    }

    @Override
    public void handle(PlayerBoardUpdate update) {
        PlayerBoardView playerBoardView = playerBoards.get(update.getPlayer());
        if (playerBoardView == null) {
            return;
        }
        playerBoardView.setDeaths(update.getPlayerBoard().getDeaths());
        playerBoardView.setDamages(update.getPlayerBoard().getDamages());
        playerBoardView.setMarks(update.getPlayerBoard().getMarks());
        playerBoardView.setState(update.getPlayerBoard().getState());
        //TODO: Refresh the player board
    }

    @Override
    public void handle(DrawnWeaponUpdate update) {
        //TODO: Implement this method
    }

    @Override
    public void handle(DrawnPowerupUpdate update) {
        //TODO: Implement this method
    }
}

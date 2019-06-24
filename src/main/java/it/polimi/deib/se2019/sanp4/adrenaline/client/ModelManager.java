package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.*;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A class responsible of managing the local model and of handling model updates
 */
public class ModelManager implements ModelUpdateVisitor {

    private Map<String, PlayerView> players;
    private Map<String, PlayerBoardView> playerBoards;
    private Map<String, ActionCardView> actionCards;
    private MatchView match;
    private BoardView board;
    private PlayerTurnView currentTurn;

    ModelManager() {
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

    public Map<String, PlayerBoardView> getPlayerBoards() {
        return playerBoards;
    }

    public PlayerTurnView getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(PlayerTurnView currentTurn) {
        this.currentTurn = currentTurn;
    }

    public ActionCardView getActionCard(String player) {
        return actionCards.getOrDefault(player, null);
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
    }

    @Override
    public void handle(LobbyUpdate update) {
        // Do nothing, since this update is only used to render the list of connected players
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
        playerView.setState(update.getPlayer().getState());
        playerView.setScore(update.getPlayer().getScore());
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
    }

    @Override
    public void handle(SquareUpdate update) {
        SquareView square = board.getSquare(update.getSquare().getLocation());
        if (square == null) {
            return;
        }
        square.setAdjacentMap(update.getSquare().getAdjacentMap());
        square.setPlayers(update.getSquare().getPlayers());
        // Check whether the square is a spawn or ammo square
        if (square.printTypeMarker().equals("S")) {
            ((SpawnSquareView) square).setWeapons(((SpawnSquareView) update.getSquare()).getWeapons());
        } else {
            ((AmmoSquareView) square).setAmmoCard(((AmmoSquareView) update.getSquare()).getAmmoCard());
        }
    }

    @Override
    public void handle(BoardUpdate update) {
        board.setSquares(update.getBoard().getSquares());
        board.setSpawnPoints(update.getBoard().getSpawnPoints());
    }

    @Override
    public void handle(MatchUpdate update) {
        match.setFrenzy(update.getMatch().isFrenzy());
        match.setKillshotsCount(update.getMatch().getKillshotsCount());
        match.setTotalSkulls(update.getMatch().getTotalSkulls());
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
    }

    @Override
    public void handle(DrawnWeaponUpdate update) {
        //TODO: Implement this method
    }

    @Override
    public void handle(DrawnPowerupUpdate update) {
        //TODO: Implement this method
    }

    @Override
    public void handle(InitialUpdate update) {
        this.actionCards = update.getActionCards();
        this.board = update.getBoard();
        this.currentTurn = update.getCurrentTurn();
        this.match = update.getMatch();
        this.players = update.getPlayers();
        this.playerBoards = update.getPlayerBoards();
    }

    @Override
    public void handle(PlayerTurnUpdate update) {
        currentTurn.setRemainingActions(update.getPlayerTurn().getRemainingActions());
        currentTurn.setPlayer(update.getPlayerTurn().getPlayer());
        currentTurn.setState(update.getPlayerTurn().getState());
    }

    @Override
    public void handle(WeaponCardUpdate update) {
        // First determine who is the owner of the weapon card
        Optional<String> weaponCardOwner = players.entrySet().stream().filter(entry ->
                entry.getValue().getWeapons().contains(update.getWeaponCard())
        ).map(Map.Entry::getKey).findFirst();
        if (!weaponCardOwner.isPresent()) {
            // The weapon card does not belong to anyone
            return;
        }
        // We then retrieve the object representing the card and update its state according to the received update
        List<WeaponCard> ownerWeapons = players.get(weaponCardOwner.get()).getWeapons();
        WeaponCard weaponCard = ownerWeapons.get(ownerWeapons.indexOf(update.getWeaponCard()));
        weaponCard.setState(update.getWeaponCard().getState());
    }
}

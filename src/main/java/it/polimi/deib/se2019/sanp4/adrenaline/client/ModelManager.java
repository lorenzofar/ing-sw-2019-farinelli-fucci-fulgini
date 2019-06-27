package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.*;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCreator;

import java.io.IOException;
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

    public ActionCardView getActionCard(String player) {
        return actionCards.getOrDefault(player, null);
    }

    @Override
    public void handle(LobbyUpdate update) {
        // Do nothing, since this update is only used to render the list of connected players
    }

    @Override
    public void handle(ReloadUpdate update) {
        //TODO: Implement this method
    }

    @Override
    public void handle(PlayerUpdate update) {
        // Retrieve the player view associated to the player
        if (players.containsKey(update.getPlayer().getName())) {
            players.put(update.getPlayer().getName(), update.getPlayer());
        }
    }

    @Override
    public void handle(ActionCardUpdate update) {
        if (actionCards.containsKey(update.getPlayer())) {
            actionCards.put(update.getPlayer(), update.getActionCard());
        }
    }

    @Override
    public void handle(SquareUpdate update) {
        SquareView square = board.getSquare(update.getSquare().getLocation());
        board.setSquare(square);
    }

    @Override
    public void handle(MatchUpdate update) {
        this.match = update.getMatch();
    }

    @Override
    public void handle(PlayerBoardUpdate update) {
        if (playerBoards.containsKey(update.getPlayer())) {
            playerBoards.put(update.getPlayer(), update.getPlayerBoard());
        }
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
        currentTurn = update.getPlayerTurn();
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

    @Override
    public void handle(MatchOperationalStateUpdate update) {
        //TODO: Implement this method
    }

    @Override
    public void handle(LeaderboardUpdate update) {
        //TODO: Implement this method
    }
}

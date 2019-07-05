package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.*;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Leaderboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A class responsible of managing the local model and of handling model updates
 *
 * @author Lorenzo Farinelli
 */
public class ModelManager implements ModelUpdateVisitor {

    /**
     * The map associating each player username to the corresponding player view
     */
    private Map<String, PlayerView> players;
    /**
     * The map associating each player username to the corresponding player board view
     */
    private Map<String, PlayerBoardView> playerBoards;

    /**
     * The map associating each player username to the corresponding action card view
     */
    private Map<String, ActionCardView> actionCards;
    /**
     * The view of the match
     */
    private MatchView match;
    /**
     * The view of the game board
     */
    private BoardView board;
    /**
     * THe view of the current turn
     */
    private PlayerTurnView currentTurn;
    /**
     * The leaderboard of the game, it will be {@code null} until a LeaderboardUpdate is received
     */
    private Leaderboard leaderboard;

    ModelManager() {
        players = new HashMap<>();
        playerBoards = new HashMap<>();
        actionCards = new HashMap<>();
    }

    /**
     * Retrieves the player view of the players in the match
     *
     * @return A map associating each player username to the corresponding player view
     */
    public synchronized Map<String, PlayerView> getPlayers() {
        return new HashMap<>(players);
    }

    /**
     * Retrieves the colors of the players in the match
     *
     * @return A map associating each player username to an object representing its color
     */
    public synchronized Map<String, ColoredObject> getPlayersColors() {
        return players.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getColor()));
    }

    /**
     * Sets the player views of the players in the match
     *
     * @param players A map associating each player username to the corresponding player view
     */
    public synchronized void setPlayers(Map<String, PlayerView> players) {
        if (players != null && !players.entrySet().contains(null)) {
            this.players = players;
        }
    }

    /**
     * Retrieves the view of the match
     *
     * @return The object representing the view of the match
     */
    public synchronized MatchView getMatch() {
        return match;
    }

    /**
     * Sets the view of the match
     *
     * @param match The object representing the view of the match
     */
    public synchronized void setMatch(MatchView match) {
        this.match = match;
    }

    /**
     * Retrieves the view of the game board
     *
     * @return The object representing the view of the game board
     */
    public synchronized BoardView getBoard() {
        return board;
    }

    /**
     * Sets the view of the game board
     *
     * @param board The object representing the view of the game board
     */
    public synchronized void setBoard(BoardView board) {
        this.board = board;
    }

    /**
     * Sets the player board views of the players in the match
     *
     * @return A map associating each player username to the corresponding player board view
     */
    public synchronized Map<String, PlayerBoardView> getPlayerBoards() {
        return new HashMap<>(playerBoards);
    }

    /**
     * Sets the view of the current turn
     *
     * @return The object representing the turn view
     */
    public synchronized PlayerTurnView getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Retrieves the view of the action card owned by the provided player
     *
     * @param player The username of the player
     * @return The object representing the action card view
     */
    public synchronized ActionCardView getActionCard(String player) {
        return actionCards.getOrDefault(player, null);
    }

    /**
     * Retrieves the leaderboard of the match
     *
     * @return The object representing the leaderboard
     */
    public synchronized Leaderboard getLeaderboard() {
        return leaderboard;
    }

    @Override
    public synchronized void handle(LobbyUpdate update) {
        // Do nothing, since this update is only used to render the list of connected players
    }

    @Override
    public synchronized void handle(PlayerUpdate update) {
        // Retrieve the player view associated to the player
        if (players.containsKey(update.getPlayer().getName())) {
            players.put(update.getPlayer().getName(), update.getPlayer());
        }
    }

    @Override
    public synchronized void handle(ActionCardUpdate update) {
        if (actionCards.containsKey(update.getPlayer())) {
            actionCards.put(update.getPlayer(), update.getActionCard());
        }
    }

    @Override
    public synchronized void handle(SquareUpdate update) {
        board.setSquare(update.getSquare());
    }

    @Override
    public synchronized void handle(MatchUpdate update) {
        this.match = update.getMatch();
    }

    @Override
    public synchronized void handle(PlayerBoardUpdate update) {
        if (playerBoards.containsKey(update.getPlayer())) {
            playerBoards.put(update.getPlayer(), update.getPlayerBoard());
        }
    }

    @Override
    public synchronized void handle(DrawnWeaponUpdate update) {
        // Here we do nothing since we do not have to update anything
    }

    @Override
    public synchronized void handle(DrawnPowerupUpdate update) {
        // Here we do nothing since we do not have to update anything
    }

    @Override
    public synchronized void handle(InitialUpdate update) {
        this.actionCards = update.getActionCards();
        this.board = update.getBoard();
        this.currentTurn = update.getCurrentTurn();
        this.match = update.getMatch();
        this.players = update.getPlayers();
        this.playerBoards = update.getPlayerBoards();
    }

    @Override
    public synchronized void handle(PlayerTurnUpdate update) {
        currentTurn = update.getPlayerTurn();
    }

    @Override
    public synchronized void handle(WeaponCardUpdate update) {
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
    public synchronized void handle(MatchOperationalStateUpdate update) {
        // We do nothing, since this information is not shown and doesn't need to be updated
    }

    @Override
    public synchronized void handle(LeaderboardUpdate update) {
        this.leaderboard = update.getLeaderboard();
    }
}

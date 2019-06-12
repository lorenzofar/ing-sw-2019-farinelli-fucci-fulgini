package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CardinalDirection;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

public abstract class AbstractWeapon {

    protected final WeaponCard weaponCard;

    protected final Map<String, Player> savedPlayers;

    protected final Map<String, Square> savedSquares;

    protected ShootingDirectionEnum shootingDirection;

    protected CardinalDirection selectedDirection;

    protected final Set<Player> damagedPlayers;

    protected final ControllerFactory factory;

    protected final Match match;

    protected final Map<String, PersistentView> views;

    /* TODO: Add effects */

    /**
     * Creates a new weapon controller, with no effects.
     *
     * @param weaponCard The weapon card associated to this weapon, not null
     * @param match      The match which has to be controlled, not null
     * @param views      The views of the players in the match, not null
     * @param factory    The factory needed to create other controllers, not null
     */
    public AbstractWeapon(WeaponCard weaponCard,
                          Match match, Map<String, PersistentView> views, ControllerFactory factory) {
        /* Initialise with provided values */
        this.weaponCard = weaponCard;
        this.match = match;
        this.views = views;
        this.factory = factory;

        /* Default value */
        this.shootingDirection = ShootingDirectionEnum.ANY;

        /* Empty collections */
        this.savedPlayers = new HashMap<>();
        this.savedSquares = new HashMap<>();
        this.damagedPlayers = new HashSet<>();
    }

    /**
     * Returns the weapon card associated to this weapon controller
     *
     * @return The weapon card associated to this weapon controller
     */
    public WeaponCard getWeaponCard() {
        return weaponCard;
    }

    /**
     * Returns all the saved players corresponding to the ids in given collection
     *
     * @param ids Collection of player target ids, not null
     * @return All the saved players corresponding to the ids in given collection
     */
    public Set<Player> getSavedPlayers(Collection<String> ids) {
        return savedPlayers.entrySet().stream()
                .filter(e -> ids.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    /**
     * Returns the saved player corresponding to given id
     *
     * @param id Identifier of the player target, not null
     * @return The saved player, or null if it doesn't exist
     */
    public Player getSavedPlayer(String id) {
        return savedPlayers.get(id);
    }

    /**
     * Associates the given id to the given player in the map of saved players.
     * If another player was already associated to this id, it will be overwritten
     *
     * @param id     The id of the target, not null
     * @param player The player to be saved, not null
     */
    public void savePlayer(String id, Player player) {
        savedPlayers.put(id, player);
    }

    /**
     * Returns all the saved squares corresponding to the ids in given collection
     *
     * @param ids Collection of square target ids, not null
     * @return All the saved squares corresponding to the ids in given collection
     */
    public Set<Square> getSavedSquares(Collection<String> ids) {
        return savedSquares.entrySet().stream()
                .filter(e -> ids.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    /**
     * Returns the saved square corresponding to given id
     *
     * @param id Identifier of the square target, not null
     * @return The saved square, or null if it doesn't exist
     */
    public Square getSavedSquare(String id) {
        return savedSquares.get(id);
    }

    /**
     * Associates the given id to the given square in the map of saved squares.
     * If another square was already associated to this id, it will be overwritten
     *
     * @param id     The id of the target, not null
     * @param square The square to be saved, not null
     */
    public void saveSquare(String id, Square square) {
        savedSquares.put(id, square);
    }

    /**
     * Returns this weapon's direction constraint
     *
     * @return This weapons direction constraint, not null
     */
    public ShootingDirectionEnum getShootingDirection() {
        return shootingDirection;
    }

    /**
     * Sets this weapon's direction constraint
     *
     * @param shootingDirection The direction constraint, not null
     */
    public void setShootingDirection(ShootingDirectionEnum shootingDirection) {
        this.shootingDirection = shootingDirection;
    }

    /**
     * Returns the selected cardinal direction, if any
     *
     * @return An selected cardinal direction, {@code null} if not selected
     */
    public CardinalDirection getSelectedDirection() {
        return selectedDirection;
    }


    /**
     * Selects the cardinal direction of this weapon.
     * Any previously set direction is overwritten
     *
     * @param direction The direction to be selected, if null sets no direction
     */
    public void selectCardinalDirection(CardinalDirection direction) {
        this.selectedDirection = direction;
    }

    /**
     * Returns an unmodifiable set containing the players who received damage while using this weapon
     *
     * @return An unmodifiable set containing the players who received damage while using this weapon
     */
    public Set<Player> getDamagedPlayers() {
        return damagedPlayers;
    }

    /**
     * Adds given player to the set of damaged players
     *
     * @param player The player to be added, not null
     */
    public void addDamagedPlayer(Player player) {
        damagedPlayers.add(player);
    }

    /**
     * Makes the user with given view use this weapon in the current state of the match
     *
     * @param view The view of the player who wants to use this weapon, not null
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    public abstract void use(PersistentView view) throws InterruptedException;
}

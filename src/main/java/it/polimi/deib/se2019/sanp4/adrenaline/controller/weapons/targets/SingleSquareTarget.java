package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.TargetingEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CardinalDirection;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CancellationException;

import static it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.ShootingDirectionEnum.CARDINAL;

/**
 * Represents a target of {@link TargetingEffect} which stays on a single square.
 * <p>
 *     Provides filters and commands to restrict selection of the square where the actual target resides.
 * </p>
 * These are:
 * <ul>
 *     <li><b>visibility</b>: the visibility constraint of the targeted square from the shooter</li>
 *     <li><b>visibleFromPlayer</b>: the target square must be visible from the player,
 *     saved in the weapon, with specified id</li>
 *     <li><b>minDist</b> and <b>maxDist</b>: minimum and maximum distance (inclusive) of the target square
 *     from the shooter</li>
 *     <li><b>excludePlayers:</b>: players (represented by target ids) that will be excluded from
 *     the targeting scope</li>
 *     <li><b>excludePlayers</b>: squares (represented by their target ids) that will be excluded from the
 *     targeting scope</li>
 *     <li><b>moveShooterHere</b>: will move the shooter in the target square after shooting</li>
 *     <li><b>squareRef</b>: saves the targeted square in the weapon, with the given id</li>
 * </ul>
 */
public abstract class SingleSquareTarget extends AbstractTarget {

    protected Set<String> chooseBetweenTargets;

    protected String visibleFromPlayer;

    protected boolean moveShooterHere;

    protected String squareRef;

    /**
     * Creates a new single square target associated to the given weapon.
     * While the id is set on initialization, the other properties receive their default values,
     * which can be changed later by using setters.
     *
     * @param id      The unique identifier of this target in its effect, not null
     * @param weapon  The weapon controller associated to this target, not null
     * @param match   The match which has to be controlled, not null
     * @param factory The factory to create needed controllers, not null
     */
    public SingleSquareTarget(String id, AbstractWeapon weapon, Match match, ControllerFactory factory) {
        super(id, weapon, match, factory);

        /* Set default values */
        chooseBetweenTargets = null; /* Choose between any targets you want */
        visibleFromPlayer = null; /* No constraint */
        moveShooterHere = false; /* Don't move shooter to target square */
        squareRef = null; /* Do not save a reference to target's square */
    }

    /* ====================== GETTERS AND SETTERS ========================= */

    /**
     * Returns the set of target ids from which the shooter can choose his target.
     *
     * @return The set of target ids from which the shooter can choose his target,
     * null if there is no such constraint
     */
    public Set<String> getChooseBetweenTargets() {
        return chooseBetweenTargets;
    }

    /**
     * Sets the set of target ids from which the shooter can choose his target.
     *
     * @param chooseBetweenTargets Set of target ids from which the shooter can choose his target,
     *                             null if no such constraint
     */
    public void setChooseBetweenTargets(Set<String> chooseBetweenTargets) {
        this.chooseBetweenTargets = chooseBetweenTargets;
    }

    /**
     * Returns the id of the player target from which this target needs to be visible.
     *
     * @return The id of the player target from which this target needs to be visible,
     * null if there is no such constraint
     */
    public String getVisibleFromPlayer() {
        return visibleFromPlayer;
    }

    /**
     * Sets the id of the player target from which this target needs to be visible.
     *
     * @param visibleFromPlayer The id of the player target from which this target needs to be visible,
     *                          null if no such constraint
     */
    public void setVisibleFromPlayer(String visibleFromPlayer) {
        this.visibleFromPlayer = visibleFromPlayer;
    }

    /**
     * Returns whether the shooter will be moved to the target's square after shooting or not.
     *
     * @return {@code true} if the shooter will be moved to the target's square after shooting, {@code false} otherwise
     */
    public boolean isMoveShooterHere() {
        return moveShooterHere;
    }

    /**
     * Sets whether the shooter has to be moved to the target's square after shooting or not.
     *
     * @param moveShooterHere whether the shooter has to be moved to the target's square after shooting or not
     */
    public void setMoveShooterHere(boolean moveShooterHere) {
        this.moveShooterHere = moveShooterHere;
    }

    /**
     * Returns the id with which the target's square will be saved in the weapon.
     *
     * @return The id with which the target's square will be saved in the weapon, null if it won't be saved.
     */
    public String getSquareRef() {
        return squareRef;
    }

    /**
     * Sets the id with which the target's square has to be saved in the weapon.
     *
     * @param squareRef The id with which the target's square has to be saved in the weapon,
     *                  null if it doesn't have to be saved.
     */
    public void setSquareRef(String squareRef) {
        this.squareRef = squareRef;
    }

    /* ====================== UTILITY ========================= */

    /**
     * Determines and returns the squares which are selectable for
     * targeting according to the following constraints:
     * <ul>
     * <li>Direction of the weapon (if already set)</li>
     * <li>Visibility</li>
     * <li>Minimum and maximum distance</li>
     * <li>Squares to be excluded via {@code excludeSquares}</li>
     * <li>Visibility from specified player target ({@code visibleFromPlayer})</li>
     * </ul>
     *
     * @param start The square from which to calculate distances, not null
     * @return The selectable squares according to given criteria
     */
    protected Set<Square> determineSelectableSquares(Square start) {
        /* Get a hook to the board */
        Board board = match.getBoard();

        Set<Square> query = new HashSet<>();

        /* Handle the direction */
        CardinalDirection direction = weapon.getSelectedDirection();
        if (weapon.getShootingDirection() == CARDINAL && direction == null) {
            /* Then we query squares in straight direction in all the directions */
            for (CardinalDirection d : CardinalDirection.values()) {
                query.addAll(board.querySquares(start, visibility, d, minDist, maxDist));
            }
        } else {
            /* We query squares in all directions or in the selected direction */
            query.addAll(board.querySquares(start, visibility, direction, minDist, maxDist));
        }

        /* Exclude specified squares */
        Set<Square> toExclude = weapon.getSavedSquares(excludeSquares);

        query.removeAll(toExclude);

        /* Intersect with squares visible from specified player */
        if (visibleFromPlayer != null) {
            /* Get the player associated to that target */
            Player him = weapon.getSavedPlayer(visibleFromPlayer);

            /* Determine the squares he can see */
            Set<Square> visibleFromHim;
            if (him != null) {
                /* Determine the squares he can see */
                visibleFromHim = board.getVisibleSquares(him.getCurrentSquare());
            } else {
                visibleFromHim = new HashSet<>();
            }

            /* Intersect them with the ones we already have */
            query.retainAll(visibleFromHim);
        }

        return query;
    }

    /* ====================== EXECUTE ========================= */

    /**
     * Executes this target given the user of the weapon
     *
     * @param view The view of the player using the weapon
     * @return {@code true} if the player chose his target and was able to perform damage/marks
     * {@code false} if the player chose not to use the target
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    @Override
    public abstract boolean execute(PersistentView view) throws InterruptedException;
}

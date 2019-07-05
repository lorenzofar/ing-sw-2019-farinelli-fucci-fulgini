package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.TargetingEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CardinalDirection;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.ShootingDirectionEnum.CARDINAL;

/**
 * Represents a target of {@link TargetingEffect} where all the players in a square get the same damage and marks.
 * <p>
 *     The square must meet all the requirements of {@link SingleSquareTarget}, players that are excluded from
 *     the targeting scope will not get damage/marks.
 * </p>
 * <p>
 *     After execution, the targeted square is saved in the weapon with the target's id.
 * </p>
 * @author Alessandro Fulgini
 */
public class SquareTarget extends SingleSquareTarget {

    private static final String MESSAGE_SELECT_SQUARE_TARGET = "Select the square you want to target" +
            " (%d damage, %d marks)";

    private final Board board;

    /**
     * Creates a new square target associated to the given weapon.
     * While the id is set on initialization, the other properties receive their default values,
     * which can be changed later by using setters.
     *
     * @param id      The unique identifier of this target in its effect, not null
     * @param weapon  The weapon controller associated to this target, not null
     * @param match   The match which has to be controlled, not null
     * @param factory The factory to create needed controllers, not null
     */
    public SquareTarget(String id, AbstractWeapon weapon, Match match, ControllerFactory factory) {
        super(id, weapon, match, factory);
        board = match.getBoard();
    }

    /* ====================== UTILITIES ========================= */

    /**
     * Determines which squares can be targeted by the shooter.
     * <p>
     *     If a target with the same id is already saved in the weapon,
     *     it is immediately returned.
     *     If there is not a pre-saved target, the algorithm will determine
     *     all selectable squares by applying all the needed filters.
     * </p>
     * @param shooter The player using the weapon, not null
     * @return A set with the selectable squares
     */
    private Set<Square> determineSelectableTargets(Player shooter) {
        /* Check if the target already exists */
        Square forcedTarget = weapon.getSavedSquare(this.id);
        if (forcedTarget != null) {
            return Collections.singleton(forcedTarget);
        }

        /* Prepare filter for chooseBetweenTargets */
        Predicate<Square> chooseBetweenFilter;
        if (chooseBetweenTargets != null) {
            /* Prepare a set with the selectable squares */
            Set<Square> toRetain = weapon.getSavedSquares(chooseBetweenTargets);
            /* Prepare the predicate */
            chooseBetweenFilter = toRetain::contains;
        } else {
            chooseBetweenFilter = s -> true; /* All ok */
        }

        /* Prepare filter for squares without targetable players */
        Set<Player> untargetablePlayers = determineUntargetablePlayers(shooter);
        Predicate<Square> targetablePlayersFilter = s -> {
            Set<Player> ps = new HashSet<>(s.getPlayers()); /* Get players in square */
            ps.removeAll(untargetablePlayers); /* Remove untargetable */
            return !ps.isEmpty(); /* Check if anyone remains */
        };

        /* Determine selectable squares and filter the ones with at least one targetable player */
        return determineSelectableSquares(shooter.getCurrentSquare()).stream()
                .filter(chooseBetweenFilter) /* Filter for chooseBetweenTargets */
                .filter(targetablePlayersFilter) /* Only retain if they have players */
                .collect(Collectors.toSet());
    }

    /**
     * Given the set of possible targets, handles the selection:
     * <p>
     *     If the target is mandatory and there is only one selectable square,
     *     then the selection is automatic.
     *     If the target is optional, the user is asked to select a square even if
     *     only one is selectable.
     * </p>
     * <p>
     *     If the weapon is directional and the direction is not set, this will also set the direction
     *     of the selected square.
     * </p>
     *
     * @param view The view of the shooter, not null
     * @param choices The targets from which the user can choose from, not null
     * @return The selected square, null if no square was/could be selected
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    private Square handleTargetSelection(PersistentView view, Set<Square> choices) throws InterruptedException {
        Square selectedSquare;
        List<Square> selectableSquares = new ArrayList<>(choices);
        Player shooter = match.getPlayerByName(view.getUsername());

        /* Ask the user to select or auto-select */
        int selectableCount = selectableSquares.size();
        if (selectableCount == 0) {
            selectedSquare = null; /* No selection */
        } else if (selectableCount > 1 || optional) {
            /* Ask the player to select a square */
            selectedSquare = askToSelectSquare(view, selectableSquares);
        } else {
            /* There is only one square and it's mandatory */
            selectedSquare = selectableSquares.get(0);
        }

        /* Handle directional weapons */
        if (selectedSquare != null
                && weapon.getShootingDirection() == CARDINAL
                && weapon.getSelectedDirection() == null) {

            /* Set the selected direction, if any */
            CardinalDirection selectedDirection = Board.calculateAlignedDirection(
                    shooter.getCurrentSquare().getLocation(),
                    selectedSquare.getLocation()
            );
            weapon.selectCardinalDirection(selectedDirection);
        }

        return selectedSquare;
    }

    /**
     * Asks the given user to select one or no square to target, depending on the target being <i>optional</i>
     * or not
     *
     * @param view The view of the user, not null
     * @param choices The possible choices, not null
     * @return The user's choice, null if no choice
     * @throws CancellationException if the request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    private Square askToSelectSquare(PersistentView view, List<Square> choices) throws InterruptedException {
        /* Map to locations */
        List<CoordPair> coordPairs = choices.stream().map(Square::getLocation).collect(Collectors.toList());
        /* Send request */
        SquareRequest req = new SquareRequest(
                String.format(MESSAGE_SELECT_SQUARE_TARGET, damage, marks),coordPairs, this.optional);
        CoordPair selected = view.sendChoiceRequest(req).get();

        /* Map back to square */
        if (selected != null) {
            return board.getSquare(selected);
        } else {
            return null; /* No selection */
        }
    }

    /**
     * Applies the damages and marks to the targetable players in the given square.
     * A player in the square is targetable:
     * <ul>
     *     <li>If he's not excluded via {@code excludePlayers}</li>
     *     <li>If he's not the shooter</li>
     * </ul>
     * The attribute {@code shooter} must be set before calling this
     * @param square The target square, not null
     * @param shooter The player using the weapon, not null
     */
    private void applyDamageAndMarks(Square square, Player shooter) {
        Set<Player> players = new LinkedHashSet<>(square.getPlayers());

        /* Exclude players who can't be targeted */
        players.removeAll(determineUntargetablePlayers(shooter));

        /* Apply damage and marks to the remaining players */
        players.forEach(p -> applyDamageAndMarks(p, shooter));
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
    public boolean execute(PersistentView view) throws InterruptedException {
        Player shooter = match.getPlayerByName(view.getUsername());

        /* Determine which squares can be targeted */
        Set<Square> selectableTargets = determineSelectableTargets(shooter);

        /* Ask the user to select the square or auto-select */
        Square selectedTarget = handleTargetSelection(view, selectableTargets);

        /* No selection */
        if (selectedTarget == null) {
            return false;
        }

        /* Give marks and damage to the players in the square */
        applyDamageAndMarks(selectedTarget, shooter);

        /* Move shooter if required */
        if (moveShooterHere) {
            board.movePlayer(shooter, selectedTarget);
        }

        /* Save target reference */
        weapon.saveSquare(this.id, selectedTarget);

        /* Save additional reference if required */
        if (squareRef != null) {
            weapon.saveSquare(squareRef, selectedTarget);
        }

        return true;
    }
}

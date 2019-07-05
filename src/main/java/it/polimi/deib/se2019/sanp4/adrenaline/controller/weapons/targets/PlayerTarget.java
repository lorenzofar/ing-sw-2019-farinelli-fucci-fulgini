package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PlayerRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.TargetingEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.ShootingDirectionEnum.CARDINAL;
import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum.ANY;

/**
 * Represents a single player target in a {@link TargetingEffect}.
 * <p>
 * When this target has been executed, the targeted player is saved in the weapon with the target's id.
 * If a player with the same target id is already saved in the weapon, this gets automatically selected
 * as a target.
 * </p>
 * <p>
 * This type of targets provides all the commands and filters of {@link SingleSquareTarget},
 * which are applied to the square where the player actually gets shot, plus two other commands.
 * </p>
 * <p>
 * The additional commands are {@code movePlayerBefore} and {@code movePlayerAfter}.
 * These specify the amount of steps that the targeted player can be moved before/after getting damaged.
 * In case of {@code movePlayerBefore}, the selected target must land on a targetable square, according
 * to the filters of {@link SingleSquareTarget}, and the movement is free (i.e. not constrained to the
 * shooting direction.
 * In case of {@code movePlayerAfter}, the movement is constrained to the shooting direction.
 * </p>
 * @author Alessandro Fulgini
 */
public class PlayerTarget extends SingleSquareTarget {

    private static final String MESSAGE_SELECT_PLAYER_TARGET = "Select the player you want to target" +
            " (%d damage, %d marks)";

    private static final String MESSAGE_SELECT_MOVE_BEFORE_DESTINATION = "Move %s to a square before shooting";

    private static final String MESSAGE_SELECT_MOVE_AFTER_DESTINATION = "You can move %s to a square";

    protected int moveTargetBefore;

    protected int moveTargetAfter;

    protected Board board;

    /**
     * Creates a new player target associated to the given weapon.
     * While the id is set on initialization, the other properties receive their default values,
     * which can be changed later by using setters.
     *
     * @param id      The unique identifier of this target in its effect, not null
     * @param weapon  The weapon controller associated to this target, not null
     * @param match   The match which has to be controlled, not null
     * @param factory The factory to create needed controllers, not null
     */
    public PlayerTarget(String id, AbstractWeapon weapon, Match match, ControllerFactory factory) {
        super(id, weapon, match, factory);
        board = match.getBoard();

        /* Set default values */
        this.moveTargetBefore = this.moveTargetAfter = 0; /* Do not allow to move the target */
    }

    /* ====================== GETTERS AND SETTERS ========================= */


    /**
     * Returns the amount of steps the user can move the target before shooting.
     *
     * @return The amount of steps the user can move the target before shooting.
     */
    public int getMoveTargetBefore() {
        return moveTargetBefore;
    }

    /**
     * Sets the amount of steps the user can move the target before shooting.
     *
     * @param moveTargetBefore The amount of steps the user can move the target before shooting, non-negative,
     *                         zero if it can't be moved
     */
    public void setMoveTargetBefore(int moveTargetBefore) {
        this.moveTargetBefore = moveTargetBefore;
    }

    /**
     * Returns the amount of steps the user can move the target after shooting.
     *
     * @return The amount of steps the user can move the target after shooting.
     */
    public int getMoveTargetAfter() {
        return moveTargetAfter;
    }

    /**
     * Sets the amount of steps the user can move the target after shooting.
     *
     * @param moveTargetAfter The amount of steps the user can move the target after shooting, non-negative,
     *                        zero if it can't be moved
     */
    public void setMoveTargetAfter(int moveTargetAfter) {
        this.moveTargetAfter = moveTargetAfter;
    }

    /* ===================== UTILITIES ======================== */

    /**
     * Determines the targets that can be selected from the player
     *
     * @param shooter The player using the weapon, not null
     * @return A set of selectable targets
     */
    private Set<Player> determineSelectableTargets(Player shooter) {

        /* Check if the target already exists */
        Player forcedSelection = weapon.getSavedPlayer(this.id);
        if (forcedSelection != null) {
            return Collections.singleton(forcedSelection);
        }

        /* Determine the squares where the player can shoot */
        Set<Square> selectableSquares = determineSelectableSquares(shooter.getCurrentSquare());

        /* Compute an initial set of targetable players */
        Stream<Player> targetStream;
        if (moveTargetBefore > 0) {
            /* Start from all players in any square */
            targetStream = match.getPlayers().stream()
                    /* Only keep players that are on a square */
                    .filter(p -> p.getCurrentSquare() != null);
        } else {
            /* Start from players in selectable squares */
            targetStream = selectableSquares.stream()
                    /* Map to players in the square */
                    .flatMap(s -> s.getPlayers().stream());
        }

        /* Prepare filter for chooseBetweenTargets */
        Predicate<Player> chooseBetweenFilter;
        if (chooseBetweenTargets != null) {
            /* Prepare a set with the selectable players */
            Set<Player> toRetain = weapon.getSavedPlayers(chooseBetweenTargets);
            /* Prepare the predicate */
            chooseBetweenFilter = toRetain::contains;
        } else {
            chooseBetweenFilter = p -> true; /* All ok */
        }

        /* Apply more filters */
        targetStream = targetStream
                /* Remove explicitly excluded and shooter */
                .filter(p -> !determineUntargetablePlayers(shooter).contains(p))
                /* Only keep the ones he is obliged to select from */
                .filter(chooseBetweenFilter);

        /* Check that the remaining targets can be moved, if necessary */
        if (moveTargetBefore > 0) {
            /* Selectable players must be movable to a selectable square in max. given number of steps */
            /* The direction and visibility don't count */
            targetStream = targetStream
                    .filter(p -> {
                        Set<Square> fromTarget = board
                                .querySquares(p.getCurrentSquare(), ANY, null, null, moveTargetBefore);
                        fromTarget.retainAll(selectableSquares); /* Intersect */
                        return !fromTarget.isEmpty();
                    });
        }

        /* Collect the remaining targets */
        return targetStream.collect(Collectors.toSet());
    }

    /**
     * Given the set of possible targets, handles the selection:
     * <p>
     * If the target is mandatory and there is only one selectable player,
     * then the selection is automatic.
     * If the target is optional, the user is asked to select a player even if
     * only one is selectable.
     * </p>
     * <p>
     * This won't set the direction of the weapon, since the target may be moved after selection.
     * </p>
     *
     * @param view    The view of the shooter, not null
     * @param choices The targets from which the user can choose from, not null
     * @return The selected square, null if no square was/could be selected
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    private Player handleTargetSelection(PersistentView view, Set<Player> choices) throws InterruptedException {
        Player selectedPlayer;
        List<Player> selectablePlayers = new ArrayList<>(choices);

        /* Ask the user to select or auto-select */
        int selectableCount = selectablePlayers.size();
        if (selectableCount == 0) {
            selectedPlayer = null; /* No selection */
        } else if (selectableCount > 1 || optional) {
            /* Ask the player to select a square */
            selectedPlayer = askToSelectPlayer(view, selectablePlayers);
        } else {
            /* There is only one square and it's mandatory */
            selectedPlayer = selectablePlayers.get(0);
        }

        return selectedPlayer;
    }

    /**
     * Asks the given user to select one or no player to target, depending on the target being <i>optional</i>
     * or not
     *
     * @param view    The view of the user, not null
     * @param choices The possible choices, not null
     * @return The user's choice, null if no choice
     * @throws CancellationException if the request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    private Player askToSelectPlayer(PersistentView view, List<Player> choices) throws InterruptedException {
        /* Map to names */
        List<String> usernames = choices.stream().map(Player::getName).collect(Collectors.toList());
        /* Send request */
        PlayerRequest req = new PlayerRequest(
                String.format(MESSAGE_SELECT_PLAYER_TARGET, damage, marks), usernames, this.optional);
        String selected = view.sendChoiceRequest(req).get();

        /* Map back to player */
        if (selected != null) {
            return match.getPlayerByName(selected);
        } else {
            return null; /* No selection */
        }
    }

    /**
     * Determine the squares where the target can be moved and asks the player
     * if he wants to move it there.
     * This method should only be called if {@code moveTargetBefore &gt; 0}
     * and if the target can actually be moved to a square where he can be shot
     * in max. {@code moveTargetBefore} steps.
     *
     * @param view   The view of the player using the weapon, not null
     * @param target The selected target, not null
     * @throws CancellationException if the request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    private void handleMoveTargetBefore(PersistentView view, Player target) throws InterruptedException {
        Player shooter = match.getPlayerByName(view.getUsername());

        /* Determine the squares where the target can be moved */
        Set<Square> fromTarget = board.querySquares(target.getCurrentSquare(),
                ANY, weapon.getSelectedDirection(), null, moveTargetBefore);

        /* Determine the squares where he can be shot */
        Set<Square> fromShooter = determineSelectableSquares(shooter.getCurrentSquare());

        /* Intersect them */
        fromShooter.retainAll(fromTarget);

        Square destination;
        if (fromShooter.size() == 1) {
            /* Then the target can be moved to only one square in order to be targeted */
            destination = fromShooter.iterator().next(); /* Select it automatically */
        } else {
            /* The target can be moved to more than one square, let the user select it */
            SquareRequest req = new SquareRequest(
                    String.format(MESSAGE_SELECT_MOVE_BEFORE_DESTINATION, target.getName()),
                    fromShooter.stream().map(Square::getLocation).collect(Collectors.toList()),
                    false
            );
            CoordPair selected = view.sendChoiceRequest(req).get();
            destination = board.getSquare(selected);
        }

        /* Move the target to the desired destination */
        board.movePlayer(target, destination);
    }

    /**
     * Determines the squares where the target can be moved after shooting and asks the shooter where he wants to move
     * him.
     * This method should only be called if {@code moveTargetAfter &gt; 0}
     *
     * @param view   The view of the player using the weapon, not null
     * @param target The selected target, not null
     * @throws CancellationException if the request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    private void handleMoveTargetAfter(PersistentView view, Player target) throws InterruptedException {
        /* Determine where the target can be moved */
        Set<Square> fromTarget = board.querySquares(target.getCurrentSquare(),
                ANY, weapon.getSelectedDirection(), null, moveTargetAfter);

        /* If there is only one, it's certainly the square the target is currently in */
        if (fromTarget.size() > 1) {
            /* Ask the user to select */
            SquareRequest req = new SquareRequest(
                    String.format(MESSAGE_SELECT_MOVE_AFTER_DESTINATION, target.getName()),
                    fromTarget.stream().map(Square::getLocation).collect(Collectors.toList()),
                    false
            );
            CoordPair selected = view.sendChoiceRequest(req).get();

            /* Move to selected square */
            board.movePlayer(target, board.getSquare(selected));
        }
    }

    /**
     * If the weapon is directional and the direction has not been set,
     * it selects the shooting direction.
     * The target must be in the square where he has been shot.
     *
     * @param target  The targeted player, in the square where he's been shot, not null
     * @param shooter The player using the weapon, not null
     */
    private void saveShootingDirection(Player target, Player shooter) {
        if (weapon.getShootingDirection() == CARDINAL && weapon.getSelectedDirection() == null) {
            /* Set the selected direction, if any */
            CardinalDirection selectedDirection = Board.calculateAlignedDirection(
                    shooter.getCurrentSquare().getLocation(),
                    target.getCurrentSquare().getLocation()
            );
            weapon.selectCardinalDirection(selectedDirection);
        }
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

        /* Determine which players can be targeted */
        Set<Player> selectableTargets = determineSelectableTargets(shooter);

        /* Ask the user to select the target or auto-select */
        Player selectedTarget = handleTargetSelection(view, selectableTargets);

        /* No selection */
        if (selectedTarget == null) {
            return false;
        }

        /* The user may want to move his target before shooting */
        if (moveTargetBefore > 0) {
            handleMoveTargetBefore(view, selectedTarget);
        }

        /* Now the target is in place to be shot */

        /* Apply damage and marks */
        applyDamageAndMarks(selectedTarget, shooter);

        /* If needed save the shooting direction */
        saveShootingDirection(selectedTarget, shooter);

        /* Move shooter if required */
        if (moveShooterHere) {
            board.movePlayer(shooter, selectedTarget.getCurrentSquare());
        }

        /* Save reference to the square if required */
        if (squareRef != null) {
            weapon.saveSquare(squareRef, selectedTarget.getCurrentSquare());
        }

        /* Now the user may want to move the target after shooting */
        if (moveTargetAfter > 0) {
            handleMoveTargetAfter(view, selectedTarget);
        }

        /* Save target reference */
        weapon.savePlayer(this.id, selectedTarget);

        return true;
    }
}

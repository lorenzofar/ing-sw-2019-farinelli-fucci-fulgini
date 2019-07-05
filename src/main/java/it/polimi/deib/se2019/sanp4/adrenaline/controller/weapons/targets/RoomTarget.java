package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Room;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a weapon target which assigns damage and marks to the players in a single room.
 * <ul>
 * <li>The nearest square of the room must be within {@code minDist} and {@code maxDist} from the shooter.</li>
 * <li>Players in squares listed in {@code excludeSquares} won't receive damage and marks,
 * even if they are in the room</li>
 * <li>Players listed in {@code excludePlayers} won't receive damage and marks,
 * even if they are in the room</li>
 * </ul>
 * @author Alessandro Fulgini
 */
public class RoomTarget extends AbstractTarget {

    private static final String MESSAGE_SELECT_ROOM = "Select a room: the players inside will get %d damage" +
            " and %d marks";

    protected Board board;


    /**
     * Creates a new room target associated to the given weapon.
     * While the id is set on initialization, the other properties receive their default values,
     * which can be changed later by using setters.
     *
     * @param id      The unique identifier of this target in its effect, not null
     * @param weapon  The weapon controller associated to this target, not null
     * @param match   The match which has to be controlled, not null
     * @param factory The factory to create needed controllers, not null
     */
    public RoomTarget(String id, AbstractWeapon weapon, Match match, ControllerFactory factory) {
        super(id, weapon, match, factory);
        board = match.getBoard();
    }

    /* ===================== UTILITIES ======================== */

    /**
     * Determines the targets that can be selected by the player.
     * <p>
     * First, the squares which meet the requirements of {@code minDist} and {@code maxDist}
     * and are determined. The direction is taken into account only if already set.
     * Then the rooms of those squares are determined and only the ones containing targetable players are returned.
     * </p>
     *
     * @param shooter The player using the weapon, not null
     * @return A set of rooms that can be selected
     */
    private Set<Room> determineSelectableTargets(Player shooter) {
        /* This predicate identifies rooms with at least one targetable player */
        Set<Player> untargetablePlayers = determineUntargetablePlayers(shooter);
        Predicate<Room> hasTargetablePlayers = room ->
                /* Check if at least one is targetable */
                room.getPlayers().stream().anyMatch(p -> !untargetablePlayers.contains(p));

        Set<Square> selectableSquares = board.querySquares(shooter.getCurrentSquare(),
                visibility, weapon.getSelectedDirection(), minDist, maxDist);

        return selectableSquares.stream()
                /* Map to the rooms and remove duplicates */
                .map(Square::getRoom)
                .distinct()
                /* Keep only the ones with at least one targetable player */
                .filter(hasTargetablePlayers)
                .collect(Collectors.toSet());
    }

    /**
     * Given the set of possible targets, takes care of the selection process:
     * <p>
     * If the target is mandatory and only one is selectable, the selection is automatic.
     * If it is optional or there are more choices the user is asked to perform the selection.
     * If there are no choices, then {@code null} is returned
     * </p>
     *
     * @param view    The view of the player using the weapon, not null
     * @param choices The possible targets, not null
     * @return The selected choice or {@code null} if no choice has been selected
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    private Room handleTargetSelection(PersistentView view, Set<Room> choices) throws InterruptedException {
        if (choices.isEmpty()) {
            return null;
        } else if (choices.size() > 1 || optional) {
            /* Ask the user to select */
            return askToSelectRoom(view, choices);
        } else {
            /* Automatically select the obliged choice */
            return choices.iterator().next();
        }
    }

    /**
     * Asks the given user to select one or no room to target, depending on the target being <i>optional</i>
     * or not
     *
     * @param view    The view of the player using the weapon, not null
     * @param choices The possible targets, not null
     * @return The selected choice or {@code null} if no choice has been selected
     * @throws CancellationException if the request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    private Room askToSelectRoom(PersistentView view, Set<Room> choices) throws InterruptedException {
        /* Map the rooms to the coordinates of their squares */
        List<CoordPair> squares = choices.stream()
                .flatMap(room -> room.getSquares().stream())
                .map(Square::getLocation)
                .collect(Collectors.toList());

        /* The user will select a square among the given ones */
        SquareRequest req = new SquareRequest(
                String.format(MESSAGE_SELECT_ROOM, damage, marks), squares, optional);
        CoordPair selected = view.sendChoiceRequest(req).get();

        /* No selection */
        if (selected == null) {
            return null;
        }

        /* Find the room containing the square with given location, which is guaranteed to exist */
        return choices.stream()
                .filter(room -> room.getSquares().stream()
                        .anyMatch(s -> s.getLocation().equals(selected)))
                .findAny()
                .orElse(null);
    }

    /**
     * Applies the correct amount of damage and marks to the targetable players in given room.
     * <p>
     * A player may not be targetable for the following reasons:
     * <ul>
     * <li>He's been excluded with {@code excludePlayers}</li>
     * <li>He's in a square excluded with {@code excludeSquares}</li>
     * <li>He's the shooter</li>
     * </ul>
     *
     * @param target  The selected room to target, not null
     * @param shooter The player using the weapon, not null
     */
    private void applyDamageAndMarks(Room target, Player shooter) {
        /* Determine squares and players to exclude */
        Set<Player> playersToExclude = determineUntargetablePlayers(shooter);
        Set<Square> squaresToExclude = weapon.getSavedSquares(excludeSquares);

        /* Determine all the players who will be targeted and apply damage and marks */
        target.getSquares().stream()
                /* Exclude squares */
                .filter(s -> !squaresToExclude.contains(s))
                /* Map to players in squares */
                .flatMap(s -> s.getPlayers().stream())
                /* Exclude players */
                .filter(p -> !playersToExclude.contains(p))
                /* Apply D&M */
                .forEach(player -> applyDamageAndMarks(player, shooter));
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

        /* Determine possible targets */
        Set<Room> selectableTargets = determineSelectableTargets(shooter);

        /* Ask to select one or auto-select */
        Room selectedTarget = handleTargetSelection(view, selectableTargets);

        if (selectedTarget == null) {
            return false; /* No selection */
        }

        /* Apply damage and marks to the players in the room */
        applyDamageAndMarks(selectedTarget, shooter);

        return true; /* The target has been used */
    }
}

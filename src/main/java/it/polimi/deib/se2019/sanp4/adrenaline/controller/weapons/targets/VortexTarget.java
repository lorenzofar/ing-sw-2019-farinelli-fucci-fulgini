package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PlayerRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum.ANY;

/**
 * Represents single player target of the vortex type.
 * <p>
 * The vortex square is saved in the weapon under a special name (i.e. "vortex").
 * If this square is not found when the target is executed, then the shooter is asked to select it.
 * Then the shooter is asked to select a player to be taken into the vortex.
 * The selected player is then saved to the weapon with the id of this target.
 * </p>
 */
public class VortexTarget extends AbstractTarget {

    private static final String MESSAGE_SELECT_VORTEX = "Select a square to be the vortex";

    private static final String MESSAGE_SELECT_PLAYER = "Select the player you want to bring into the vortex";

    private static final String VORTEX_ID = "vortex";

    protected Board board;

    /**
     * Creates a new abstract target associated to the given weapon.
     * While the id is set on initialization, the other properties receive their default values,
     * which can be changed later by using setters.
     *
     * @param id      The unique identifier of this target in its effect, not null
     * @param weapon  The weapon controller associated to this target, not null
     * @param match   The match which has to be controlled, not null
     * @param factory The factory to create needed controllers, not null
     */
    public VortexTarget(String id, AbstractWeapon weapon, Match match, ControllerFactory factory) {
        super(id, weapon, match, factory);
        this.board = match.getBoard();
    }

    /* ===================== UTILITIES ======================== */

    /**
     * Given the shooter, determines which square can be selected as the vortex.
     * Such square must meet the following requirements:
     * <ul>
     * <li>It must not be the shooter's square</li>
     * <li>It must meet the visibility requirement</li>
     * <li>It must be within {@code maxDist} from the shooter</li>
     * <li>At least one targetable player must be within {@code maxDist} from it</li>
     * </ul>
     *
     * @param shooter The player using the weapon, not null
     * @return A set of squares which can be selected as the vortex
     */
    private Set<Square> determineSelectableVortex(Player shooter) {
        /* Determine candidates to be the vortex */
        return board.querySquares(shooter.getCurrentSquare(), visibility, null, 1, maxDist).stream()
                .filter(candidate -> !determineSelectablePlayers(candidate, shooter).isEmpty())
                .collect(Collectors.toSet());
    }

    /**
     * Determines which players can be taken into the vortex and shot.
     * Such players must be within {@code maxDist} from the vortex,
     * must not be in {@code excludePlayers} and must not be the shooter.
     *
     * @param vortex  The vortex square, not null
     * @param shooter The player using the weapon, not null
     * @return The players that can be taken into the vortex
     */
    private Set<Player> determineSelectablePlayers(Square vortex, Player shooter) {
        /* Determine players who can't be targeted */
        Set<Player> untargetablePlayers = determineUntargetablePlayers(shooter);

        return board
                /* Get neighbors */
                .querySquares(vortex, ANY, null, null, maxDist).stream()
                /* Map to the players in the neighbors */
                .flatMap(square -> square.getPlayers().stream())
                /* Filter out untargetable ones */
                .filter(player -> !untargetablePlayers.contains(player))
                .collect(Collectors.toSet());
    }

    /**
     * Given the set of possible targets, handles the selection:
     * <p>
     * If the target is mandatory and there is only one selectable player,
     * then the selection is automatic.
     * If the target is optional, the user is asked to select a player even if
     * only one is selectable.
     * </p>
     *
     * @param view    The view of the shooter, not null
     * @param choices The targets from which the user can choose from, not null
     * @return The selected square, null if no square was/could be selected
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    private Player handlePlayerSelection(PersistentView view, Set<Player> choices) throws InterruptedException {
        if (choices.isEmpty()) return null;
        Player selectedPlayer;
        
        /* Ask the user to select or auto-select */
        if (choices.size() > 1 || optional) {
            /* Ask the player to select a square */
            selectedPlayer = askToSelectPlayer(view, choices);
        } else {
            /* There is only one player and it's mandatory */
            selectedPlayer = choices.iterator().next();
        }

        return selectedPlayer;
    }

    /**
     * Asks the given user to select a square to be the vortex.
     *
     * @param view    The view of the user, not null
     * @param choices The possible choices, not null
     * @return The user's choice, null if no choice
     * @throws CancellationException if the request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    private Square askToSelectVortex(PersistentView view, Set<Square> choices) throws InterruptedException {
        /* Map to locations */
        List<CoordPair> coordPairs = choices.stream().map(Square::getLocation).collect(Collectors.toList());
        /* Send request */
        SquareRequest req = new SquareRequest(MESSAGE_SELECT_VORTEX, coordPairs, false);
        CoordPair selected = view.sendChoiceRequest(req).get();

        return board.getSquare(selected); /* Map back to square */
    }

    /**
     * Asks the given user to select one or no player to bring into the vortex,
     * depending on the target being <i>optional</i> or not
     *
     * @param view    The view of the user, not null
     * @param choices The possible choices, not null
     * @return The user's choice, null if no choice
     * @throws CancellationException if the request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    private Player askToSelectPlayer(PersistentView view, Set<Player> choices) throws InterruptedException {
        /* Map to names */
        List<String> usernames = choices.stream().map(Player::getName).collect(Collectors.toList());
        /* Send request */
        PlayerRequest req = new PlayerRequest(MESSAGE_SELECT_PLAYER, usernames, this.optional);
        String selected = view.sendChoiceRequest(req).get();

        /* Map back to player */
        if (selected != null) {
            return match.getPlayerByName(selected);
        } else {
            return null; /* No selection */
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

        /* Determine if the vortex square has already been selected */
        Square vortex = weapon.getSavedSquare(VORTEX_ID);

        if (vortex == null) {
            /* The user should select the vortex */
            Set<Square> choices = determineSelectableVortex(shooter);

            if (choices.isEmpty()) return false; /* Can't select a vortex */

            vortex = askToSelectVortex(view, choices);

            /* Save the vortex in the weapon */
            weapon.saveSquare(VORTEX_ID, vortex);
        }

        /* The vortex square is in place */

        /* Now it's time to select the player to be taken into the vortex */
        Set<Player> selectablePlayers = determineSelectablePlayers(vortex, shooter);
        Player selectedPlayer = handlePlayerSelection(view, selectablePlayers);

        if (selectedPlayer == null) {
            return false; /* No selection */
        }

        /* Move the player to the vortex */
        board.movePlayer(selectedPlayer, vortex);

        /* Apply damage and marks */
        applyDamageAndMarks(selectedPlayer, shooter);

        /* Save the targeted player */
        weapon.savePlayer(id, selectedPlayer);

        return true;
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a weapon target which assigns damage and marks to all the players in a certain radius
 * from the shooter.
 * <ul>
 * <li>The internal and external radius can be specified with {@code minDist} and {@code maxDist}.</li>
 * <li>Specific players or squares can be excluded from getting damage with {@code excludePlayers}
 * and {@code excludeSquares}</li>
 * <li>Weapon direction is not taken into account</li>
 * </ul>
 * Please note that this type of target requires no interaction from the user.
 */
public class RadiusTarget extends AbstractTarget {
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
    public RadiusTarget(String id, AbstractWeapon weapon, Match match, ControllerFactory factory) {
        super(id, weapon, match, factory);
    }

    /**
     * Executes this target given the user of the weapon
     *
     * @param view The view of the player using the weapon
     * @return {@code true} if at least one player received the set damage/marks
     * {@code false} if no player received damage/marks
     */
    @Override
    public boolean execute(PersistentView view) {
        Player shooter = match.getPlayerByName(view.getUsername());
        Board board = match.getBoard();

        /* Determine the squares in the radius */
        Set<Square> squares = board.querySquares(shooter.getCurrentSquare(),
                visibility, null, minDist, maxDist);

        /* Remove the explicitly excluded squares */
        squares.removeAll(weapon.getSavedSquares(excludeSquares));

        /* Determine the players to be targeted */
        Set<Player> targetablePlayers = squares.stream()
                /* Map to players in those squares */
                .flatMap(square -> square.getPlayers().stream())
                .collect(Collectors.toSet());
        targetablePlayers.removeAll(determineUntargetablePlayers(shooter));

        /* Apply damage and marks */
        targetablePlayers.forEach(player -> applyDamageAndMarks(player, shooter));

        return !targetablePlayers.isEmpty();
    }
}

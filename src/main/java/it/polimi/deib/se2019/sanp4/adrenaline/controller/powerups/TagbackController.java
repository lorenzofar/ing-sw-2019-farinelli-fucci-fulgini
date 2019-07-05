package it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurn;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerBoard;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.util.HashSet;
import java.util.Set;

/**
 * Controller for the powerup effect {@link PowerupEnum#TAGBACK}.
 * <p>
 * A player may ues this during another player's turn, when he gets damaged by that player.
 * The player of the turn will get a mark from the player using the powerup.
 * </p>
 * @author Alessandro Fulgini
 */
public class TagbackController implements PowerupController {

    private final Match match;

    private static final String MESSAGE_CANT_USE = "You can't use this powerup: you haven't been" +
            " damaged during this turn";

    private static final int MARKS = 1;

    /**
     * Creates a new controller for the powerup effect {@link PowerupEnum#TAGBACK}
     *
     * @param match The match to be controlled, not null
     */
    public TagbackController(Match match) {
        this.match = match;
    }

    /**
     * Makes the player associated to given view use this powerup.
     * <p>
     * If the players who uses this powerup has received no damage during the current turn, he can't use
     * the powerup so he'll be notified and the method returns {@code false}.
     * </p>
     * <p>
     * If he has been damaged, then a mark will be given to the current player, with no additional action
     * required for the user of the powerup, and the method returns {@code true}.
     * </p>
     *
     * @param view The view of the player who uses the powerup, not null
     * @return {@code true} if the powerup has been used, {@code false} if it hasn't been used
     */
    @Override
    public boolean use(PersistentView view) {
        PlayerTurn currentTurn = match.getCurrentTurn();
        Player player = match.getPlayerByName(view.getUsername());

        /* Check that this player has been damaged during the current turn and that it's not the current player */
        Set<Player> damagedPlayers = new HashSet<>(currentTurn.getDamagedPlayers());
        damagedPlayers.remove(currentTurn.getTurnOwner()); /* Robust to rule changes */

        if (!damagedPlayers.contains(player)) {
            /* Notify the player that he can't use the powerup */
            view.showMessage(MESSAGE_CANT_USE, MessageType.WARNING);
            return false;
        }

        /* The powerup can be used */
        PlayerBoard currentPlayer = currentTurn.getTurnOwner().getPlayerBoard();
        currentPlayer.addMark(player, MARKS);
        return true;
    }
}

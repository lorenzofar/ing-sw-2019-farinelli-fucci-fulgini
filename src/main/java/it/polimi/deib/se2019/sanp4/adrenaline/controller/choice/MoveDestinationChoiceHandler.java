package it.polimi.deib.se2019.sanp4.adrenaline.controller.choice;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.ChoiceHandler;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.Controller;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerImpl;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

/** A specialized choice handler to handle the choice of a destination square when a player wants to move */
public class MoveDestinationChoiceHandler implements ChoiceHandler<CoordPair> {

    private static final Logger logger = Logger.getLogger(GrabWeaponChoiceHandler.class.getName());

    @Override
    public void handleChoice(CoordPair destination, String playerUsername, Controller controller) {
        if(destination == null || controller == null){
            throw new NullPointerException("Found null parameters");
        }

        // We get the square the player is on
        Player player = controller.getModel().getMatch().getPlayerByName(playerUsername);

        // We check if he's actually present in the match
        if(player == null){
            throw new IllegalStateException("Player does not exist in the match");
        }

        // We first check whether the destination is actually reachable
        boolean isDestinationReachable = controller.getModel().getMatch().getBoard().isReachable(
                player.getCurrentSquare().getLocation(),
                destination,
                ControllerImpl.MAX_MOVE_STEPS
        );
        if(isDestinationReachable){
            // We add the player to the selected square
            controller.getModel().getMatch().getBoard().getSquare(destination).addPlayer(player);
        }
        else{
            logger.log(Level.FINE,"Player tried to move to a non-reachable square");
            //TODO: Handle scenario where destination is not reachable
        }
    }

    @Override
    public void cancel() {
        //TODO: Implement this method
    }
}

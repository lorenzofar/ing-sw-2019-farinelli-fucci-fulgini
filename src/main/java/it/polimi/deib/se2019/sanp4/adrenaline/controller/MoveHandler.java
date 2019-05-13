package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.MoveEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.choice.MoveDestinationChoiceHandler;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.request.CoordPairRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.Set;

public class MoveHandler implements EventHandler{

    // Private constructor to hide public one
    private MoveHandler(){}

    public static void handle(MoveEvent event, Controller controller){
        // First we determine who is the player that wants to move
        Player player = controller.getModel().getMatch().getPlayerByName(event.getSender());

        if(player == null){
            throw new IllegalStateException("Player does not exist");
        }

        // Then we determine on which square he is on
        Square playerSquare = player.getCurrentSquare();

        // And we retrieve the set of squares he can reach
        Set<CoordPair> navigableSquares= controller.getModel().getMatch().getBoard().getNavigableSquares(playerSquare.getLocation(), ControllerImpl.MAX_MOVE_STEPS);

        Request<CoordPair> destinationRequest = new CoordPairRequest(
                "Select a destination square",
                navigableSquares,
                false
        );

        RequestContext<CoordPair> destinationRequestContext = new RequestContext<>(destinationRequest, new MoveDestinationChoiceHandler());
        controller.sendRequest(player, destinationRequestContext);

        // TODO: Finish implementing this method
    }

}
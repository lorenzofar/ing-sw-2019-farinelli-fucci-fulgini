package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurn;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An helper class that is responsible of managing the game flow
 * It manages game turns and game ending
 * It manages frenzy mode transition
 */
public class MatchController {

    /** The object representing the controller it acts on */
    private Controller controller;

    /**
     * Creates a new match controller with the provided controller
     * @param controller The object representing the controller, not null
     */
    MatchController(Controller controller){
        if(controller == null){
            throw new NullPointerException("Controller cannot be null");
        }
    }

    /**
     * Ends the current match and triggers final scoring
     */
    public void endMatch(){
        // This happens when the last turn after frenzy mode is completed
        controller.getGameTimer().stop(); // We stop the game timer
        //TODO: Implement this method
        //TODO: Trigger final scoring
    }

    /**
     * Starts the turn of the current player
     */
    public void startCurrentTurn(){
        //TODO: Implement this method
        controller.getGameTimer().start(); // We start the game timer for the current turn (last thing to do after setup)
    }

    /**
     * Ends the current turn and sets up the next one
     */
    public void endCurrentTurn(){
        controller.getGameTimer().reset(); // We reset the game timer to be ready for the next turn
        controller.getModel().getMatch().endCurrentTurn();
        selectNextTurn();
        //TODO: Implement this method
    }

    /**
     * Picks the next player and sets up his turn
     */
    public void selectNextTurn(){
        // We first retrieve the owner of the current turn
        Player currentPlayer = controller.getModel().getMatch().getCurrentTurn().getTurnOwner();
        // Then we retrieve the list of all the players that can actually play
        List<Player> players = controller.getModel().getMatch().getPlayers().stream().filter(player -> player.getState().canPlay()).collect(Collectors.toList());
        // We determine the index of the current player
        int index = players.indexOf(currentPlayer);
        // Then we increase the index by 1 to rotate clockwise
        index++;
        // We check whether we arrived at the end of the list
        // If yes, we start again
        if(index >= players.size()) {
            index = 0;
            if(controller.getModel().getMatch().isFrenzy()){
                // The match was in frenzy mode
                // so here the last player has completed its turn
                // and we're ready to end the game and perform final scoring
                endMatch();
            }
        }
        // We create a new turn for the next player
        PlayerTurn newTurn = new PlayerTurn(players.get(index));
        // We set the newly created turn as the current one
        controller.getModel().getMatch().setCurrentTurn(newTurn);
    }

    /**
     * Prepares the game to transition to frenzy mode
     */
    public void setupFrenzyMode(){
        // Here we should get all the players of the game and trigger their final frenzy mode
        // We should handle exceptions generated when their damage board have pending damages
        controller.getModel().getMatch().getPlayers().forEach(player -> {
            try {
                player.getPlayerBoard().turnFrenzy();
            }catch (PlayerException ex){}
        });
        //TODO: Finish implementing this method
    }
}

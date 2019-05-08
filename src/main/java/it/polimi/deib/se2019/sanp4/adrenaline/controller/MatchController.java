package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.model.board.AmmoSquare;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurn;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An helper class that is responsible of managing the game flow
 * It manages game turns and game ending
 * It manages frenzy mode transition
 */
public class MatchController {

    private static final int MIN_ACTIVE_PLAYERS = 3;

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

    private void refillAmmoSquares(){
        // First we get all the ammo squares where there are no ammo tiles left
        // We first retrieve all the squares composing the board
        List<AmmoSquare> emptyAmmoSquares = controller.getModel().getMatch().getBoard().getSquares()
                .stream()
                // Then we keep only the ammo squares
                .filter(square -> square instanceof AmmoSquare)
                .map(square -> (AmmoSquare)square)
                // And we further filter only the empty ones
                .filter(ammoSquare -> ammoSquare.getAmmoCard() == null)
                .collect(Collectors.toList());
        // Then we add for each of them a new ammo card drawn from the ammo stack
        emptyAmmoSquares.forEach(ammoSquare -> {
            // The draw a new card from the stack
            AmmoCard ammoCard = controller.getModel().getMatch().getAmmoStack().draw();
            // We put the card inside the ammo square
            ammoSquare.insertAmmo(ammoCard);
        });
    }

    /**
     * Ends the current match and triggers final scoring
     */
    public void endMatch(){
        // This happens when the last turn after frenzy mode is completed
        controller.getGameTimer().stop(); // We stop the game timer
        //TODO: Maybe we could notify the controller so he can tell players about game endings

        // We already scored the last turn, so we just perform final scoring
        // And we get the scores computed from the killshot track
        Map<Player, Integer> killshotTrackScores = controller.getScoreManager().scoreFinal(controller.getModel().getMatch());

        // Get all the players involved in the match
        List<Player> players = controller.getModel().getMatch().getPlayers();
        // Get the maximum score
        int maxScore = Collections.max(players.stream().map(Player::getScore).collect(Collectors.toList()));
        // Check whether there are draws, getting all the players with the highest score
        List<Player> topPlayers = players
                .stream()
                .filter(player -> player.getScore() == maxScore)
                .collect(Collectors.toList());
        if(topPlayers.size() > 1){
            // Here we have to break the tie by considering the scores got from the killshot track
            // First we remove from the killshot track scores map the players not belonging to the top ones
            killshotTrackScores.keySet()
                    .stream()
                    .filter(shooter -> !topPlayers.contains(shooter))
                    .forEach(killshotTrackScores::remove);

            // We check whether there are some elements left in the scores map
            // Otherwise, the players did not perform any killshot and they remain tied
            // Hence topPlayers list will contain the winners
            if(killshotTrackScores.size() != 0) {
                // Then we determine which is the highest score among top players
                int maxKillshotScore = Collections.max(killshotTrackScores.values());
                // Then we remove from top players those with a lesser score
                killshotTrackScores.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() != maxKillshotScore)
                        .map(Map.Entry::getKey)
                        .forEach(topPlayers::remove);
                // Now the topPlayers list contain the winner
            }
        }
        //TODO; Tell who is the winner (by passing the list of winners)
    }

    /**
     * Starts the turn of the current player
     */
    public void startCurrentTurn(){
        //TODO: Implement this method
        controller.getGameTimer().start(); // We start the game timer for the current turn (last thing to do after setup)
    }

    /**
     * Ends the current turn
     * Triggers scoring
     * and sets up the next one
     */
    public void endCurrentTurn(){
        controller.getGameTimer().reset(); // We reset the game timer to be ready for the next turn
        controller.getModel().getMatch().endCurrentTurn();
        // We tell the score manager to perform scoring of the turn
        controller.getScoreManager().scoreTurn(controller.getModel().getMatch());

        // We put new ammo tiles on square where there are none
        refillAmmoSquares();

        // After scoring, check whether to turn frenzy
        if(controller.getModel().getMatch().getSkulls() == 0){
            // There is no room left on the killshots track
            // Hence we set up frenzy mode
            setupFrenzyMode();
        }
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
            }catch (PlayerException ex){
                // We do nothing since the player board cannot be turned to frenzy mode
            }
        });
        controller.getModel().getMatch().goFrenzy();
        //TODO: Finish implementing this method
    }

    /**
     * Retrievs the number of active players
     * and determines whether the game should continue or not
     */
    private void checkActivePlayersCount(){
        int activePlayers = (int)controller.getModel().getMatch().getPlayers().stream().filter(player -> player.getState().canPlay()).count();
        if(activePlayers < MIN_ACTIVE_PLAYERS){
            // The number of players is not sufficient for the game to continue
            // We should hence stop the timer and end the match
            controller.getGameTimer().stop();
            endMatch();
        }
    }

    /**
     * Suspend the current player since its timer expired
     */
    public void suspendCurrentPlayer(){
        Player currentPlayer = controller.getModel().getMatch().getCurrentTurn().getTurnOwner();
        controller.getModel().getMatch().suspendPlayer(currentPlayer.getName());
        checkActivePlayersCount();
        endCurrentTurn();

    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.controller;

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
        //TODO: Implement this method
    }

    /**
     * Starts the turn of the current player
     */
    public void startCurrentTurn(){
        //TODO: Implement this method
    }

    /**
     * Picks the next player and sets up his turn
     */
    public void selectNextTurn(){
        //TODO: Implement this method
    }

    /**
     * Prepares the game to transition to frenzy mode
     */
    public void setupFrenzyMode(){
        //TODO: Implement this method
    }
}

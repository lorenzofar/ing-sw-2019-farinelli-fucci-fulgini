package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.action.MoveActionController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.match.MatchController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.match.SpawnController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.match.TurnController;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurn;

/**
 * Abstract factory used by controllers to create the other controllers they need.
 * The factory is bound to a specific {@link Match} and map of {@link PersistentView}s,
 * which are injected as dependencies as needed
 */
public interface ControllerFactory {

    /**
     * Creates the controller for the match of this factory
     * @return the controller for the match of this factory
     */
    MatchController createMatchController();

    /**
     * Creates a new controller for the current turn of the match.
     * @param turn The turn to be controlled, not null
     * @throws NullPointerException If the turn is null
     */
    TurnController createTurnController(PlayerTurn turn);

    /**
     * Creates the spawn controller associated to the match of this factory
     * @return the spawn controller associated to the match of this factory
     */
    SpawnController createSpawnController();

    /**
     * Returns the score manager for this match
     * @return the score manager for this match
     */
    ScoreManager createScoreManager();

    /**
     * Returns the payment handler for this match
     * @return the payment handler for this match
     */
    PaymentHandler createPaymentHandler();

    /**
     * Creates the controller for the Move basic action
     * @return the controller for the Move basic action
     */
    MoveActionController createMoveActionController();
}

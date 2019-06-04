package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.match.MatchController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.match.TurnController;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurn;

import java.util.Map;

/**
 * Default implementation of controller factory
 */
public class StandardControllerFactory implements ControllerFactory {

    /**
     * The match associated to this factory
     */
    private final Match match;

    /**
     * The views of the players playing in the match
     */
    private final Map<String, PersistentView> views;

    /* Match-wide controllers */
    private final SpawnController spawnController;

    private final ScoreManager scoreManager;

    private final PaymentHandler paymentHandler;

    private final MoveActionController moveActionController;

    /**
     * Creates a factory associated to the given match and views of the players,
     * which will be injected as dependencies where needed.
     *
     * @param match the match to be controlled, not null
     * @param views the persistent views of the players, not null
     */
    public StandardControllerFactory(Match match, Map<String, PersistentView> views) {
        if (match == null || views == null) {
            throw new NullPointerException("Match and views cannot be null");
        }
        this.match = match;
        this.views = views;

        /* Create match-wide controllers */
        spawnController = new SpawnController(match);
        scoreManager = new StandardScoreManager();
        paymentHandler = new PaymentHandler(match);
        moveActionController = new MoveActionController(match);
    }

    /**
     * Creates the controller for the match of this factory
     *
     * @return the controller for the match of this factory
     */
    @Override
    public MatchController createMatchController() {
        return new MatchController(match, views, this);
    }

    /**
     * Creates a new controller for the turn of the match.
     *
     * @param turn The turn to be controlled, not null
     * @throws NullPointerException If the turn is null
     */
    @Override
    public TurnController createTurnController(PlayerTurn turn) {
        return new TurnController(turn, match, views, this);
    }

    /**
     * Creates the spawn controller associated to the match of this factory
     *
     * @return the spawn controller associated to the match of this factory
     */
    @Override
    public SpawnController createSpawnController() {
        return spawnController;
    }

    /**
     * Returns the score manager for this match
     *
     * @return the score manager for this match
     */
    @Override
    public ScoreManager createScoreManager() {
        return scoreManager;
    }

    /**
     * Returns the payment handler for this match
     *
     * @return the payment handler for this match
     */
    @Override
    public PaymentHandler createPaymentHandler() {
        return paymentHandler;
    }

    /**
     * Creates the controller for the Move basic action
     *
     * @return the controller for the Move basic action
     */
    @Override
    public MoveActionController createMoveActionController() {
        return moveActionController;
    }

    /* ======= GETTERS ======= */

    Match getMatch() {
        return match;
    }

    Map<String, PersistentView> getViews() {
        return views;
    }
}

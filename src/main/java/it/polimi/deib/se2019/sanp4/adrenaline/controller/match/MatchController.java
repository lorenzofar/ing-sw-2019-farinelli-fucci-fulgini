package it.polimi.deib.se2019.sanp4.adrenaline.controller.match;

import it.polimi.deib.se2019.sanp4.adrenaline.common.AdrenalineProperties;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.LeaderboardUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Leaderboard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurn;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerException;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * It is responsible for controlling the flow of the game, from the beginning to the end
 */
public class MatchController {

    private final int minPlayers;

    private Match match;

    private Map<String, PersistentView> views;

    private SpawnController spawnController;

    private ScoreManager scoreManager;

    private List<Player> deadPlayers;

    private List<Player> players;

    private boolean finished;

    private ControllerFactory factory;

    private Runnable afterTurnCallback;

    /**
     * Creates the controller for given match
     *
     * @param match   the match to be controller, in its initial phase, not null
     * @param views   the persistent views of the players, not null
     * @param factory the factory to be used for creating sub-controllers
     */
    public MatchController(Match match, Map<String, PersistentView> views, ControllerFactory factory) {
        this.match = match;
        this.views = views;
        this.scoreManager = factory.createScoreManager();
        this.spawnController = factory.createSpawnController();
        this.players = match.getPlayers();
        this.deadPlayers = new ArrayList<>();
        this.finished = false;
        this.factory = factory;

        minPlayers = Integer.parseInt((String) AdrenalineProperties.getProperties()
                .getOrDefault("adrenaline.players.min", "3"));
    }

    /**
     * Runs the match
     *
     * @throws InterruptedException if the thread is interrupted while running the match
     */
    public void runMatch() throws InterruptedException {
        selectNextTurn(); /* Trigger the selection of the first turn */

        /* Main loop for each turn */
        while (!finished) {
            /* Refill the board with items */
            match.refillBoard();

            /* Let the player play his turn */
            runCurrentTurn();

            /* Call the callback if provided */
            if (afterTurnCallback != null) {
                afterTurnCallback.run();
            }

            /* Check for final player and active players number */
            checkIfMatchIsFinished(); // <- sets finished flag

            /* Scoring, resetting player boards, set frenzy mode etc. */
            endCurrentTurn();

            if (!finished) {
                respawnDeadPlayers(); /* Respawn dead players (if any) */
                selectNextTurn(); // <- sets finished flag
            }
        }

        /* The match is over */
        endMatch();
    }

    /**
     * Given that match.currentTurn is set, gets the player and
     * asks the {@link TurnController} to actually run the turn
     *
     * @throws InterruptedException If the thread gets interrupted
     */
    void runCurrentTurn() throws InterruptedException {
        /* Select the current turn */
        PlayerTurn turn = match.getCurrentTurn();

        TurnController turnController = factory.createTurnController(turn);
        turnController.runTurn();
    }

    /**
     * When the turn is over and the scoring has been performed, this checks it the match is over.
     * The match can end for two reasons:
     * <ol>
     * <li>It reached its final end: the final player performed his turn</li>
     * <li>The number of active players is not sufficient to continue</li>
     * </ol>
     * If the match is detected to be over, then the flag {@code finished is set}
     */
    void checkIfMatchIsFinished() {
        /* Determine the current player */
        Player currentPlayer = match.getCurrentTurn().getTurnOwner();

        /* Determine the number of active players */
        int activePlayers = (int) players.stream().filter(p -> p.getState().canPlay()).count();

        if (activePlayers < minPlayers || match.isFinalPlayer(currentPlayer)) {
            finished = true;
        }
    }

    /**
     * Implements the first part of the post-turn procedure:
     * <ol>
     * <li>Set turn state</li>
     * <li>Scoring</li>
     * <li>Resetting player boards</li>
     * <li>Setting up frenzy mode</li>
     * <li>Assigning action cards</li>
     * </ol>
     */
    void endCurrentTurn() {
        /* Set the turn state to OVER */
        match.endCurrentTurn();

        /* First assign scores and update killshots track */
        scoreManager.scoreTurn(match);

        /* Then save the list of dead players: they will need to respawn */
        deadPlayers = players.stream()
                .filter(p -> p.getPlayerBoard().isDead())
                .collect(Collectors.toList());

        /* Reset the boards of dead players */
        for (Player p : players) {
            try {
                /* Try to reset */
                p.getPlayerBoard().updateDeathsAndReset();
            } catch (PlayerException e) {
                /* The player boards of non-dead players won't reset */
            }
        }

        /* If the last skull has been drawn from the killshot track, then turn to frenzy mode */
        if (!match.isFrenzy() && match.getSkulls() == 0) {
            setupFrenzyMode();
        }

        /* If someone died when frenzy mode started or when it's running, his board needs to get flipped */
        /* If it is already flipped, flipping it again will make no difference */
        if (match.isFrenzy()) {
            players.forEach(player -> {
                try {
                    /* Try to flip the board */
                    player.getPlayerBoard().turnFrenzy();
                } catch (PlayerException e) {
                    /* The players who still have damage won't get their player board flipped */
                }
            });
        }

        /* Assign the action cards based on the damage of the players */
        players.forEach(Player::updateActionCard);
    }

    /**
     * When the last skull has been drawn, call this
     * <ul>
     * <li>Sets the frenzy attribute in {@link Match}</li>
     * <li>Sets the proper action cards</li>
     * <li>Saves the last player</li>
     * </ul>
     */
    private void setupFrenzyMode() {
        /* Set the frenzy attribute */
        match.goFrenzy();

        /* Change the action cards */
        Player current = match.getCurrentTurn().getTurnOwner();

        /* From the first to the current (excluded) give the action card with x1 */
        ActionCard actionCard = ActionCardCreator.createActionCard(ActionCardEnum.FRENZY1);

        for (Player p : players) {
            p.setActionCard(actionCard);

            /* After the current player switch to the x2 action card */
            if (p.equals(current)) {
                /* Switch the action card */
                actionCard = ActionCardCreator.createActionCard(ActionCardEnum.FRENZY2);
            }
        }
    }

    /**
     * Based on the current turn, picks the next player who is able to play
     * and sets up his turn.
     * If there are no online players or if the final player is encountered while
     * selecting the next turn, this will set the {@code finished} flag
     */
    void selectNextTurn() {
        int startingIndex;
        int nextPlayerIndex;
        PlayerTurn currentTurn = match.getCurrentTurn();

        if (currentTurn == null) {
            /* This is the first turn, so pick the first player who is able to play */
            startingIndex = 0;
            nextPlayerIndex = 0; /* No duplication, just prevent infinite loop */
        } else {
            /* Get the index of the player owning the current turn */
            startingIndex = players.indexOf(currentTurn.getTurnOwner());
            nextPlayerIndex = startingIndex + 1; /* Go to the next player */
        }


        Player nextPlayer;
        PlayerTurn nextTurn = null;
        do {
            /* If we reached the end of the list, we start again */
            if (nextPlayerIndex >= players.size()) nextPlayerIndex = 0;

            nextPlayer = players.get(nextPlayerIndex);

            if (nextPlayer.getState().canPlay()) {
                /* This player is the next player */
                nextTurn = new PlayerTurn(nextPlayer);
                match.setCurrentTurn(nextTurn); /* Set it as the new turn and notify the players */
            } else if (match.isFinalPlayer(nextPlayer) || nextPlayerIndex == startingIndex) {
                // First condition: we skipped the final player because he's offline
                // Second condition: there are no more online players
                finished = true;
            } else {
                nextPlayerIndex++;
            }
        } while (nextTurn == null && !finished);
    }

    /**
     * Procedure which respawns the players listed in {@code deadPlayers}.
     * Each player is asked to choose where to respawn based on powerups.
     * If the player is unable to respond, the spawn is automatic.
     * Every player is guaranteed to be respawned (if the thread is not interrupted)
     *
     * @throws InterruptedException if the thread gets interrupted while waiting
     */
    void respawnDeadPlayers() throws InterruptedException {
        for (Player p : deadPlayers) {
            PersistentView view = views.get(p.getName());
            spawnController.respawn(view);
        }
    }

    /**
     * Called when the match is over (end of the main loop)
     * <ul>
     * <li>Perform final scoring</li>
     * <li>Send the leaderboard to the views</li>
     * </ul>
     */
    void endMatch() {
        scoreManager.scoreFinal(match);

        /* Generate leaderboard and update */
        Leaderboard leaderboard = Leaderboard.generate(players);
        LeaderboardUpdate leaderboardUpdate = new LeaderboardUpdate(leaderboard);

        /* Send the update to everyone */
        match.update(leaderboardUpdate);

        /* Then set the final scoring view on all views */
        views.values().forEach(view -> view.selectScene(ViewScene.FINAL_SCORES));
    }

    /* ======= CALLBACK ========== */

    /**
     * Returns the callback that is called at the end of a turn
     *
     * @return The callback
     */
    public Runnable getAfterTurnCallback() {
        return afterTurnCallback;
    }

    /**
     * Sets the callback which is called at the end of a turn
     * <p>
     * It gets called right after the current player completed his turn, but before checking the number of active
     * players, respawning and preparing for the next turn.
     * </p>
     * <p>An example usage would be to reconnect players before the next turn</p>
     *
     * @param afterTurnCallback The callback to be called, nullable
     */
    public void setAfterTurnCallback(Runnable afterTurnCallback) {
        this.afterTurnCallback = afterTurnCallback;
    }

    /* ======= GETTERS AND SETTERS FOR TESTING ========= */

    List<Player> getDeadPlayers() {
        return deadPlayers;
    }

    boolean isMatchFinished() {
        return finished;
    }
}

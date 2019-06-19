package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.AdrenalineProperties;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.BoardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SkullCountRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

/**
 * This is responsible for asking a player the initial configuration of the match.
 * If the request expires, a random configuration is chosen
 */
public class MatchBootstrapper {

    private static final int NUMBER_OF_BOARDS = 4;

    private static final int MAX_SKULLS = 8;

    private int timeout;

    public MatchBootstrapper() {
        /* Get the value of the timeout */
        timeout = Integer.parseInt((String) AdrenalineProperties.getProperties()
                .getOrDefault("adrenaline.shorttime", "30"));
    }

    /**
     * Asks give player for the match configuration with a timeout.
     * If the request expires a random board and a default number of skulls is chosen
     * @param view view of the player
     * @return the match configuration, not null
     * @throws InterruptedException if the thread gets interrupted
     */
    public MatchConfiguration askForMatchConfiguration(PersistentView view) throws InterruptedException {
        int boardId;
        try {
            boardId = askForBoardId(view).get();
            view.stopTimer();
        } catch (CancellationException e) {
            /* Select a default board */
            boardId = 0;
        }

        int skulls;
        try {
            skulls = askForSkullsCount(view).get();
            view.stopTimer();
        } catch (CancellationException e) {
            /* Select a default value */
            skulls = MAX_SKULLS;
        }

        return new MatchConfiguration(boardId, skulls);
    }

    private CompletableChoice<Integer> askForBoardId(PersistentView view) {
        /* Generate request */
        BoardRequest req = new BoardRequest(integerRange(0, NUMBER_OF_BOARDS - 1));
        return requestWithTimer(view, req);
    }

    private CompletableChoice<Integer> askForSkullsCount(PersistentView view) {
        /* Generate request */
        SkullCountRequest req = new SkullCountRequest(integerRange(1, MAX_SKULLS));
        return requestWithTimer(view, req);
    }

    private CompletableChoice<Integer> requestWithTimer(PersistentView view, ChoiceRequest<Integer> req) {
        /* Send the request and start the timer */
        view.startTimer(() -> null, timeout, TimeUnit.SECONDS); /* If it expires the request gets cancelled */
        return view.sendChoiceRequest(req);
    }

    private List<Integer> integerRange(int min, int max) {
        List<Integer> choices = new LinkedList<>();
        for (int i = min; i <= max; i++) {
            choices.add(i);
        }
        return choices;
    }
}

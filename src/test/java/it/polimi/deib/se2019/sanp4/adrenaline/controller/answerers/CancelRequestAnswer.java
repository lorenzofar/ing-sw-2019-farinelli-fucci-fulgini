package it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;

import java.io.Serializable;

/**
 * Mockito answer to sendChoiceRequests which cancels any incoming request
 */
public class CancelRequestAnswer implements SendChoiceRequestAnswer<Serializable> {

    /**
     * Used to build the answer (choice) based on the request
     *
     * @param req Request sent by the caller
     * @return the response to the request
     */
    @Override
    public CompletableChoice<Serializable> answer(ChoiceRequest<Serializable> req) {
        /* Immediately cancel the request */
        return new CompletableChoice<>(req).cancel();
    }
}

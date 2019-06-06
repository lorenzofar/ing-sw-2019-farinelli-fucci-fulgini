package it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;

import java.io.Serializable;

/**
 * Mockito answer to sendChoiceRequests which selects no choice. This must only be used for optional requests
 */
public class ChooseNoneAnswer<T extends Serializable> implements SendChoiceRequestAnswer<T> {
    /**
     * Used to build the answer (choice) based on the request
     *
     * @param req Request sent by the caller
     * @return the response to the request
     */
    @Override
    public CompletableChoice<T> answer(ChoiceRequest<T> req) throws Throwable {
        return new CompletableChoice<>(req).complete(null);
    }
}

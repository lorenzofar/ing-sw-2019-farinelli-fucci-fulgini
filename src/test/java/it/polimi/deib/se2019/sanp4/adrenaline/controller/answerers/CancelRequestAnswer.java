package it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Mockito answer to sendChoiceRequests which cancels any incoming request
 */
public class CancelRequestAnswer implements Answer<CompletableChoice> {
    @Override
    public CompletableChoice answer(InvocationOnMock invocationOnMock) throws Throwable {
        /* Intercept the request */
        ChoiceRequest req = (ChoiceRequest) invocationOnMock.getArguments()[0];

        /* Cancel it immediately */
        return new CompletableChoice<>(req).cancel();
    }
}

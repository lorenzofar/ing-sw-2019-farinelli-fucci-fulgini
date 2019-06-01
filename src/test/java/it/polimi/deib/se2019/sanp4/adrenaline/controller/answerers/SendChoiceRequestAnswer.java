package it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.Serializable;

/**
 * Utility interface for stubbing the {@link PersistentView#sendChoiceRequest(ChoiceRequest)} method
 * @param <T> the type of value of the request
 */
public interface SendChoiceRequestAnswer<T extends Serializable> extends Answer<CompletableChoice<T>> {
    @Override
    default CompletableChoice<T> answer(InvocationOnMock invocationOnMock) throws Throwable {
        ChoiceRequest req = invocationOnMock.getArgument(0, ChoiceRequest.class);
        return answer(req);
    }

    /**
     * Used to build the answer (choice) based on the request
     * @param req Request sent by the caller
     * @return the response to the request
     */
    CompletableChoice<T> answer(ChoiceRequest<T> req) throws Throwable;
}

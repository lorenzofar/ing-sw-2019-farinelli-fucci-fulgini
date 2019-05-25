package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.BoardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SkullCountRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MatchBootstrapperTest {

    @Mock
    private static PersistentView persistentView;

    @Captor
    private static ArgumentCaptor<ChoiceRequest<Integer>> reqCaptor;

    @Captor
    private static ArgumentCaptor<SkullCountRequest> skReqCaptor;

    private static MatchBootstrapper matchBootstrapper;

    private static final int skulls = 5;

    private static final int boardId = 1;

    @Before
    public void setUp() {
        matchBootstrapper = new MatchBootstrapper();
    }

    @Test
    public void askForMatchConfiguration_bothRequestsComplete_shouldSetGivenValues() throws InterruptedException {
        /* Set up answerer */
        doAnswer(invocationOnMock -> {
            ChoiceRequest<Integer> req = (ChoiceRequest<Integer>) invocationOnMock.getArguments()[0];
            CompletableChoice<Integer> res = new CompletableChoice<>(req);
            if (req instanceof BoardRequest) {
                /* Answer to board request */
                res.complete(boardId);
            } else {
                /* Answer to skulls request */
                res.complete(skulls);
            }
            return res;
        }).when(persistentView).sendChoiceRequest(reqCaptor.capture());

        /* Do the test */
        MatchConfiguration config = matchBootstrapper.askForMatchConfiguration(persistentView);
        assertEquals(boardId, config.getBoardId());
        assertEquals(skulls, config.getSkulls());
    }

    @Test
    public void askForMatchConfiguration_secondRequestCancels_shouldSetDefaultValue() throws InterruptedException {
        /* Set up answerer */
        doAnswer(invocationOnMock -> {
            ChoiceRequest<Integer> req = (ChoiceRequest<Integer>) invocationOnMock.getArguments()[0];
            CompletableChoice<Integer> res = new CompletableChoice<>(req);
            if (req instanceof BoardRequest) {
                /* Answer to board request */
                res.complete(boardId);
            } else {
                /* Answer to skulls request */
                res.cancel();
            }
            return res;
        }).when(persistentView).sendChoiceRequest(reqCaptor.capture());

        /* Do the test */
        MatchConfiguration config = matchBootstrapper.askForMatchConfiguration(persistentView);
        assertEquals(boardId, config.getBoardId());
        assertEquals(8, config.getSkulls());
    }

    @Test
    public void askForMatchConfiguration_firstRequestCancels_shouldSetDefaultValue() throws InterruptedException {
        /* Set up answerer */
        doAnswer(invocationOnMock -> {
            ChoiceRequest<Integer> req = (ChoiceRequest<Integer>) invocationOnMock.getArguments()[0];
            CompletableChoice<Integer> res = new CompletableChoice<>(req);
            if (req instanceof BoardRequest) {
                /* Answer to board request */
                res.cancel();
            } else {
                /* Answer to skulls request */
                res.cancel();
            }
            return res;
        }).when(persistentView).sendChoiceRequest(reqCaptor.capture());

        /* Do the test */
        MatchConfiguration config = matchBootstrapper.askForMatchConfiguration(persistentView);
        assertEquals(0, config.getBoardId());
        assertEquals(8, config.getSkulls());
    }
}
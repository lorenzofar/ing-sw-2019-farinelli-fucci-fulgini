package it.polimi.deib.se2019.sanp4.adrenaline.model;

import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.InitialUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModelImplTest {

    @Mock
    private static Match match;

    @Mock
    private static ModelUpdate update;

    @Mock
    private static Observer<ModelUpdate> observer1;

    @Mock
    private static Observer<ModelUpdate> observer2;

    @Test
    public void new_shouldSetInitialState() {
        ModelImpl model = new ModelImpl();

        assertEquals(MatchOperationalState.WAITING_PLAYERS, model.getOperationalState());
        assertNull(model.getMatch());
    }

    @Test
    public void setMatch_shouldSetProperty() {
        ModelImpl model = new ModelImpl();

        model.setMatch(match);

        assertEquals(match, model.getMatch());
    }

    @Test
    public void setMatch_substitute_shouldSetNewMatch() {
        ModelImpl model = new ModelImpl();

        Match oldMatch = mock(Match.class);
        model.setMatch(oldMatch);

        model.setMatch(match);
        assertEquals(match, model.getMatch());
    }

    @Test
    public void setOperationalState_shouldSetProperty() {
        ModelImpl model = new ModelImpl();

        for (MatchOperationalState state : MatchOperationalState.values()) {
            model.setOperationalState(state);
            assertEquals(state, model.getOperationalState());
        }
    }

    @Test
    public void suspendPlayer_noMatch_shouldDoNothing() {
        ModelImpl model = new ModelImpl();

        model.suspendPlayer("player");

        verify(match, never()).suspendPlayer(anyString());
    }

    @Test
    public void unsuspendPlayer_noMatch_shouldDoNothing() {
        ModelImpl model = new ModelImpl();

        model.unsuspendPlayer("player");

        verify(match, never()).unsuspendPlayer(anyString());
    }

    @Test
    public void suspendPlayer_matchSet_usernameProvided_shouldCallDelegate() {
        ModelImpl model = new ModelImpl();
        model.setMatch(match);

        model.suspendPlayer("player");

        verify(match).suspendPlayer("player");
    }

    @Test
    public void unsuspendPlayer_matchSet_usernameProvided_shouldCallDelegate() {
        ModelImpl model = new ModelImpl();
        model.setMatch(match);

        model.unsuspendPlayer("player");

        verify(match).unsuspendPlayer("player");
    }

    @Test
    public void suspendPlayer_matchSet_usernameNull_shouldDoNothing() {
        ModelImpl model = new ModelImpl();
        model.setMatch(match);

        model.suspendPlayer(null);

        verify(match, never()).suspendPlayer(anyString());
    }

    @Test
    public void unsuspendPlayer_matchSet_usernameNull_shouldDoNothing() {
        ModelImpl model = new ModelImpl();
        model.setMatch(match);

        model.unsuspendPlayer(null);

        verify(match, never()).unsuspendPlayer(anyString());
    }

    @Test
    public void update_nullRecipients_shouldSendBroadcast() {
        ModelImpl model = new ModelImpl();

        /* Create two observers and subscribe them with names */
        model.addObserver("1", observer1);
        model.addObserver("2", observer2);

        /* Setup the mock of the update */
        when(update.getRecipients()).thenReturn(null);

        /* Call the method */
        model.update(update);

        /* Check that all the subscribers got that */
        verify(observer1).update(update);
        verify(observer2).update(update);
    }

    @Test
    public void update_specifiedRecipients_shouldOnlySendToSpecificNames() {
        ModelImpl model = new ModelImpl();

        /* Create two observers and subscribe them with names */
        model.addObserver("1", observer1);
        model.addObserver("2", observer2);

        /* Setup the mock of the update */
        when(update.getRecipients()).thenReturn(Collections.singleton("2"));

        /* Call the method */
        model.update(update);

        /* Check that only 2 got the update */
        verify(observer2).update(update);
    }

    @Test
    public void sendInitialUpdate_noMatch_shouldDoNothing() {
        ModelImpl model = new ModelImpl();

        /* Subscribe an observer */
        model.addObserver("1", observer1);

        model.sendInitialUpdate("1");

        verify(observer1, never()).update(any());
    }

    @Test
    public void sendInitialUpdate_matchSet_shouldGenerateAndSend() {
        ModelImpl model = new ModelImpl();
        model.setMatch(match);

        /* Subscribe two observers */
        model.addObserver("1", observer1);
        model.addObserver("2", observer2);

        /* Mock the generation of the update */
        InitialUpdate initialUpdate = mock(InitialUpdate.class);
        when(match.generateUpdate()).thenReturn(initialUpdate);

        model.sendInitialUpdate("1");

        verify(observer1).update(initialUpdate);
        verify(observer2, never()).update(any());
    }
}
package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ChoiceResponse;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersistentViewImplTest {

    private static class StringRequest extends ChoiceRequest<String> {

        private static final long serialVersionUID = -2439272347432526217L;

        StringRequest(List<String> choices) {
            super("somemessage", choices, true, String.class);
        }
    }

    private static final String username = "name";

    private static StringRequest sampleRequest;

    @Captor
    private static ArgumentCaptor<Observer<ViewEvent>> observerCaptor;

    @Mock
    private static ModelUpdate modelUpdate;

    @Mock
    private static RemoteView remoteView;

    private static RemoteView faultyRemote;

    @Mock
    private static Callable callback;

    @BeforeClass
    public static void setupClass() {
        /* Disable logging */
        LogManager.getLogManager().reset();
        sampleRequest = new StringRequest(Arrays.asList("a", "b", "c"));
    }

    public static void setupFaulty() throws IOException {
        /* Set up mock */
        faultyRemote = mock(RemoteView.class);
        doThrow(new IOException()).when(faultyRemote).performRequest(any(StringRequest.class));
        doThrow(new IOException()).when(faultyRemote).showMessage(anyString(), any(MessageType.class));
        doThrow(new IOException()).when(faultyRemote).selectScene(any(ViewScene.class));
        doThrow(new IOException()).when(faultyRemote).addObserver(observerCaptor.capture());
        doThrow(new IOException()).when(faultyRemote).update(any(ModelUpdate.class));
        doThrow(new IOException()).when(faultyRemote).ping();
    }

    @Test(expected = NullPointerException.class)
    public void create_nullUsername_shouldThrow() {
        new PersistentViewImpl(null, remoteView);
    }

    @Test(expected = NullPointerException.class)
    public void create_nullView_shouldThrow() {
        new PersistentViewImpl(username, null);
    }

    @Test
    public void create_faultyRemote_shouldCreateAnyway() throws IOException {
        setupFaulty();
        PersistentView view = new PersistentViewImpl(username, faultyRemote);
        assertEquals(username, view.getUsername());
    }

    /* ====== TIMER ======= */

    @Test(expected = NullPointerException.class)
    public void startTimer_nullCallback_shouldThrow() {
        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);

        view.startTimer(null, 12, TimeUnit.SECONDS);
    }

    @Test
    public void startTimer_callbackProvided_shouldCallCallback() throws Exception {
        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);

        view.startTimer(callback, 0, TimeUnit.SECONDS);
        view.getTimer().get(); /* Wait for the timer to expire */
        verify(callback).call(); /* Check that the callback has been called */
        assertFalse(view.isTimerRunning()); /* Check that the timer is not running */
    }

    @Test
    public void startTimer_throwingCallback_shouldIgnoreException() throws Exception {
        doThrow(new Exception()).when(callback).call();
        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);

        view.startTimer(callback, 0, TimeUnit.SECONDS);
        view.getTimer().get(); /* Wait for the timer to expire */
        verify(callback, timeout(10)).call();
    }

    @Test
    public void startTimer_alreadyRunning_shouldStopPrevious() throws Exception {
        Callable<?> stoppedCallback = mock(Callable.class);
        Callable<?> newCallback = mock(Callable.class);

        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);

        /* Start the first timer */
        view.startTimer(stoppedCallback, 15, TimeUnit.SECONDS);
        assertTrue(view.isTimerRunning()); /* Check that the timer is running */
        /* Start a new timer */
        view.startTimer(newCallback, 0, TimeUnit.SECONDS);

        view.getTimer().get(); /* Wait for the timer to expire */

        /* Check callback calls */
        verify(stoppedCallback, never()).call();
        verify(newCallback, timeout(2)).call();
    }

    @Test
    public void stopTimer_whileTimerIsRunning_shouldBeStopped() {
        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);

        view.startTimer(callback, 1, TimeUnit.MILLISECONDS);
        view.stopTimer();
        assertFalse(view.isTimerRunning());
    }

    /* ====== NETWORK ======= */

    @Test
    public void setReconnectionCallback_shouldBeSet() {
        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);

        view.setReconnectionCallback(callback);
        assertEquals(callback, view.getReconnectionCallback());
    }

    @Test(expected = NullPointerException.class)
    public void reconnectRemoteView_nullRemote_shouldThrow() {
        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);
        view.reconnectRemoteView(null);
    }

    @Test
    public void reconnectRemoteView_remoteStillConnected_shouldFail() {
        RemoteView newRemote = mock(RemoteView.class);

        /* We provide a view that never throws IOException */
        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);

        /* Then we try to connect another remote view */
        boolean reconnected = view.reconnectRemoteView(newRemote);
        assertFalse(reconnected); /* Check that reconnection failed */
        assertEquals(remoteView, view.getRemote()); /* Check that it still uses the old remote */
    }

    @Test
    public void reconnectRemoteView_networkProblemAlreadyDetected_shouldReconnect() throws IOException {
        setupFaulty();
        /* We provide a remote that throws on any method call */
        PersistentViewImpl view = new PersistentViewImpl(username, faultyRemote);

        /* We also provide a callback */
        view.setReconnectionCallback(callback);

        /* Then we call a method to trigger the problem */
        try {
            view.ping();
            fail();
        } catch (IOException e) {
            /* Ignore */
        }

        /* The persistent view should have detected the problem, so reconnection should happen */
        boolean reconnected = view.reconnectRemoteView(remoteView);
        assertTrue(reconnected);
        assertEquals(remoteView, view.getRemote());
    }

    @Test
    public void reconnectRemoteView_networkProblemStillNotDetected_shouldReconnect() throws IOException {
        setupFaulty();
        /* We provide a remote that throws on any method call */
        PersistentViewImpl view = new PersistentViewImpl(username, faultyRemote);

        /* Then we provide a genuine view */
        boolean reconnected = view.reconnectRemoteView(remoteView);

        /* The persistent view should have detected the problem, so reconnection should happen */
        assertTrue(reconnected);
        assertEquals(remoteView, view.getRemote());
    }

    @Test
    public void setNetworkFaultCallback_shouldBeSetAndScheduled() throws IOException {
        setupFaulty();
        PersistentViewImpl view = new PersistentViewImpl(username, faultyRemote);

        view.setNetworkFaultCallback(callback);
        assertEquals(callback, view.getNetworkFaultCallback());

        /* Trigger network problem */
        try {
            view.ping();
            fail();
        } catch (IOException ignore) {
            /* OK */
        }
    }

    /* ========== REQUESTS ============ */

    @Test
    public void sendChoiceRequest_remoteConnected_shouldReturnChoice() throws IOException {
        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);

        /* Send request */
        CompletableChoice<String> choice = view.sendChoiceRequest(sampleRequest);

        assertFalse(choice.isCompleted());
        assertTrue(view.getRequestManager().hasPendingRequests());
        verify(remoteView).performRequest(any(StringRequest.class)); /* Verify remote method call */
    }

    @Test
    public void sendChoiceRequest_duplicate_shouldReturnCancelledChoice() {
        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);

        /* Send first request */
        StringRequest req = new StringRequest(Arrays.asList("1", "2", "3"));
        view.sendChoiceRequest(req);

        /* Try to send it again */
        CompletableChoice<String> choice = view.sendChoiceRequest(req);

        assertTrue(choice.isCancelled());
    }

    @Test
    public void sendChoiceRequest_faulty_ShouldDetectProblem() throws IOException {
        setupFaulty();
        PersistentViewImpl view = new PersistentViewImpl(username, faultyRemote);

        /* Send request */
        StringRequest req = new StringRequest(Arrays.asList("1", "2", "3"));
        view.sendChoiceRequest(req);

        assertTrue(view.hasNetworkFault());
    }

    @Test
    public void cancelPendingRequests_shouldCancel() {
        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);

        /* Send request */
        CompletableChoice<String> choice = view.sendChoiceRequest(sampleRequest);

        /* Cancel all requests */
        view.cancelPendingRequests();
        assertTrue(choice.isCancelled());
        assertFalse(view.getRequestManager().hasPendingRequests());
    }

    /* ====== DELEGATE METHODS ========= */

    @Test(expected = IOException.class)
    public void performRequest_faultyRemote_shouldThrow() throws IOException {
        setupFaulty();
        PersistentViewImpl view = new PersistentViewImpl(username, faultyRemote);
        view.performRequest(sampleRequest);
    }

    @Test
    public void delegates_connectedRemote_shouldBeCalled() throws IOException {
        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);

        /* Call and verify methods */
        view.performRequest(sampleRequest);
        verify(remoteView).performRequest(sampleRequest);

        view.showMessage("message", MessageType.INFO);
        verify(remoteView).showMessage("message", MessageType.INFO);

        view.selectScene(ViewScene.LOBBY);
        verify(remoteView).selectScene(ViewScene.LOBBY);

        view.ping();
        verify(remoteView).ping();

        view.update(modelUpdate); /* Cannot check async call */
    }

    @Test
    public void delegates_alreadyDetectedFault_shouldNotCallRemote() throws IOException {
        setupFaulty();
        PersistentViewImpl view = new PersistentViewImpl(username, faultyRemote);

        /* Trigger the fault */
        try {
            view.ping();
            fail();
        } catch (IOException ignore) {
            /* OK */
        }

        /* Call and verify methods */

        try {
            view.performRequest(sampleRequest);
            fail();
        } catch (IOException e) {
            verify(remoteView, never()).performRequest(sampleRequest);
        }

        view.showMessage("message", MessageType.INFO);
        verify(remoteView, never()).showMessage("message", MessageType.INFO);

        view.selectScene(ViewScene.LOBBY);
        verify(remoteView, never()).selectScene(ViewScene.LOBBY);

        try {
            view.ping();
            fail();
        } catch (IOException e) {
            verify(remoteView, never()).ping();
        }

        view.update(modelUpdate);
        verify(remoteView, never()).update(modelUpdate);
    }

    @Test
    public void delegates_faultNotDetected_shouldBeDetected() throws IOException {
        setupFaulty();
        PersistentViewImpl view = new PersistentViewImpl(username, faultyRemote);
        assertFalse(view.hasNetworkFault());

        try {
            view.performRequest(sampleRequest);
            fail();
        } catch (IOException e) {
            assertTrue(view.hasNetworkFault());
        }

        view = new PersistentViewImpl(username, faultyRemote);
        view.showMessage("message", MessageType.INFO);
        assertTrue(view.hasNetworkFault());

        view = new PersistentViewImpl(username, faultyRemote);
        view.selectScene(ViewScene.LOBBY);
        assertTrue(view.hasNetworkFault());

        view = new PersistentViewImpl(username, faultyRemote);
        try {
            view.ping();
            fail();
        } catch (IOException e) {
            /* Ignore */
        }
        assertTrue(view.hasNetworkFault());

        view = new PersistentViewImpl(username, faultyRemote);
        view.update(modelUpdate); /* Cannot check async call */
    }

    /* ============ HANDLE CHOICE RESPONSE =============== */

    @Test
    public void visit_validChoiceProvided_shouldBeCompleted() {
        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);

        /* Send request */
        CompletableChoice<String> choice = view.sendChoiceRequest(sampleRequest);

        /* Send a valid response for that */
        ChoiceResponse<String> res = new ChoiceResponse<>("sender", sampleRequest.getUuid(), "a");
        view.visit(res);

        /* Check that the choice has been completed */
        assertTrue(choice.isCompleted());
    }

    @Test
    public void visit_inexistentUuid_shouldIgnore() {
        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);

        ChoiceResponse<String> res = new ChoiceResponse<>("sender", sampleRequest.getUuid(), "a");
        view.visit(res);

        assertFalse(view.getRequestManager().hasPendingRequests());
    }

    @Test
    public void visit_invalidChoice_shouldIgnore() {
        PersistentViewImpl view = new PersistentViewImpl(username, remoteView);

        /* Send request */
        CompletableChoice<String> choice = view.sendChoiceRequest(sampleRequest);

        ChoiceResponse<String> res = new ChoiceResponse<>("sender", sampleRequest.getUuid(), "invalid");
        view.visit(res);

        assertFalse(choice.isCompleted());
        assertTrue(view.getRequestManager().hasPendingRequests());
    }
}
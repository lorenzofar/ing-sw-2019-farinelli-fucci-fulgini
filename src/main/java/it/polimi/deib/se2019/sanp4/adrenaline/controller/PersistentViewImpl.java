package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ChoiceResponse;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.DuplicateIdException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.UnknownIdException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.InvalidChoiceException;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.RequestManager;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of {@link PersistentView} by decorating {@link RemoteView}
 * Uses a {@link RequestManager} to handle requests to the user
 * and a {@link ScheduledExecutorService} for the timer.
 * By convention, if the remote view has connection faults, it gets set to null
 */
public class PersistentViewImpl extends RemoteObservable<ViewEvent> implements PersistentView {

    private static Logger logger = Logger.getLogger(PersistentView.class.getName());

    /* This class observes the underlying remote view and sends updates to its parent */
    private class RemoteViewObserver implements RemoteObserver<ViewEvent> {

        private PersistentView parent;

        RemoteViewObserver(PersistentView parent) {
            this.parent = parent;
        }

        /* Receives an update from the remote view */
        @Override
        public void update(ViewEvent event) {
            if (event != null) {
                event.accept(parent); /* Make the persistent view visit the update */
            }
        }
    }

    private String username;

    private RemoteView remote;

    private final RequestManager requestManager = new RequestManager();

    private final ScheduledExecutorService timerScheduler = Executors.newSingleThreadScheduledExecutor();

    private final ExecutorService callbackExecutor = Executors.newSingleThreadExecutor();

    private final ExecutorService updateExecutor = Executors.newSingleThreadExecutor();

    private Future<?> timer;

    private final Object timerLock = new Object();

    private Callable<?> reconnectionCallback;

    private Callable<?> networkFaultCallback;

    private RemoteViewObserver eventSpy;

    /* ========== CONSTRUCTOR ========== */

    /**
     * Creates a new PersistentView by decorating a remote RemoteView
     *
     * @param username the username associated to this view, not null
     * @param remote   the actual remote view, not null
     */
    public PersistentViewImpl(String username, RemoteView remote) {
        if (username == null) throw new NullPointerException("Username cannot be null");
        if (remote == null) throw new NullPointerException("View cannot be null");
        this.username = username;
        this.remote = remote;

        /* Now create an internal remote observer to get events from the view */
        /* It needs to be exported to be RMI-compatible */
        eventSpy = new RemoteViewObserver(this);
        try {
            RemoteObserver<ViewEvent> stub =
                    (RemoteObserver<ViewEvent>) UnicastRemoteObject.exportObject(eventSpy, 0);
            remote.addObserver(stub);
        } catch (IOException ignore) {
            logger.log(Level.SEVERE, "Could not subscribe to remote view of \"{0}\"", username);
        }
    }

    /* ========== TIMER ========== */

    /**
     * Starts a timer for this particular player.
     * If a timer is running, this will stop it and start the new one
     *
     * @param callback the function that will be called when the timer expires, not null
     * @param delay    the duration of the timer
     * @param unit     the time unit for delay
     * @throws RejectedExecutionException – if the task cannot be scheduled for execution
     * @throws NullPointerException       – if callback is null
     */
    @Override
    public void startTimer(Callable<?> callback, long delay, TimeUnit unit) {
        if (callback == null) throw new NullPointerException("Callback cannot be null");
        synchronized (timerLock) {
            if (isTimerRunning()) {
                stopTimer();
            }
            timer = timerScheduler.schedule(() -> {
                cancelPendingRequests();
                try {
                    callback.call();
                } catch (Exception e) {
                    /* Ignore the exception */
                }
            }, delay, unit);
        }
    }

    /**
     * Stops the running timer.
     * If there is no running timer, it does nothing
     */
    @Override
    public void stopTimer() {
        synchronized (timerLock) {
            if (timer != null) {
                timer.cancel(true);
            }
        }
    }

    /**
     * Checks if the timer is running
     *
     * @return {@code true} if the timer is running, {@code false} if not
     */
    @Override
    public boolean isTimerRunning() {
        synchronized (timerLock) {
            if (timer == null) return false;
            return !timer.isDone();
        }
    }

    /**
     * Returns the future associated to the timer callback
     *
     * @return future associated to the timer callback
     */
    Future getTimer() {
        return timer;
    }

    /* ========= NETWORK ========== */

    /**
     * Forces the disconnection of the internal remote view, if it is connected.
     * The disconnection is then treated as a network fault
     */
    @Override
    public void disconnectRemoteView() {
        /* Check if the remote is still connected */
        if (remote != null) {
            /* Try to remove observer */
            try {
                remote.removeObserver(eventSpy);
            } catch (IOException ignore) {
                /* Ignore: we cannot reach the remote */
            }
            foundNetworkFault(); /* Treat disconnection as a network fault */
        }
    }

    /**
     * Substitutes the remote view of the player with the provided one.
     * In detail:
     * <ul>
     * <li>If network faults have already been detected, the substitution is guaranteed</li>
     * <li>
     * If network faults have not been detected, a ping is sent to the client to detect
     * network problems.
     * If the ping doesn't succeed, the reconnection happens; if it does succeed, then
     * the reconnection doesn't happen
     * </li>
     * </ul>
     *
     * @param view the view of the player who wants to reconnect
     * @return {@code true} if reconnection goes fine, {@code false} otherwise
     */
    @Override
    public synchronized boolean reconnectRemoteView(RemoteView view) {
        if (view == null) throw new NullPointerException("View cannot be null");
        /* Check connectivity */
        try {
            remote.ping();
        } catch (IOException e) {
            /* No connectivity => we invalidate the current remote */
            invalidateRemote();
        } catch (NullPointerException ignore) {
            /* This means that remote was already null */
        }

        /* If there were network problems, accept reconnection */
        if (remote == null) {
            /* Add the listener */
            try {
                view.addObserver(eventSpy);
            } catch (IOException e) {
                return false; /* Does not respond => don't keep it */
            }

            /* Substitute the remote */
            remote = view;
            /* Call the reconnection callback asynchronously */
            if (reconnectionCallback != null) {
                callbackExecutor.submit(reconnectionCallback);
            }
            return true;
        } else {
            /* If the user is still connected, do not allow reconnection */
            return false;
        }
    }

    /**
     * Sets the function to be called when a reconnection happens
     *
     * @param callback the function to be called when a reconnection happens
     */
    @Override
    public void setReconnectionCallback(Callable<?> callback) {
        reconnectionCallback = callback;
    }

    /**
     * Returns the callback called on reconnection
     *
     * @return The callback called on reconnection
     */
    Callable getReconnectionCallback() {
        return reconnectionCallback;
    }

    /**
     * Sets the function to be called when a network fault is detected
     *
     * @param callback the function to be called when a network fault is detected
     */
    @Override
    public void setNetworkFaultCallback(Callable<?> callback) {
        networkFaultCallback = callback;
    }

    /**
     * Returns the callback called when a network fault is detected
     *
     * @return The callback called when a network fault is detected
     */
    Callable getNetworkFaultCallback() {
        return networkFaultCallback;
    }

    /**
     * Returns the remote view used by this PersistentView
     *
     * @return the remote view used by this PersistentView
     */
    public RemoteView getRemote() {
        return remote;
    }

    /**
     * Called when a network fault is detected
     */
    private void foundNetworkFault() {
        /* No connectivity => we invalidate the current remote */
        invalidateRemote();
        cancelPendingRequests();
        stopTimer();
        if (networkFaultCallback != null) {
            /* Notify the subscriber in another thread */
            callbackExecutor.submit(networkFaultCallback);
        }
    }

    /**
     * Tries to remove the event observer from the current remote
     * and then sets it to null, even if it couldn't remove the observer
     */
    private void invalidateRemote() {
        /* Try to remove the observer */
        try {
            remote.removeObserver(eventSpy);
        } catch (IOException | NullPointerException e) {
            /* Ignore, it just doesn't respond anymore */
        }
        remote = null;
    }

    /**
     * Returns if a network fault has been detected with the remote.
     * Note that this does not try to contact the remote to check connectivity,
     * but reports if a network fault has been detected in previous method calls
     *
     * @return if a network fault has been detected
     */
    @Override
    public boolean hasNetworkFault() {
        return remote == null;
    }

    /* ========== REQUESTS ============ */

    /**
     * Sends a request to the player and returns an object that can be used to retrieve the response
     * to that request.
     * If the request is not accepted by the {@link RequestManager}, or if the user is disconnected
     * this returns a pre-cancelled {@link CompletableChoice}
     *
     * @param request the request to be sent, not null
     * @return a completable choice which can be used to retrieve the user's choice
     */
    @Override
    public <T extends Serializable> CompletableChoice<T> sendChoiceRequest(ChoiceRequest<T> request) {
        if (isTimerRunning()) {
            /* Try to send the request */
            try {
                /* Insert it in the request manager */
                CompletableChoice<T> choice = requestManager.insertRequest(request);
                /* Send it to the player */
                remote.performRequest(request); /* Throws NPE if already disconnected */
                return choice;
            } catch (IOException e) {
                foundNetworkFault(); /* Call the callback */
            } catch (DuplicateIdException | NullPointerException e) {
                /* Proceed with next block */
            }
        }
        /* The request could not be sent, or the timer is not running so return a pre-cancelled choice */
        /* in order to end the current interaction with the user */
        return new CompletableChoice<>(request).cancel();
    }

    /**
     * Forces all the pending {@link ChoiceRequest}s for this view to be canceled
     */
    @Override
    public void cancelPendingRequests() {
        requestManager.cancelPendingRequests();
    }

    /**
     * Returns the request manager used by this PersistentView
     *
     * @return the request manager used by this PersistentView
     */
    RequestManager getRequestManager() {
        return requestManager;
    }

    /* ======== DELEGATE METHODS ========= */

    /**
     * Returns the username of the player associated with this view.
     * It returns {@code null} if the username has not already been set
     * (prior to login)
     *
     * @return username of the player, if it has been set, {@code null} otherwise
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Performs the provided request on the view
     *
     * @param request The object representing the request, not null
     * @throws IOException if the remote call fails
     */
    @Override
    public <T extends Serializable> void performRequest(ChoiceRequest<T> request) throws IOException {
        if (remote == null) throw new IOException("Not connected");
        try {
            remote.performRequest(request);
        } catch (IOException e) {
            foundNetworkFault();
            throw e;
        }
    }

    /**
     * Displays the provided message
     *
     * @param text The text of the message, not null
     * @param type The type of the message, not null
     */
    @Override
    public void showMessage(String text, MessageType type) {
        try {
            remote.showMessage(text, type);
        } catch (IOException e) {
            foundNetworkFault();
        } catch (NullPointerException ignore) {
            /* Ignore */
        }
    }

    /**
     * Displays the selected scene
     *
     * @param scene The object representing the scene
     */
    @Override
    public void selectScene(ViewScene scene) {
        try {
            remote.selectScene(scene);
        } catch (IOException e) {
            foundNetworkFault();
        } catch (NullPointerException ignore) {
            /* Ignore */
        }
    }

    /**
     * Checks connectivity to the client
     *
     * @throws IOException If there is no connectivity
     */
    @Override
    public void ping() throws IOException {
        try {
            remote.ping();
        } catch (IOException | NullPointerException e) {
            foundNetworkFault();
            throw new IOException(e);
        }
    }

    /**
     * Send an update from a {@link RemoteObservable} object.
     * The update is sent asynchronously
     *
     * @param update update to be sent
     */
    @Override
    public void update(ModelUpdate update) {
        if (remote == null) return;
        updateExecutor.submit(() -> updateSync(update));
    }

    /**
     * Sends an update to the remote view, synchronously
     *
     * @param update the update to be sent
     */
    @Override
    public void updateSync(ModelUpdate update) {
        if (remote != null) {
            try {
                remote.update(update);
            } catch (IOException e) {
                foundNetworkFault();
            }
        }
    }

    /**
     * Handles given choice response by trying to complete the corresponding request
     *
     * @param choiceResponse the choice response event
     */
    @Override
    public <T extends Serializable> void visit(ChoiceResponse<T> choiceResponse) {
        /* Get the uuid and try to complete the choice */
        String uuid = choiceResponse.getUuid();
        T choice = choiceResponse.getChoice();
        try {
            requestManager.completeRequest(uuid, choice);
        } catch (UnknownIdException e) {
            /* If we get these the response might have been generated by someone else, so we ignore it */
        } catch (InvalidChoiceException e) {
            /* Re-send the request */
            resendRequest(requestManager.getPendingChoice(uuid).getRequest());
        }
    }


    /**
     * Sends a request to the remote without putting it in the request manager
     * Note: remote must not be null
     *
     * @param request The request to be sent, not null
     */
    private void resendRequest(ChoiceRequest request) {
        /* Try to send the request */
        try {
            /* Send the request to the player */
            remote.performRequest(request); /* Throws NPE if already disconnected */
        } catch (IOException e) {
            foundNetworkFault(); /* Call the callback */
        }
    }
}

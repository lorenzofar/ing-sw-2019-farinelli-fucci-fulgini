package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEventVisitor;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteObserver;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Represents an extension of the View of a player, which is used by the controller.
 *
 * <div>
 * <b>Requests</b>
 * <p>
 *     This enables the controller classes to make {@link ChoiceRequest}s to the user
 *     and receive a {@link CompletableChoice} in response to each request.
 *     When the user makes the choice, that {@link CompletableChoice} will be completed.
 *     When the timer is not running, all requests made with {@link #sendChoiceRequest(ChoiceRequest)}
 *     will return a pre-cancelled {@link CompletableChoice}
 * </p>
 * </div>
 * <div>
 * <b>Timer</b>
 * <p>
 * This also has the ability to keep a timer for the player to respond to requests.
 * When this timer expires, the following things will happen:
 * </p>
 * <ol>
 *     <li>All the pending {@link CompletableChoice}s are cancelled.</li>
 *     <li>The provided callback is called</li>
 * </ol>
 * </div>
 * <div>
 * <b>Network faults</b>
 * <p>
 * The methods exposed by this interface do not throw {@link IOException}: if a remote call fails,
 * the caller of the method (a controller class) doesn't need to be notified about that.
 * But if a network fault is detected, i.e. if an {@code IOException} is thrown
 * when calling a method on the remote view, the following will happen:
 * </p>
 * <ol>
 *     <li>All the pending {@link CompletableChoice}s are cancelled.</li>
 *     <li>If the timer is running, it gets stopped</li>
 *     <li>If a callback has been provided with {@link #setNetworkFaultCallback(Callable)} (Callable)},
 *     it gets called</li>
 * </ol>
 * </div>
 * <div>
 * <b>Persistence</b>
 * <p>
 *     This view is associated to the player from the beginning till the end of the match, even if he disconnects.
 *     Then it can also handle the reconnection of the player.
 * </p>
 * </div>
 * <div>
 * <b>Updates</b>
 * <p>
 *     Updates are sent asynchronously if calling {@link #update(Object)}, in order not to block the model
 *     However, if an update has to be sent synchronously, the method {@link #updateSync(ModelUpdate)} is provided
 * </p>
 * </div>
 * @author Alessandro Fulgini
 */
public interface PersistentView extends RemoteView, ViewEventVisitor {

    /* ========= TIMER ========== */

    /**
     * Starts a timer for this particular player.
     * If a timer is running, this will stop it and start the new one
     * @param callback the function that will be called when the timer expires
     * @param delay the duration of the timer
     * @param unit the time unit for delay
     */
    void startTimer(Callable<?> callback, long delay, TimeUnit unit);

    /**
     * Stops the running timer.
     * If there is no running timer, it does nothing
     */
    void stopTimer();

    /**
     * Checks if the timer is running
     * @return {@code true} if the timer is running, {@code false} if not
     */
    boolean isTimerRunning();

    /* ========= NETWORK ========== */

    /**
     * Forces the disconnection of the internal remote view, if it is connected.
     * The disconnection is then treated as a network fault
     */
    void disconnectRemoteView();

    /**
     * Substitutes the remote view of the player with the provided one.
     * In detail:
     * <ul>
     *     <li>If network faults have already been detected, the substitution is guaranteed</li>
     *     <li>
     *         If network faults have not been detected, a ping is sent to the client to detect
     *         network problems.
     *         If the ping doesn't succeed, the reconnection happens; if it does succeed, then
     *         the reconnection doesn't happen
     *     </li>
     * </ul>
     * @param view the view of the player who wants to reconnect
     * @return {@code true} if reconnection goes fine, {@code false} otherwise
     */
    boolean reconnectRemoteView(RemoteView view);

    /**
     * Sets the function to be called when a reconnection happens
     * @param callback the function to be called when a reconnection happens
     */
    void setReconnectionCallback(Callable<?> callback);

    /**
     * Sets the function to be called when a network fault is detected
     * @param callback the function to be called when a network fault is detected
     */
    void setNetworkFaultCallback(Callable<?> callback);

    /**
     * Returns if a network fault has been detected with the remote.
     * Note that this does not try to contact the remote to check connectivity,
     * but reports if a network fault has been detected in previous method calls
     * @return if a network fault has been detected
     */
    boolean hasNetworkFault();

    /* ========== REQUESTS =========== */

    /**
     * Sends a request to the player and returns an object that can be used to retrieve the response
     * to that request
     * @param request the request to be sent, not null
     * @param <T> the type of choices
     * @return a completable choice which can be used to retrieve the user's choice
     */
    <T extends Serializable> CompletableChoice<T> sendChoiceRequest(ChoiceRequest<T> request);

    /**
     * Forces all the pending {@link ChoiceRequest}s for this view to be canceled
     */
    void cancelPendingRequests();

    /* ========== SYNCHRONOUS UPDATE ===== */

    /**
     * Sends an update to the remote view, synchronously
     * @param update the update to be sent
     */
    void updateSync(ModelUpdate update);

    /* =========== DELEGATED ============= */

    /**
     * Returns the username of the player associated with this view.
     * It returns {@code null} if the username has not already been set
     * (prior to login)
     *
     * @return username of the player, if it has been set, {@code null} otherwise
     */
    @Override
    String getUsername();

    /**
     * Displays the provided message
     *
     * @param text The text of the message, not null
     * @param type The type of the message, not null
     */
    @Override
    void showMessage(String text, MessageType type);

    /**
     * Displays the selected scene
     *
     * @param scene The object representing the scene
     */
    @Override
    void selectScene(ViewScene scene);

    /**
     * Adds an observer to listen for updates
     *
     * @param observer The object representing the observer, not null
     */
    @Override
    void addObserver(RemoteObserver<ViewEvent> observer);

    /**
     * Removes an observer from listening for updates
     * If it hasn't previously subscribed, does nothing
     *
     * @param observer The object representing the observer, not null
     */
    @Override
    void removeObserver(RemoteObserver<ViewEvent> observer);
}

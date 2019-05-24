package it.polimi.deib.se2019.sanp4.adrenaline.controller.requests;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.DuplicateIdException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.UnknownIdException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.logging.Logger;

/**
 * Responsible for managing the {@link ChoiceRequest} sent to the view
 * and for providing a way for their responses to complete the choices.
 *
 * <p>
 *     The class keeps a map with all the completable choices associated to pending request.
 *     When a request is completed(cancelled) its CompletableChoice is completed(cancelled)
 *     and it is then removed from the internal map.
 * </p>
 */
public class RequestManager {

    private static Logger logger = Logger.getLogger(RequestManager.class.getName());

    /**
     * A map with all the uncompleted choices.
     * The key is the UUID of the request which originated the {@link CompletableChoice}
     */
    private Map<String, CompletableChoice> pendingChoices;

    /**
     * Creates an empty RequestManager
     */
    public RequestManager() {
        pendingChoices = new HashMap<>();
    }

    /**
     * Receives a choice request and returns a {@link CompletableChoice} which can be used to retrieve
     * the chosen object when this is provided by completing the {@link CompletableChoice}.
     * The completion can be done by calling {@link #completeRequest(String, Object)}
     * @param request the request that has to be inserted
     * @param <T> the type of the choice
     * @return an object which can be used to retrieve the choice
     * @throws DuplicateIdException if a request with the same uuid is pending
     */
    public <T extends Serializable> CompletableChoice<T> insertRequest(ChoiceRequest<T> request)
            throws DuplicateIdException {
        String uuid = request.getUuid();
        if (pendingChoices.containsKey(uuid)) {
            throw new DuplicateIdException(String.format("Cannot insert request %s: already inserted", uuid));
        }

        /* Create a new completable choice from the request */
        CompletableChoice<T> choice = new CompletableChoice<>(request);
        /* Save it in the map and return it */
        pendingChoices.put(uuid, choice);
        return choice;
    }

    /**
     * Given the UUID of the request, attempt to complete the associated choice.
     * This method will check if the choice is valid based on the request
     * and if it is valid it will make the choice available via {@link CompletableChoice#get()}.
     * If the choice completes successfully, it will be deleted from the pending map.
     * @param uuid unique identifier of the request whose choice has to be completed
     * @param choice the object which represents the choice
     * @throws UnknownIdException if no {@link CompletableChoice} associated to the UUID can be found
     * @throws InvalidChoiceException f the choice is invalid (not accepted by the request)
     */
    public void completeRequest(String uuid, Object choice)
            throws UnknownIdException, InvalidChoiceException {

        /* Get the pending choice, if it exists */
        CompletableChoice completableChoice = pendingChoices.get(uuid);
        if (completableChoice == null) {
            throw new UnknownIdException(String.format("Cannot complete request %s: it does not exist", uuid));
        }

        /* Try to complete it with the given choice object */
        completableChoice.complete(choice);

        /* If the completion goes fine, remove the choice from the map */
        pendingChoices.remove(uuid);
    }

    /**
     * Attempts to cancel the choice request.
     * This attempt will fail if the request (i.e. its {@link CompletableChoice}) has already been completed.
     * In case the cancellation is successful, anyone waiting on that choice to complete
     * will get a {@link CancellationException}.
     * This method also removes the choices from the pending map.
     * If you try to cancel a request that does not exist (already completed or never inserted) the method will
     * simply return {@code false}.
     * @param uuid identifier of the request you want to cancel
     * @return {@code true} if the request was correctly cancelled, {@code false} if the request could not be cancelled,
     * either because it ha been already completed or because it has never been submitted
     */
    public boolean cancelRequest(String uuid) {
        /* Get the pending choice, if it exists */
        CompletableChoice completableChoice = pendingChoices.get(uuid);

        if (completableChoice == null) return false;

        /* Remove the choice from the map */
        pendingChoices.remove(uuid);

        /* Cancel it and remove it and return if cancellation was successful or not */
        return completableChoice.cancel();
    }


    /**
     * Cancels all the pending requests in this manager.
     * <p>
     *     This is particularly useful when a player does not respond.
     *     When the timeout is reached, the pending request(s) should be cleared out before
     *     passing the turn over, so that the request doesn't get stuck in the manager forever.
     * </p>
     */
    public void cancelPendingRequests() {
        /* Cancel all the choices */
        pendingChoices.values().forEach(CompletableChoice::cancel);
        /* Empty the map */
        pendingChoices.clear();
    }

    /**
     * Checks if a pending request with the given UUID is saved in the manager
     * @param uuid unique identifier of the request
     * @return {@code true} if the request is pending, {@code false} otherwise
     */
    public boolean isRequestPending(String uuid) {
        /* If a choice exists in the map with associated UUID, then that request is pending */
        return pendingChoices.containsKey(uuid);
    }

    /**
     * Returns whether there are pending requests or not
     * @return {@code true} if there are pending requests, {@code false} otherwise
     */
    public boolean hasPendingRequests() {
        return !pendingChoices.isEmpty();
    }

    /**
     * Returns the pending choice associated to given request UUID.
     * <p>
     *     Please note that this method does not remove the choice from the internal map, if it gets completed
     *     and also that the return type has no type parameter, so the casting will be unchecked.
     *     Completion/cancellation of the requests should always be performed with
     *     {@link #completeRequest(String, Object)} and {@link #cancelRequest(String)}.
     * </p>
     * @param uuid the unique identifier of the request that originated the choice
     * @return the pending choice if it is found, {@code null} if no request is found with given UUID
     * @apiNote This method is package-private because external classes are not encouraged to manipulate
     * the choices directly, because it prevents them from being automatically taken out of the pending map
     */
    CompletableChoice getPendingChoice(String uuid) {
        return pendingChoices.get(uuid);
    }
}

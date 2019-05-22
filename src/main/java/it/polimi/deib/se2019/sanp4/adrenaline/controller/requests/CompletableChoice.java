package it.polimi.deib.se2019.sanp4.adrenaline.controller.requests;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;

import java.util.concurrent.CancellationException;

/**
 * Represents the "promise" of a value in response to a {@link ChoiceRequest}
 * highly inspired by {@code CompletableFuture}.
 * A {@code CompletableChoice} can be returned when asking for a choice that has to be performed asynchronously
 * (e.g. by the user).
 * <p>
 *     The method that received the {@code CompletableChoice} can then call {@link #get()}, which will wait for
 *     the choice to be completed (it is a blocking method) and then return it.
 * </p>
 * <p>
 *     On the other hand the choice can be completed by calling {@link #complete(Object)}, which will complete
 *     the choice with the given object (if it is a valid choice) and return in via {@link #get()}.
 * </p>
 * <p>
 *     The {@code CompletableChoice} can also be cancelled, in which case the ones waiting on {@link #get()} will
 *     receive an exception.
 * </p>
 * @param <T> the type of the choice
 */
public class CompletableChoice<T> {

    /** The request whose response will provide the choice */
    private ChoiceRequest<T> request;

    /** Indicates if the choice has been completed (normal completion or cancellation) */
    private boolean completed;

    /** Indicates if the future has been cancelled */
    private boolean cancelled;

    /** The choice provided */
    private T choice;

    /**
     * Creates a FutureChoice in a pending state, bound to given request.
     * @param request Request whose response will provide the choice
     */
    public CompletableChoice(ChoiceRequest<T> request) {
        if (request == null) throw new NullPointerException("Request cannot be null");
        this.request = request;
        this.completed = false;
        this.cancelled = false;
    }

    /**
     * Attempt to complete the choice with given object.
     * This method will check if the choice is valid based on the request
     * and if it is valid it will make the choice available via {@link #get()}
     * If the choice has already been completed, this call has no effect
     * @param choice the choice object to be provided, even null if the request is optional
     * @throws InvalidChoiceException if the choice is invalid (not accepted by the request)
     */
    public synchronized void complete(Object choice) throws InvalidChoiceException {
        /* If the future has been completed, it cannot be completed again */
        if (completed) return;

        /* Check that the choice is valid */
        if (request.isChoiceValid(choice)) {
            /* The choice is valid */

            /* Safely cast the provided choice and set the status to completed */
            /* Note that the cast is safe because we made sure that the choice is valid */
            this.choice = request.getType().cast(choice);
            completed = true;
            notifyAll(); /* Notify waiting threads that the choice has been provided */
        } else {
            /* The choice is not valid */
            throw new InvalidChoiceException(choice);
        }
    }

    /**
     * Attempts to cancel the choice.
     * This attempt will fail if the choice has already been completed or cancelled.
     *
     * <p>After this method returns, subsequent calls to {@link #isCompleted()} will
     * always return {@code true}.  Subsequent calls to {@link #isCancelled}
     * will always return {@code true} if this method returned {@code true}.
     *
     * @return {@code false} if the choice could not be cancelled,
     * typically because it has already completed normally;
     * {@code true} otherwise
     */
    public synchronized boolean cancel() {
        /* If already completed or canceled, canceling fails */
        if (completed) return false;

        /* Else set canceled to true, mark as completed and notify */
        cancelled = true;
        completed = true;
        notifyAll();
        return true;
    }

    /**
     * Returns {@code true} if this choice was cancelled before it completed
     * normally.
     *
     * @return {@code true} if this choice was cancelled before it completed
     */
    public synchronized boolean isCancelled() {
        return cancelled;
    }

    /**
     * Returns {@code true} if this choice completed.
     * <p>
     * Completion may be due to normal completion, an exception, or
     * cancellation -- in all of these cases, this method will return
     * {@code true}.
     *
     * @return {@code true} if this choice completed
     */
    public synchronized boolean isCompleted() {
        return completed;
    }

    /**
     * Waits if necessary for the choice to be completed, and then
     * retrieves its result.
     *
     * @return the completed choice
     * @throws CancellationException if the choice was cancelled
     * @throws InterruptedException  if the current thread was interrupted while waiting
     */
    public synchronized T get() throws InterruptedException {
        /* Wait until the future is completed */
        while (!completed) {
            wait();
        }
        /* If cancelled throw */
        if (cancelled) throw new CancellationException();
        /* Else return the choice */
        return choice;
    }

    /**
     * Returns the request that originated the choice
     * @return request that originated the choice
     */
    public ChoiceRequest<T> getRequest() {
        return request;
    }
}


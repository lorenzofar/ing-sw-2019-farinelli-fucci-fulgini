package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A class describing an overlay that can be observed to catch fired events
 */
public abstract class ObservableOverlay<T> extends Button {

    /**
     * The data associated to the overlay
     */
    private T data;

    /**
     * List of consumers listening for selection events from the overlay
     */
    private List<Consumer<ObservableOverlay<T>>> listeners = new ArrayList<>();

    /**
     * Add the provided listener to listen for element's events
     *
     * @param listener The object representing the listener
     */
    public void addListener(Consumer<ObservableOverlay<T>> listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Removes the provided listeners from the list of attached ones
     *
     * @param listener The object representing the listener
     */
    public void removeListener(Consumer<ObservableOverlay<T>> listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    /**
     * Removes all the attached listeners from the overlay
     */
    public void clearListeners() {
        listeners.clear();
    }

    /**
     * Notify the listeners that the element has fired an event
     */
    void notifyListeners() {
        // Create a new list of listeners to notify to avoid concurrent modification exceptions
        List<Consumer<ObservableOverlay<T>>> listenersToNotify = new ArrayList<>(this.listeners);
        listenersToNotify.forEach(listener -> listener.accept(this));
    }

    /**
     * Enables the overlay to fire observable events
     */
    public abstract void enable();

    /**
     * Resets the overlay and prevents it from firing observable events
     */
    public abstract void reset();

    /**
     * Retrieves the data associated to the overlay
     *
     * @return The object representing the data
     */
    public T getData() {
        return data;
    }

    /**
     * Sets the data associated to the overlay
     *
     * @param data The object representing the data
     */
    void setData(T data) {
        this.data = data;
    }
}

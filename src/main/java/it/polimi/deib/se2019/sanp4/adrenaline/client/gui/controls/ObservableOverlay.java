package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import javafx.scene.control.Control;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A class describing an overlay that can be observed to catch fired events
 */
public class ObservableOverlay extends Control {
    /**
     * List of consumers listening for selection events from the overlay
     */
    private List<Consumer<ObservableOverlay>> listeners = new ArrayList<>();

    /**
     * Add the provided listener to listen for element's events
     *
     * @param listener The object representing the listener
     */
    public void addListener(Consumer<ObservableOverlay> listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Notify the listeners that the element has fired an event
     */
    protected void notifyListeners() {
        listeners.forEach(listener -> listener.accept(this));
    }

}
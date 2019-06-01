package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SelectableOverlay extends Button {

    private static final String SELECTABLE_CLASS = "selectable";

    /**
     * List of consumers listening for selection events from the overlay
     */
    private List<Consumer<SelectableOverlay>> listeners;

    /**
     * Property to store whether the element is selectable or not
     */
    private BooleanProperty selectable = new SimpleBooleanProperty(false);


    SelectableOverlay(String resource) {
        listeners = new ArrayList<>();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            // Ignore loading errors
        }
    }

    /**
     * Sets the provided control to be the visual feedback of the selectable status
     *
     * @param control The javafx control to use
     */
    void setSelectableRoot(Control control) {
        this.selectable.addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                if (newValue) {
                    control.getStyleClass().add(SELECTABLE_CLASS);
                } else {
                    control.getStyleClass().removeAll(SELECTABLE_CLASS);
                }
            }
        });
    }

    /**
     * Sets whether the element can be selected or not
     *
     * @param selectable {@code true} if the element can be selected, {@code false otherwise}
     */
    public void setSelectable(boolean selectable) {
        this.selectable.set(selectable);
    }

    public void addListener(Consumer<SelectableOverlay> listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Notify the listeners that the element has been selected
     * Listeners are notified only when the element can actually be selected
     */
    void notifyListeners() {
        if (selectable.get()) {
            listeners.forEach(listener -> listener.accept(this));
        }
    }

}

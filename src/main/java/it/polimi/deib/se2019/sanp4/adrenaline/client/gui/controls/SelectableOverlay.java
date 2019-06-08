package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Control;

import java.io.IOException;

/**
 * A class describing an overlay that can be selected (e.g. by clicking on it)
 * It extends the ObservableOverlay to notify listeners that it has been selected
 */
public abstract class SelectableOverlay extends ObservableOverlay {

    private static final String SELECTABLE_CLASS = "selectable";

    /**
     * Property to store whether the element is selectable or not
     */
    private BooleanProperty selectable = new SimpleBooleanProperty(false);

    SelectableOverlay(String resource) {
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
        control.setOnMouseClicked(event -> this.notifyListeners());
    }

    /**
     * Sets whether the element can be selected or not
     *
     * @param selectable {@code true} if the element can be selected, {@code false otherwise}
     */
    public void setSelectable(boolean selectable) {
        this.selectable.set(selectable);
    }

    /**
     * Enables the overlay to fire observable events
     */
    @Override
    void enable() {
        this.selectable.set(true);
    }

    /**
     * Resets the overlay and prevents it from firing observable events
     */
    @Override
    public void reset() {
        this.selectable.set(false);
    }
}

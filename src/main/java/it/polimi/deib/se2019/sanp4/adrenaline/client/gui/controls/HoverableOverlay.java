package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Control;

import java.io.IOException;

/**
 * A class describing an overlay that reacts when it's being hovered
 */
public class HoverableOverlay extends ObservableOverlay {

    private boolean hovered = false;

    public HoverableOverlay(String resource) {
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
     * Sets the root element that triggers hovering events
     * @param control The object representing the root element
     */
    public void setHoverableRoot(Control control) {
        control.hoverProperty().addListener(((observableValue, oldValue, newValue) -> {
            hovered = newValue;
            if (oldValue != newValue) {
                notifyListeners();
            }
        }));
    }

    /**
     * Determines whether the overlay is being hovered
     *
     * @return {@code true} if the overlay is being hovered, {@code false} otherwise
     */
    public boolean isHovered() {
        return hovered;
    }
}

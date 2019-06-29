package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import javafx.stage.Stage;

/**
 * An abstract class representing a controller used to handle GUI screens.
 * It holds a reference to the current ClientView and the current stage, along with methods to set them
 */
abstract class GUIController {
    /**
     * The object representing the current client view
     */
    ClientView clientView;
    /**
     * The object representing the current stage
     */
    Stage stage;

    /**
     * Set the provided client view
     *
     * @param clientView The object representing the client view, not null
     */
    void setClientView(ClientView clientView) {
        if (clientView == null) {
            throw new NullPointerException("Client view cannot be null");
        }
        this.clientView = clientView;
    }

    /**
     * Sets the stage the controller is into
     *
     * @param stage The object representing the stage
     */
    void setStage(Stage stage) {
        this.stage = stage;
    }
}
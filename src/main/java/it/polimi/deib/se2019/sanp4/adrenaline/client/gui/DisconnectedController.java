package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * The controller for the scene shown when the user is disconnected from the server
 * It acts as an informational scene to notify the user and also allows the user to reconnect
 */
public class DisconnectedController extends GUIController {

    @FXML
    private Button reconnectBtn;

    @FXML
    private TextField serverHostnameField;

    /**
     * Property to determine whether the connection was successful and to disable button accoridngly
     */
    private BooleanProperty connected;
    /**
     * Property to store the hostname of the server to reconnect to
     */
    private StringProperty serverHostname;

    @FXML
    public void initialize() {
        connected = new SimpleBooleanProperty(false);
        serverHostname = new SimpleStringProperty("");
        serverHostnameField.textProperty().bindBidirectional(serverHostname);
        reconnectBtn.disableProperty().bind(connected.or(serverHostname.isEmpty()));
    }

    /**
     * Try to reconnect to the server and handle errors
     */
    @FXML
    public void reconnect() {
        // First try to reconnect to the server
        // Then if the connection is successful, try to log in
        try {
            if (!clientView.getServerConnection().isActive()) {
                // Connection is performed only when the connection is not active
                clientView.getServerConnection().connect(serverHostname.get());
            }
            connected.set(true);
            clientView.getServerConnection().login(clientView.getUsername());
        } catch (IOException e) {
            connected.set(false);
            new Alert(Alert.AlertType.ERROR, "Error establishing connection to server").show();
        } catch (LoginException e) {
            connected.set(false);
            new Alert(Alert.AlertType.ERROR, "Couldn't log back in, please try again").show();
        }
    }
}

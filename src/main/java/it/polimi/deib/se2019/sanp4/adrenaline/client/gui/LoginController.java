package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class LoginController extends GUIController {

    @FXML
    private TextField hostnameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    public ToggleGroup networkToggleGroup;
    @FXML
    public RadioButton socketToggle;
    @FXML
    public RadioButton rmiToggle;
    @FXML
    private Button connectBtn;


    /**
     * Property to check whether the client connected succesfully
     */
    private BooleanProperty serverConnected;
    /**
     * Property to check whether the connection has already been set in the view
     */
    private BooleanProperty connectionSet;
    /**
     * Property to check whether the user has succesfully logged in
     */
    private BooleanProperty loggedIn;
    /**
     * Property to store the inserted hostname
     */
    private StringProperty serverHostname;
    /**
     * Property to store the inserted username
     */
    private StringProperty username;

    @FXML
    public void initialize() {
        serverHostname = new SimpleStringProperty("");
        username = new SimpleStringProperty("");
        serverConnected = new SimpleBooleanProperty(false);
        connectionSet = new SimpleBooleanProperty(false);
        loggedIn = new SimpleBooleanProperty(false);
        hostnameTextField.textProperty().bindBidirectional(serverHostname);
        usernameTextField.textProperty().bindBidirectional(username);
        hostnameTextField.disableProperty().bind(serverConnected);
        usernameTextField.disableProperty().bind(loggedIn);
        socketToggle.disableProperty().bind(connectionSet);
        rmiToggle.disableProperty().bind(connectionSet);
        connectBtn.disableProperty().bind(username.isEmpty().or(serverConnected.not().and(serverHostname.isEmpty())).or(loggedIn));
    }

    /**
     * Create the server connection according to the selected configuration
     * and try to log in using the provided username.
     * When error occur, an alert is shown to the user and he can then try again
     */
    public void login() {
        // Retrieve the selected network connection and set it up in the client view accordingly
        if (serverConnected.not().get()) {
            try {
                if (networkToggleGroup.getSelectedToggle().getUserData().toString().equals("socket")) {
                    clientView.setSocketConnection();
                } else {
                    clientView.setRMIConnection();
                }
                connectionSet.set(true);
            } catch (Exception ignore) {
                // The connection has already been set in the view
                connectionSet.set(true);
            }
            try {
                clientView.getServerConnection().connect(serverHostname.get());
                serverConnected.set(true);
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Error establishing connection to server").showAndWait();
                return;
            }
        }

        // Try to log in with the provided username
        try {
            clientView.getServerConnection().login(username.getValue());
            clientView.setUsername(username.getValue());
            loggedIn.set(true);
            Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, "Succesfully connected", ButtonType.OK).show());
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "A network error occurred").showAndWait();
            // A network error occurred
        } catch (LoginException e) {
            new Alert(Alert.AlertType.WARNING, "The username is already taken, choose another one").showAndWait();
        } finally {
            serverHostname.set("");
            username.set("");
        }
    }
}

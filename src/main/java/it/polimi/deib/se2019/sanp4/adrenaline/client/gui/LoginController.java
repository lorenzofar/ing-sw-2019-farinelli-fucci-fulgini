package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;
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
    private Button connectBtn;

    /** Property to store the inserted hostname */
    private StringProperty serverHostname;
    /** Property to store the inserted username */
    private StringProperty username;

    @FXML
    public void initialize(){
        serverHostname = new SimpleStringProperty("");
        username = new SimpleStringProperty("");
        hostnameTextField.textProperty().bindBidirectional(serverHostname);
        usernameTextField.textProperty().bindBidirectional(username);
        connectBtn.disableProperty().bind(username.isEmpty().or(serverHostname.isEmpty()));
    }

    /**
     * Create the server connection according to the selected configuration
     * and try to log in using the provided username.
     * When error occur, an alert is shown to the user and he can then try again
     */
    public void login(){
        // Retrieve the selected network connection and set it up in the client view accordingly
        if(networkToggleGroup.getSelectedToggle().getUserData().toString().equals("socket")){
            clientView.setSocketConnection();
        }
        else{
            clientView.setRMIConnection();
        }

        try {
            clientView.getServerConnection().connect(serverHostname.get());
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error establishing connection to server").showAndWait();
        }

        // Try to log in with the provided username
        try {
            clientView.getServerConnection().login(username.getValue());
            new Alert(Alert.AlertType.INFORMATION, "Succesfully connected", ButtonType.OK).showAndWait();
            clientView.selectScene(ViewScene.LOBBY);
            //TODO: Handle successful login
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "A network error occurred").showAndWait();
            // A network error occurred
        } catch (LoginException e) {
            new Alert(Alert.AlertType.WARNING, "The username is already taken, choose another one").showAndWait();
        }
        finally {
            serverHostname.set("");
            username.set("");
        }
    }
}

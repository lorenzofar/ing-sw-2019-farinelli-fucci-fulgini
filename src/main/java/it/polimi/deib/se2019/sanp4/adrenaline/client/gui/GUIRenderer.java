package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.client.UIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.*;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Collection;

public class GUIRenderer extends Application implements UIRenderer {

    private ClientView clientView;
    /**
     * The stage of the app to update scenes
     */
    private Stage stage;
    /**
     * The controller of the currently displayed scene
     */
    private GUIController currentController;

    @Override
    public void initialize() {
        launch();
    }

    /**
     * Sets the current scene with the provided FXML resource
     *
     * @param fxmlResource The path of the FXML file to display
     * @return {@code true} if the scene has been set succesfully, {@code false} otherwise
     */
    private boolean showScene(String fxmlResource) {
        if (fxmlResource == null) {
            return false;
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlResource));
        try {
            Scene scene = new Scene(loader.load());
            this.stage.setScene(scene);

            currentController = loader.getController();
            currentController.setClientView(clientView);
            return true;
        } catch (IOException e) {
            // An error occurred loading the scene
            return false;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Adrenaline");
        primaryStage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
        this.stage = primaryStage;
        this.clientView = new ClientView();
        clientView.setRenderer(this);

        showScene("/fxml/login.fxml");

        primaryStage.show();
    }


    /* ===== LOBBY ===== */
    @Override
    public void showLobby() {
        Platform.runLater(() -> showScene("/fxml/lobby.fxml"));
    }

    @Override
    public void updateLobby(Collection<String> connectedPlayers, boolean matchStarting) {
        Platform.runLater(() -> {
            LobbyController lobbyController = (LobbyController) currentController;
            try {
                lobbyController.setConnectedPlayers(connectedPlayers);
                lobbyController.setMatchStarting(matchStarting);
            } catch (Exception ignore) {
                // If the previous calls fail, it means the lobby is not yet initialized
                showLobby();
            }
        });
    }
    /* ================== */

    /**
     * Prepare the client for the game
     * Triggered when the match starts
     */
    @Override
    public void showMatchScreen() {
        showScene("/fxml/game.fxml");
    }

    /**
     * Shows a message to the user
     *
     * @param text The text of the message
     * @param type The type of the message
     */
    @Override
    public void showMessage(String text, MessageType type) {
        new Alert(type.getAlertType(), text, ButtonType.OK).showAndWait();
    }

    @Override
    public void handle(ActionRequest request) {
        //TODO: Implement this method
    }

    @Override
    public void handle(BoardRequest request) {
        //TODO: Implement this method
    }

    @Override
    public void handle(PlayerOperationRequest request) {
        //TODO: Implement this method
    }

    @Override
    public void handle(PlayerRequest request) {
        //TODO: Implement this method
    }

    @Override
    public void handle(PowerupCardRequest request) {
        //TODO: Implement this method
    }

    @Override
    public void handle(SkullCountRequest request) {
        try {
            ((LobbyController) currentController).showSkullsSelectionWindow(request);
        } catch (Exception ignore) {
            // We ignore this request if we're not in the lobby
        }
    }

    @Override
    public void handle(SquareRequest request) {
        //TODO: Implement this method
    }

    @Override
    public void handle(WeaponCardRequest request) {
        //TODO: Implement this method
    }
}

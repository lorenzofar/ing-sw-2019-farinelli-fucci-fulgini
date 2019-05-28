package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.client.UIRenderer;
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
        showScene("/fxml/lobby.fxml");
    }

    @Override
    public void updateLobby(Collection<String> connectedPlayers) {
        LobbyController lobbyController = (LobbyController) currentController;
        try {
            lobbyController.setConnectedPlayers(connectedPlayers);
        } catch (Exception ignore) {
            // Errors are ignored if the method is invoked when the current scene is wrong
        }
    }

    /**
     * Notify the user about the imminent start of the game
     */
    @Override
    public void startWaitingMatch() {
        LobbyController lobbyController = (LobbyController) currentController;
        try {
            lobbyController.showMatchWaiting();
        } catch (Exception ignore) {
            // Errors are ignored if the method is invoked when the current scene is wrong
        }
    }

    /**
     * Cancel the waiting indicators when the game start is cancelled (e.g. due to lack of players)
     */
    @Override
    public void cancelWaitingMatch() {
        LobbyController lobbyController = (LobbyController) currentController;
        try {
            lobbyController.hideMatchWaiting();
        } catch (Exception ignore) {
            // Errors are ignored if the method is invoked when the current scene is wrong
        }
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
}

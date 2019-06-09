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
            Platform.runLater(() -> this.stage.setScene(scene));

            currentController = loader.getController();
            currentController.setClientView(clientView);
            currentController.setStage(this.stage);
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

        Platform.runLater(primaryStage::show);
    }

    /**
     * Spawns a new windows with the provided title and showing the provided FXML file
     *
     * @param resource The path of the FXML file
     * @param title    The title of the window
     * @return The controller associated to the newly created window
     */
    private GUIController showNewWindow(String resource, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(resource));
            Stage newStage = new Stage();
            newStage.setTitle(title);
            Scene newScene = new Scene(loader.load());
            Platform.runLater(() -> newStage.setScene(newScene));
            ((GUIController) loader.getController()).setClientView(clientView);
            ((GUIController) loader.getController()).setStage(newStage);
            Platform.runLater(newStage::show);
            return (GUIController) loader.getController();
        } catch (IOException ignore) {
            // Ignore errors
            return null;
        }
    }

    /* ===== LOBBY ===== */

    @Override
    public void showLobby() {
        Platform.runLater(() -> showScene("/fxml/lobby.fxml"));
    }

    @Override
    public void updateLobby(Collection<String> connectedPlayers, boolean matchStarting) {
        Platform.runLater(() -> {
            try {
                LobbyController lobbyController = (LobbyController) currentController;
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
        try {
            GameController gameController = (GameController) currentController;
            Platform.runLater(gameController::buildMatchScreen);
        } catch (Exception ignore) {
            // An error occurred while building the match screen
        }
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
        try {
            BoardRequestController boardRequestController = (BoardRequestController) showNewWindow("/fxml/BoardConfig.fxml", "Board configuration");
            if (boardRequestController != null) {
                boardRequestController.setup(request);
            }
        } catch (Exception ignore) {
            // Errors are ignores
        }
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
        try {
            PowerupRequestController powerupRequestController = (PowerupRequestController) showNewWindow("/fxml/powerupSelectionWindow.fxml", "Select powerup");
            if (powerupRequestController != null) {
                powerupRequestController.setup(request);
            }
        } catch (Exception ignore) {
            // We ignore this error
        }
    }

    @Override
    public void handle(SkullCountRequest request) {
        try {
            SkullsRequestController skullsRequestController = (SkullsRequestController) showNewWindow("/fxml/skullsConfig.fxml", "Skulls config");
            if (skullsRequestController != null) {
                skullsRequestController.setup(request);
            }
        } catch (Exception ignore) {
            // We ignore this error
        }
    }

    @Override
    public void handle(SquareRequest request) {
        //TODO: Implement this method
    }

    @Override
    public void handle(WeaponCardRequest request) {
        try {
            WeaponCardRequestController weaponCardRequestController = (WeaponCardRequestController) showNewWindow("/fxml/weaponCardSelectionWindow.fxml", "Select Weapon");
            if (weaponCardRequestController != null) {
                weaponCardRequestController.setup(request);
            }
        } catch (Exception ignore) {
            // We ignore this error
        }
    }
}

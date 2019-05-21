package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientViewImpl;
import it.polimi.deib.se2019.sanp4.adrenaline.client.UIRenderer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class GUIRenderer extends Application implements UIRenderer {

    private ClientView clientView;
    /**
     * The stage of the app to update scenes
     */
    private Stage stage;

    @Override
    public void initialize() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Adrenaline");
        primaryStage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();

        this.stage = primaryStage;
        this.clientView = new ClientViewImpl();
        clientView.setRenderer(this);

        LoginController loginController = loader.getController();
        loginController.setClientView(clientView);
    }

    /**
     * Retrieve the controller associated to the provided fxml resource
     * @param fxmlResource The path of the resource
     * @return The object representing the controller
     */
    private Object getController(String fxmlResource){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlResource));
        return loader.getController();
    }


    @Override
    public void showLobby() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/lobby.fxml"));
        try {
            Scene lobbyScene = new Scene(loader.load());
            this.stage.setScene(lobbyScene);

            LobbyController lobbyController = loader.getController();
            lobbyController.setClientView(clientView);
        } catch (IOException e) {
            // An error occurred loading the scene
            //TODO: Handle error
        }
    }

    /**
     * Notify the user about the imminent start of the game
     */
    @Override
    public void startWaitingMatch() {
        LobbyController lobbyController = (LobbyController)getController("/fxml/lobby.fxml");
        lobbyController.showMatchWaiting();
    }

    /**
     * Cancel the waiting indicators when the game start is cancelled (e.g. due to lack of players)
     */
    @Override
    public void cancelWaitingMatch() {
        LobbyController lobbyController = (LobbyController)getController("/fxml/lobby.fxml");
        lobbyController.hideMatchWaiting();
    }

    /**
     * Prepare the client for the game
     * Triggered when the match starts
     */
    @Override
    public void showMatchScreen() {
        //TODO: Implement this method
    }
}

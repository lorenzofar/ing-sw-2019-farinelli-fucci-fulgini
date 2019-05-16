package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.client.UIRenderer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GUIRenderer extends Application implements UIRenderer{

    private ClientView clientView;
    /** The stage of the app to update scenes */
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
        this.clientView = new ClientView();

        LoginController loginController = loader.getController();
        loginController.setClientView(clientView);
    }
}

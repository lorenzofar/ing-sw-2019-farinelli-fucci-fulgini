package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.client.UIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Collection;

public class GUIRenderer extends Application implements UIRenderer {

    public static final String CSS_BOLD_TITLE = "title-text";

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
            ignore.printStackTrace();
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
    public void cancelSelection() {
        // We set a null selection handler and, incidentally, we cancel the existing one (if present)
        clientView.setSelectionHandler(null);
        //TODO: Maybe notify the user?
    }

    /**
     * Shows a screen to tell the user that he is disconnected, letting him to reconnect
     */
    @Override
    public void showDisconnectedScreen() {
        Platform.runLater(() -> showScene("/fxml/disconnected.fxml"));
    }

    /**
     * Refreshes the rendered killshots track
     */
    @Override
    public void refreshKillshotsTrack() {
        try {
            ((GameController) currentController).updateKillshotsTrack();
        } catch (Exception ignore) {
            // The game screen is not shown, hence we ignore the error
        }
    }

    /**
     * Refreshes a rendered player board
     *
     * @param boardOwner The player owning the board to refresh
     */
    @Override
    public void refreshPlayerBoard(String boardOwner) {
        //TODO: Implement this method
    }

    /**
     * Refreshes the rendered actions track of the user
     */
    @Override
    public void refreshActionsTrack() {
        try {
            ((GameController) currentController).updateActionTrack();
        } catch (Exception ignore) {
            // The game screen is not shown, hence we ignore the error
        }
    }

    /**
     * Refreshes the whole rendered game board
     */
    @Override
    public void refreshGameBoard() {
        //TODO: Implement this method
    }

    /**
     * Refreshes the rendered game board by updating the provided squares
     *
     * @param squares The collection of squares to refresh
     */
    @Override
    public void refreshGameBoard(CoordPair... squares) {
        //TODO: Implement this method
    }

    @Override
    public void refreshAmmoInfo() {
        try {
            ((GameController) currentController).updateAmmoAmount();
        } catch (Exception e) {
            // The game screen is not shown, hence we ignore the error
        }
    }

    /**
     * Refreshes the rendered information about the match
     */
    @Override
    public void refreshMatchInfo() {
        try {
            ((GameController) currentController).updateMatchInfo();
        } catch (Exception ignore) {
            // The game screen is not shown, hence we ignore the error
        }
    }

    @Override
    public void refreshSpawnWeapons() {
        try {
            ((GameController) currentController).updateSpawnWeapons();
        } catch (Exception ignore) {
            // The game screen is not shown, hence we ignore the error
        }
    }

    @Override
    public void handle(ActionRequest request) {
        Platform.runLater(() -> {
            try {
                GameController gameController = (GameController) currentController;
                gameController.askActionSelection(request);
            } catch (Exception ignore) {
                // If this request is received when when are not in the game screen, we ignore it
            }
        });
    }

    @Override
    public void handle(BoardRequest request) {
        Platform.runLater(() -> {
            try {
                BoardRequestController boardRequestController = (BoardRequestController) showNewWindow("/fxml/BoardConfig.fxml", "Board configuration");
                if (boardRequestController != null) {
                    boardRequestController.setup(request);
                }
            } catch (Exception ignore) {
                // Errors are ignored
            }
        });
    }

    @Override
    public void handle(PlayerOperationRequest request) {
        Platform.runLater(() -> {
            try {
                GameController gameController = (GameController) currentController;
                gameController.askOperationSelection(request);
            } catch (Exception ignore) {
                // If this request is received when when are not in the game screen, we ignore it
            }
        });
    }

    @Override
    public void handle(PlayerRequest request) {
        Platform.runLater(() -> {
            try {
                PlayerRequestController playerRequestController = (PlayerRequestController) showNewWindow("/fxml/playerSelectionWindow.fxml", "Select player");
                if (playerRequestController != null) {
                    playerRequestController.setup(request);
                }
            } catch (Exception ignore) {
                // We ignore this error
            }
        });
    }

    @Override
    public void handle(PowerupCardRequest request) {
        Platform.runLater(() -> {
            try {
                PowerupRequestController powerupRequestController = (PowerupRequestController) showNewWindow("/fxml/powerupSelectionWindow.fxml", "Select powerup");
                if (powerupRequestController != null) {
                    powerupRequestController.setup(request);
                }
            } catch (Exception ignore) {
                // We ignore this error
            }
        });
    }

    @Override
    public void handle(SkullCountRequest request) {
        Platform.runLater(() -> {
            try {
                SkullsRequestController skullsRequestController = (SkullsRequestController) showNewWindow("/fxml/skullsSelectionWindow.fxml", "Skulls configuration");
                if (skullsRequestController != null) {
                    skullsRequestController.setup(request);
                }
            } catch (Exception ignore) {
                // Errors are ignored
            }
        });
    }

    @Override
    public void handle(SquareRequest request) {
        Platform.runLater(() -> {
            try {
                GameController gameController = (GameController) currentController;
                gameController.askSquareSelection(request);
            } catch (Exception ignore) {
                // We ignore errors when the request is received when the match screen is not yet shown
            }
        });
    }

    @Override
    public void handle(WeaponCardRequest request) {
        Platform.runLater(() -> {
            try {
                WeaponCardRequestController weaponCardRequestController = (WeaponCardRequestController) showNewWindow("/fxml/weaponCardSelectionWindow.fxml", "Select Weapon");
                if (weaponCardRequestController != null) {
                    weaponCardRequestController.setup(request);
                }
            } catch (Exception ignore) {
                // We ignore this error
            }
        });
    }

    @Override
    public void handle(EffectRequest request) {
        Platform.runLater(() -> {
            try {
                EffectRequestController effectRequestController = (EffectRequestController) showNewWindow("/fxml/effectSelectionWindow.fxml", "Select effect");
                if (effectRequestController != null) {
                    effectRequestController.setup(request);
                }
            } catch (Exception ignore) {
                // We ignore this error
            }
        });
    }

    /* ===== STATIC HELPER METHODS ===== */

    /**
     * Creates row constraints for the provided grid pane according to the provided heights
     *
     * @param targetPane  The pane to create the constraints into
     * @param rowsHeights The array containing the percentage height for each row
     */
    public static void setRowConstraints(GridPane targetPane, double[] rowsHeights) {
        for (double rowHeight : rowsHeights) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(rowHeight);
            targetPane.getRowConstraints().add(rowConstraints);
        }
    }

    /**
     * Creates column constraints for the provided grid pane according to the provided widths
     *
     * @param targetPane    The pane to create the constraints into
     * @param columnsWidths The array containing the percentage width for each column
     */
    public static void setColumnConstraints(GridPane targetPane, double[] columnsWidths) {
        for (double columnWidth : columnsWidths) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(columnWidth);
            targetPane.getColumnConstraints().add(columnConstraints);
        }
    }
}

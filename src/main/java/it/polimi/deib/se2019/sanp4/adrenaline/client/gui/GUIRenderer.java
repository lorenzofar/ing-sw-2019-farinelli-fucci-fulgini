package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.client.UIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Leaderboard;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A specialized class implementing the {@link UIRenderer} interface that provides methods to render content in a GUI environment
 *
 * @author Lorenzo Farinelli
 */
public class GUIRenderer extends Application implements UIRenderer {

    public static final String CSS_BOLD_TITLE = "title-text";
    private static final String ADRENALINE_STAGE_TITLE = "Adrenaline";

    /* ===== HEX CODES ===== */
    public static final String HEX_RED = "#f6453a";
    public static final String HEX_GREEN = "#38d257";

    private ClientView clientView;
    /**
     * The stage of the app to update scenes
     */
    private Stage stage;
    /**
     * The controller of the currently displayed scene
     */
    private GUIController currentController;

    private static Logger logger = Logger.getLogger(GUIRenderer.class.getName());

    @Override
    public void initialize() {
        launch();
    }

    /**
     * Sets the current scene with the provided FXML resource
     * It must be executed in the JavaFX application thread
     *
     * @param fxmlResource The path of the FXML file to display
     * @param fullScreen   {@code true} if the scene should be set in full screen, {@code false} otherwise
     * @throws IllegalStateException if it is called on a thread other than the JavaFX application thread
     */
    private synchronized void showScene(String fxmlResource, boolean fullScreen) {
        if (fxmlResource == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlResource));
            Scene scene = loader.load();
            currentController = loader.getController();

            // If a stage is already present, we hide it
            if (this.stage != null) {
                this.stage.hide();
            } else {
                this.stage = new Stage();
                this.stage.setTitle(ADRENALINE_STAGE_TITLE);
                this.stage.setResizable(true);
            }

            this.stage.setMaximized(fullScreen);

            this.stage.setOnCloseRequest((WindowEvent t) -> {
                Platform.exit();
                System.exit(0);
            });

            if (currentController != null) {
                // We attach the client view and the stage only to those scenes who have a controller
                currentController.setClientView(clientView);
                currentController.setStage(this.stage);
            }

            this.stage.setScene(scene);
            this.stage.show();
        } catch (IOException e) {
            // An error occurred loading the scene
            logger.log(Level.SEVERE, "Cannot load scene with resource {0}", fxmlResource);
            logger.log(Level.SEVERE, "Exception when loading scene", e);
        }
    }

    /**
     * Spawns a new windows with the provided title and showing the provided FXML file
     *
     * @param resource The path of the FXML file
     * @param title    The title of the window
     * @return The controller associated to the newly created window
     */
    GUIController showNewWindow(String resource, String title) {
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
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Cannot load new window with resource {0}", resource);
            logger.log(Level.SEVERE, "Exception when loading new window", e);
            return null;
        }
    }

    /**
     * Updates the stage info (title and icon) after showing a scene
     *
     * @param title The title of the stage
     */
    private void updateStageInfo(String title) {
        if (this.stage != null) {
            this.stage.setTitle(title);
            this.stage.getIcons().clear();
            this.stage.getIcons().add(new Image("/adrenaline_icon.png"));
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        this.clientView = new ClientView();
        clientView.setRenderer(this);

        showScene("/fxml/login.fxml", false);
        updateStageInfo(ADRENALINE_STAGE_TITLE);
    }

    /* ===== LOBBY ===== */

    @Override
    public void showLobby() {
        Platform.runLater(() -> {
            showScene("/fxml/lobby.fxml", false);
            updateStageInfo("Adrenaline lobby");
        });
    }

    @Override
    public void updateLobby(Collection<String> connectedPlayers, boolean matchStarting) {
        Platform.runLater(() -> {
            try {
                LobbyController lobbyController = (LobbyController) currentController;
                lobbyController.setConnectedPlayers(connectedPlayers);
                lobbyController.setMatchStarting(matchStarting);
            } catch (Exception e) {
                // If the previous calls fail, it means the lobby is not yet initialized
                logger.log(Level.WARNING, "Error when updating lobby, showing it again");
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
        Platform.runLater(() -> {
            try {
                ((GameController) currentController).refreshMatchScreen();
            } catch (Exception e) {
                logger.log(Level.INFO, "Game screen was not loaded, we show it for the first time");
                showScene("/fxml/game.fxml", true);
                updateStageInfo(ADRENALINE_STAGE_TITLE);
                ((GameController) currentController).buildMatchScreen();
            }
        });
    }

    @Override
    public void showDrawnWeapon(WeaponCard weapon) {
        Platform.runLater(() -> {
            ImageViewerController imageViewerController = (ImageViewerController) showNewWindow("/fxml/imageViewer.fxml", "New weapon");
            imageViewerController.setCaption("You have just drawn this weapon:");
            imageViewerController.setImage(String.format("/images/weapons/%s.png", weapon.getId()));
        });
    }

    @Override
    public void showDrawnPowerup(PowerupCard powerup) {
        Platform.runLater(() -> {
            ImageViewerController imageViewerController = (ImageViewerController) showNewWindow("/fxml/imageViewer.fxml", "New powerup");
            imageViewerController.setCaption("You have just drawn this powerup:");
            imageViewerController.setImage(String.format("/images/powerups/%s_%s.png", powerup.getType().name().toLowerCase(), powerup.getCubeColor().name().toLowerCase()));
        });
    }


    /**
     * Shows a message to the user
     *
     * @param text The text of the message
     * @param type The type of the message
     */
    @Override
    public void showMessage(String text, MessageType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(convertAlertType(type));
            alert.getButtonTypes().set(0, ButtonType.OK);
            Text alertText = new Text(text);
            alertText.setWrappingWidth(alert.getDialogPane().getWidth() - 24);
            alertText.setTextAlignment(TextAlignment.CENTER);
            alert.getDialogPane().setContent(alertText);
            alert.show();
        });
    }

    @Override
    public void cancelSelection() {
        // We set a null selection handler and, incidentally, we cancel the existing one (if present)
        clientView.setSelectionHandler(null);
        // We reset all the messages shown to the user
        if (clientView.getScene().isGameScene()) {
            Platform.runLater(() -> {
                try {
                    ((GameController) currentController).resetRequestMessages();
                } catch (Exception e) {
                    logger.log(Level.INFO, "Failed to reset game request messages");
                }
            });
        }
    }

    /**
     * Shows a screen to tell the user that the client is going to be closed due to a fault
     *
     * @param title   The title of the screen
     * @param message The message to show to the user
     */
    @Override
    public void showPreemptionScreen(String title, String message) {
        Platform.runLater(() ->
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getButtonTypes().set(0, ButtonType.OK);
            Text alertText = new Text(message);
            alertText.setWrappingWidth(alert.getDialogPane().getWidth() - 24);
            alertText.setTextAlignment(TextAlignment.CENTER);
            alert.getDialogPane().setContent(alertText);
            alert.showAndWait();
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Shows a screen to tell the user that he is about to rejoin the match
     */
    @Override
    public void showRejoinScreen() {
        Platform.runLater(() -> showScene("/fxml/rejoin.fxml", false));
    }

    @Override
    public void showLeaderBoard() {
        Platform.runLater(() -> {
            showScene("/fxml/leaderboard.fxml", true);
            updateStageInfo(ADRENALINE_STAGE_TITLE);
            if (clientView.getModelManager().getLeaderboard() != null) {
                ((LeaderboardController) currentController).updateLeaderBoard(clientView.getModelManager().getLeaderboard());
            }
        });
    }

    @Override
    public void updateLeaderBoard(Leaderboard leaderboard) {
        if (clientView.getScene() != ViewScene.FINAL_SCORES) {
            clientView.selectScene(ViewScene.FINAL_SCORES);
        }
        Platform.runLater(() -> {
            try {
                ((LeaderboardController) currentController).updateLeaderBoard(leaderboard);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error when updating leaderboard scene");
            }
        });
    }

    @Override
    public void setIdleScreen() {
        showMatchScreen();
    }

    @Override
    public void setActiveScreen() {
        showMatchScreen();
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
        try {
            ((GameController) currentController).updatePlayerBoard(boardOwner);
        } catch (Exception ignore) {
            // The game screen is not shown, hence we ignore the error
        }
    }

    /**
     * Refreshes the whole rendered game board
     */
    @Override
    public void refreshGameBoard() {
        Platform.runLater(() -> {
            try {
                ((GameController) currentController).updateBoard();
            } catch (Exception ignore) {
                // The game screen is not shown, hence we ignore the error
            }
        });
    }

    /**
     * Refreshes the rendered game board by updating the provided squares
     *
     * @param squares The collection of squares to refresh
     */
    @Override
    public void refreshGameBoard(CoordPair... squares) {
        Platform.runLater(() -> {
            try {
                // Update all the provided squares
                for (CoordPair square : squares) {
                    ((GameController) currentController).updateSquareOverlay(square);
                }
            } catch (Exception ignore) {
                // The game screen is not shown, hence we ignore the error
            }
        });
    }

    @Override
    public void refreshAmmoInfo() {
        Platform.runLater(() -> {
            try {
                ((GameController) currentController).updateAmmoAmount();
            } catch (Exception e) {
                // The game screen is not shown, hence we ignore the error
            }
        });
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
    public void refreshOwnedWeapons() {
        Platform.runLater(() -> {
            try {
                ((GameController) currentController).updateWeaponsInfo();
            } catch (Exception ignore) {
                // The game screen is not shown, hence we ignore the error
            }
        });
    }

    @Override
    public void refreshOwnedPowerups() {
        Platform.runLater(() -> {
            try {
                ((GameController) currentController).updateOwnedPowerups();
            } catch (Exception ignore) {
                // The game screen is not shown, hence we ignore the error
            }
        });
    }

    @Override
    public void handle(ActionRequest request) {
        Platform.runLater(() -> {
            try {
                ActionRequestController actionRequestController = (ActionRequestController) showNewWindow("/fxml/actionSelectionWindow.fxml", "Action selection");
                if (actionRequestController != null) {
                    actionRequestController.setup(request);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error during action request", e);
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
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error during board request", e);
            }
        });
    }

    @Override
    public void handle(PlayerOperationRequest request) {
        Platform.runLater(() -> {
            try {
                PlayerOperationRequestController operationRequestController = (PlayerOperationRequestController) showNewWindow("/fxml/playerOperationSelectionWindow.fxml", "Operation selection");
                if (operationRequestController != null) {
                    operationRequestController.setup(request);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error during player operation request", e);
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
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error during player request", e);
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
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error during powerup request", e);
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
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error during skulls count request", e);
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
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error during weapon card request", e);
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
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error during effect request", e);
            }
        });
    }

    /* ===== STATIC HELPER METHODS ===== */

    /**
     * Converts a MessageType to a JavaFX AlertType
     *
     * @param messageType The object representing the message type
     * @return The object representing the alert type
     */
    private Alert.AlertType convertAlertType(MessageType messageType) {
        switch (messageType) {
            case WARNING:
                return Alert.AlertType.WARNING;
            case ERROR:
                return Alert.AlertType.ERROR;
            default:
                return Alert.AlertType.INFORMATION;
        }
    }

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

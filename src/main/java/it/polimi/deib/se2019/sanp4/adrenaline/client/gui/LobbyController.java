package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;


public class LobbyController extends GUIController {

    @FXML
    private VBox lobbyScene;
    @FXML
    private ListView<String> connectedPlayersListView;
    @FXML
    private ImageView adrenalineLogo;
    @FXML
    private VBox connectedPlayersContainer;
    @FXML
    private VBox matchStartIndicator;

    private ListProperty<String> connectedPlayers = new SimpleListProperty<>();

    private BooleanProperty matchStarting = new SimpleBooleanProperty(false);

    @FXML
    public void initialize() {
        connectedPlayers.set(FXCollections.observableArrayList());
        connectedPlayersListView.itemsProperty().bind(connectedPlayers);
        adrenalineLogo.fitWidthProperty().bind(lobbyScene.widthProperty().subtract(50)); // Remove padding to avoid exceeding bounds
        connectedPlayersContainer.visibleProperty().bind(matchStarting.not());
        matchStartIndicator.visibleProperty().bind(matchStarting);
    }

    /**
     * Add the provided player to the list of connected ones
     * @param username The username of the player, not null
     */
    public void addConnectedPlayer(String username){
        if(username == null){
            throw new NullPointerException("Username cannot be null");
        }
        connectedPlayers.get().add(username);
    }

    /**
     * Remove the provdided player from the list of connected ones
     * @param username The username of the player, not null
     */
    public void removeConnectedPlayer(String username){
        if(username == null){
            throw new NullPointerException("Username cannot be null");
        }
        connectedPlayers.get().remove(username);
    }

    /**
     * Inform user about the imminent start of the game
     * Show a progress ring and a loading message
     *
     */
    public void showMatchWaiting(){
        matchStarting.set(true);

    }

    /**
     * Remove the progress ring for match waiting when the game start is cancelled
     */
    public void hideMatchWaiting(){
        matchStarting.set(false);
    }

    //TODO: Listen for connected/disconnected events to update the list of connected players

}

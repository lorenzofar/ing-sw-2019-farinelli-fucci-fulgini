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

import java.util.Collection;


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
     * Sets the players connected to the lobby
     * @param connectedPlayers The list of players
     */
    void setConnectedPlayers(Collection<String> connectedPlayers) {
        this.connectedPlayers.clear();
        this.connectedPlayers.setAll(connectedPlayers);
    }

    /**
     * Sets whether the match is about to start or not
     * @param matchStarting {@code true} if the match is starting, {@code false} otherwise
     */
    void setMatchStarting(boolean matchStarting){
        this.matchStarting.set(matchStarting);
    }
}
package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Map;

/**
 * A custom control based on a VBox showing information about the match, namely:
 * <ul>
 * <li>Name of the player that is playing in the current turn</li>
 * <li>Score of the user</li>
 * <li>List of participating players along with their character color</li>
 * </ul>
 */
public class MatchInfoPane extends VBox {
    /**
     * A property to store the score of the user
     */
    private IntegerProperty scoreProperty;
    /**
     * A property to store the name of the player owning the current turn
     */
    private StringProperty currentPlayerProperty;
    /**
     * Box containing the list of players
     */
    private VBox playersContainer;

    public MatchInfoPane() {
        super();
        this.getStylesheets().add("/fxml/style.css");
        scoreProperty = new SimpleIntegerProperty(0);
        currentPlayerProperty = new SimpleStringProperty("");

        /* ===== LAYOUT BUILDING ===== */

        // First set the spacing
        this.setSpacing(8);

        // Then create the header for the current player
        Label currentPlayerHeader = new Label("Current player");
        currentPlayerHeader.getStyleClass().add(GUIRenderer.CSS_BOLD_TITLE);
        this.getChildren().add(currentPlayerHeader);

        Label currentPlayerLabel = new Label();
        currentPlayerHeader.textProperty().bind(currentPlayerProperty);
        this.getChildren().add(currentPlayerLabel);

        // Then create the score header
        Label scoreHeader = new Label("Your score");
        scoreHeader.getStyleClass().add(GUIRenderer.CSS_BOLD_TITLE);
        this.getChildren().add(scoreHeader);

        // Then the label holding the score count
        Label scoreLabel = new Label();
        scoreLabel.textProperty().bind(scoreProperty.asString());
        this.getChildren().add(scoreLabel);

        // Then the header for the players list
        Label playersHeader = new Label("Players");
        playersHeader.getStyleClass().add(GUIRenderer.CSS_BOLD_TITLE);
        this.getChildren().add(playersHeader);

        // Then create the container for the list of players
        // Add a listener to the players property, in order to update the container's content every time the map is updated
        // For each player we create an horizontal box with:
        // * the name of the player
        // * a circle filled with his color
        playersContainer = new VBox();
        this.getChildren().add(playersContainer);
    }

    /**
     * Sets the score of the user
     *
     * @param score The score of the user
     */
    public void setScore(int score) {
        scoreProperty.setValue(score);
    }

    /**
     * Sets the players participating in the match
     *
     * @param players The map containing the username of players and the associated character color
     */
    public void setPlayers(Map<String, PlayerColor> players) {
        playersContainer.getChildren().clear();
        players.forEach((name, color) ->
                {
                    HBox playerItem = new HBox(
                            8,
                            new Circle(4, Color.web(color.getHexCode())),
                            new Label(name)
                    );
                    playerItem.setAlignment(Pos.CENTER_LEFT);
                    playersContainer.getChildren().add(playerItem);
                }
        );
    }

    /**
     * Sets the player that is currently playing
     *
     * @param player The username of the player
     */
    public void setCurrentPlayer(String player) {
        currentPlayerProperty.setValue(player);
    }
}
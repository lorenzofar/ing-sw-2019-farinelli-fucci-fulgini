package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Leaderboard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

/**
 * The controller for the window showing the final scores and the match leaderboard.
 * For each player it provides information about:
 * <ul>
 * <li>The position in the leaderboard</li>
 * <li>The final score</li>
 * <li>The count of performed killshots</li>
 * <li>The count of performed overkills</li>
 * <li>The number of deaths</li>
 * </ul>
 *
 * @author Lorenzo Farinelli
 */
public class LeaderboardController extends GUIController {

    @FXML
    private VBox leaderBoardContainer;

    /**
     * Creates a grid pane to show the information contained in a leaderboard entry
     *
     * @param entry    The object representing the entry
     * @param position The leaderboard position of the entry
     * @return The GridPane containing the rendered information
     */
    private GridPane renderLeaderBoardEntry(Leaderboard.Entry entry, int position) {
        // We retrieve the color of the player
        ColoredObject playerColor = clientView.getModelManager().getPlayersColors().get(entry.getName());

        GridPane entryPane = new GridPane();
        // If the player is the winner of the match we add a class to its entry
        if (position == 1) {
            entryPane.getStyleClass().add("leaderboard-winner-entry");
        }
        entryPane.setHgap(12);
        entryPane.setPadding(new Insets(12));
        entryPane.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < 4; i++) {
            entryPane.getColumnConstraints().add(new ColumnConstraints());
        }
        // Get the third column, that contains the player's name and set hgrow priority to always
        entryPane.getColumnConstraints().get(2).setHgrow(Priority.ALWAYS);

        // Create a label to show the position in the leaderboard of the player
        Label positionLabel = new Label(String.valueOf(position));
        positionLabel.getStyleClass().add("leaderboard-position-label");
        GridPane.setColumnIndex(positionLabel, 0);
        entryPane.getChildren().add(positionLabel);

        // Create a round image of the character
        Circle playerPic = new Circle(18);
        Image characterImage = new Image(
                String.format("/images/players/%s.png", ((PlayerColor) playerColor).name().toLowerCase()),
                36, 36, true, true);
        playerPic.setFill(new ImagePattern(characterImage));
        playerPic.getStyleClass().add("shadowed");
        GridPane.setColumnIndex(playerPic, 1);
        entryPane.getChildren().add(playerPic);

        // Create a container to host:
        // * the player name
        // * the count of killshots, overkills and deaths
        VBox playerInfoContainer = new VBox(4);
        playerInfoContainer.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(entry.getName());
        nameLabel.getStyleClass().add(GUIRenderer.CSS_BOLD_TITLE);
        nameLabel.setTextFill(Color.web(playerColor.getHexCode()));
        playerInfoContainer.getChildren().add(nameLabel);
        playerInfoContainer.getChildren().add(
                new Label(String.format("%d killshots, %d overkills, %d deaths",
                        entry.getPerformedKillshots(),
                        entry.getPerformedOverkills(),
                        entry.getDeaths())));
        GridPane.setColumnIndex(playerInfoContainer, 2);
        entryPane.getChildren().add(playerInfoContainer);

        // Create a label for the score of the player
        Label scoreLabel = new Label(String.valueOf(entry.getScore()));
        scoreLabel.getStyleClass().add("leaderboard-score-label");
        GridPane.setColumnIndex(scoreLabel, 3);
        entryPane.getChildren().add(scoreLabel);
        // Eventually return the pane
        return entryPane;
    }

    /**
     * Updates the content of the displayed leaderboard to match the provided one
     *
     * @param leaderboard The object representing the leaderboard
     */
    void updateLeaderBoard(Leaderboard leaderboard) {
        // First remove all the previous entries (if present)
        leaderBoardContainer.getChildren().clear();
        // Then for each entry add a pane
        int i = 1;
        for (Leaderboard.Entry entry : leaderboard.getEntries()) {
            leaderBoardContainer.getChildren().add(renderLeaderBoardEntry(entry, i));
            i++;
        }
    }
}

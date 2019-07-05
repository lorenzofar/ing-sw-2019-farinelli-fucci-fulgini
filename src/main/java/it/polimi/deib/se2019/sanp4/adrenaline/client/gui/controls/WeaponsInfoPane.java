package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A custom control extending a {@link VBox} representing a table showing information about the weapons owned by the players
 * It also allows to set a consumer of the image ids to handle the events generated when the user clicks on the "info" button of a weapon
 *
 * @author Lorenzo Farinelli
 */
public class WeaponsInfoPane extends VBox {

    /**
     * The list of all the weapons owned by each player
     */
    private Map<String, List<WeaponCard>> playersWeapons;
    /**
     * The image viewer consumer to be called when expanding weapon info
     */
    private Consumer<String> imageViewer;

    public WeaponsInfoPane() {
        super();
        this.setSpacing(8);
        playersWeapons = new HashMap<>();
    }

    /**
     * Renders an entry of the table, showing information for the provided weapon card
     *
     * @param weaponCard The object representing the weapon card
     * @return The HBox node representing the entry
     */
    private HBox renderEntry(WeaponCard weaponCard) {
        HBox entryContainer = new HBox(8);
        entryContainer.setAlignment(Pos.CENTER_LEFT);
        // Create an indicator for the weapon usability
        Circle weaponStateIndicator = new Circle(4);
        weaponStateIndicator.setFill(Color.web(weaponCard.isUsable() ? GUIRenderer.HEX_GREEN : GUIRenderer.HEX_RED));
        entryContainer.getChildren().add(weaponStateIndicator);
        // Add a label with the name of the weapon
        entryContainer.getChildren().add(new Label(weaponCard.getName()));

        // Then add a button that, when clicked, triggers the image viewer consumer
        Button weaponInfoButton = new Button("info");
        entryContainer.getChildren().add(weaponInfoButton);
        if (imageViewer != null) {
            // Attach the image viewer as an action event handler
            weaponInfoButton.setOnAction(action -> imageViewer.accept(weaponCard.getId()));
        }
        return entryContainer;
    }

    /**
     * Updates the rendered content according to the current information
     */
    private void updateContent() {
        // We first clear the content
        this.getChildren().clear();
        // Then for each of the players
        playersWeapons.keySet().forEach(player -> {
            // We create its header
            Label playerLabel = new Label(player);
            playerLabel.getStyleClass().add(GUIRenderer.CSS_BOLD_TITLE);
            this.getChildren().add(playerLabel);
            // And attach all the entries corresponding to the owned weapons
            // Putting a placeholder instead if no weapon is owned
            if (playersWeapons.get(player).isEmpty()) {
                this.getChildren().add(new Label("No owned weapons"));
            } else {
                playersWeapons.get(player).forEach(weaponCard ->
                        this.getChildren().add(renderEntry(weaponCard))
                );
            }
        });
    }

    /**
     * Sets the weapons belonging to each player
     *
     * @param playersWeapons The map associating each player to the list of owned weapons
     */
    public void setPlayersWeapons(Map<String, List<WeaponCard>> playersWeapons) {
        this.playersWeapons = playersWeapons;
        updateContent();
    }

    /**
     * Sets the consumer that accepts the weapon id to show its card representation
     *
     * @param imageViewer The consumer of the weapon id
     */
    public void setImageViewer(Consumer<String> imageViewer) {
        this.imageViewer = imageViewer;
    }
}

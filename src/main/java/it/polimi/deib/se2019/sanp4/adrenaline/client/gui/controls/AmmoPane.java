package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Map;

/**
 * A custom control to show information about the ammo owned by the user
 */
public class AmmoPane extends VBox {

    /**
     * A map to keep track of the labels showing the amount for each cube
     */
    private Map<AmmoCube, Label> cubeLabels;

    AmmoPane() {
        super();
        this.setSpacing(8);
        this.getStylesheets().add("/fxml/style.css");

        Label header = new Label("Available ammo");
        header.getStyleClass().add(GUIRenderer.CSS_BOLD_TITLE);
        this.getChildren().add(header);

        // Then for each cube color, create a label and add to the corresponding map
        for (AmmoCube cube : AmmoCube.values()) {
            // Create a container
            HBox cubeContainer = new HBox(4);
            // Add a square with the color of the cube
            cubeContainer.getChildren().add(new Rectangle(4, 4, Color.web(cube.getHexCode())));
            // Then add a label to show the amount
            Label cubeLabel = new Label("0");
            cubeContainer.getChildren().add(cubeLabel);
            // And save it in the map
            cubeLabels.put(cube, cubeLabel);
        }
    }

    /**
     * Sets the ammo owned by the player
     *
     * @param ammo A map in which every cube color is related to the quantity of those cubes owned by the player
     */
    public void setAmmo(Map<AmmoCube, Integer> ammo) {
        // For each of the cubes contained in the map,
        // retrieve the corresponding label and update the amount
        ammo.entrySet().forEach(e -> {
            // Retrieve the corresponding label
            Label cubeLabel = cubeLabels.get(e.getKey());
            // And if is not null, update its value
            // A null retrieved label would never happen, however, since
            // both the client and the server share the same list of cube colors
            if (cubeLabel != null) {
                cubeLabel.setText(e.getValue().toString());
            }
        });
    }
}

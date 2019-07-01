package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.EnumMap;
import java.util.Map;

/**
 * A custom control to show information about the ammo owned by the user
 */
public class AmmoPane extends VBox {

    /**
     * A map to keep track of the labels showing the amount for each cube
     */
    private Map<AmmoCube, Label> cubeLabels;

    public AmmoPane() {
        super();
        this.setSpacing(8);
        this.getStylesheets().add("/fxml/style.css");
        // Create the header of the pane
        Label header = new Label("Ammo");
        header.getStyleClass().add(GUIRenderer.CSS_BOLD_TITLE);
        // Initialize the map storing the labels
        cubeLabels = new EnumMap<>(AmmoCube.class);
        HBox cubesContainer = new HBox(8);
        this.getChildren().addAll(header, cubesContainer);
        // Then for each cube color, create a label and add to the corresponding map
        for (AmmoCube cube : AmmoCube.values()) {
            // Create a container
            StackPane cubeContainer = new StackPane();
            // Add a square with the color of the cube
            Rectangle ammoCube = new Rectangle(24, 24, Color.web(cube.getHexCode(), 0.7));
            // Then add a label to show the amount
            Label cubeLabel = new Label("0");
            // Save in the map
            cubeLabels.put(cube, cubeLabel);
            // And add to the corresponding parent
            cubeContainer.getChildren().addAll(ammoCube, cubeLabel);
            cubesContainer.getChildren().add(cubeContainer);
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
        ammo.forEach((cube, amount) -> {
            // Retrieve the corresponding label
            Label cubeLabel = cubeLabels.get(cube);
            // And if is not null, update its value
            // A null retrieved label would never happen, however, since
            // both the client and the server share the same list of cube colors
            if (cubeLabel != null) {
                cubeLabel.setText(amount.toString());
            }
        });
    }
}

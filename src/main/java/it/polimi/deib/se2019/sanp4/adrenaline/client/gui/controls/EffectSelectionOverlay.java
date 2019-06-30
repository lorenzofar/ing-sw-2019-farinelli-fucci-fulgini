package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.EffectDescription;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.stream.Collectors;

/**
 * A custom control describing the visual representation of the effect of a weapon
 */
public class EffectSelectionOverlay extends SelectableOverlay<EffectDescription> {

    @FXML
    private GridPane effectContent;

    @FXML
    private Label effectName;

    @FXML
    private Text effectDescription;

    @FXML
    private HBox costContainer;

    @FXML
    private Button overlayRoot;

    public EffectSelectionOverlay() {
        super("/fxml/controls/effectSelectionOverlay.fxml");
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
    }

    public void setEffect(EffectDescription effect) {
        this.setData(effect);
        costContainer.getChildren().clear();

        if (effect == null) {
            this.effectName.textProperty().set("None of them");
            this.effectDescription.textProperty().set("Do not select any effect");
        } else {
            this.effectName.textProperty().set(effect.getName());
            this.effectDescription.textProperty().set(effect.getDescription());
            this.effectDescription.setWrappingWidth(300);
            // Set the cost of the effect
            // Take the cost container and add as many colored circles as the cubes in the list
            costContainer.getChildren().addAll(
                    effect.getCost().stream().map(cube -> {
                        Circle cubeIndicator = new Circle(8);
                        cubeIndicator.setFill(Color.web(cube.getHexCode()));
                        return cubeIndicator;
                    }).collect(Collectors.toList())
            );
        }
        overlayRoot.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        overlayRoot.setGraphic(effectContent);
    }
}

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
 * A custom control used to select effects of a weapon, showing information about:
 * <ul>
 * <li>The name of the effect</li>
 * <li>The description of the effect</li>
 * <li>The cost of the effect in terms of ammo cubes</li>
 * </ul>
 *
 * @author Lorenzo Farinelli
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

    /**
     * Sets the effect associated to the overlay.
     * If a null object is passed, the overlay is associated to no effectsto be used when the user can
     * choose not to select any effect
     *
     * @param effect The object representing the effect
     */
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

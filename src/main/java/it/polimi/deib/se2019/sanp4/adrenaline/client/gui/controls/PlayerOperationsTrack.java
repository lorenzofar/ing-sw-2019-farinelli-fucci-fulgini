package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerOperationEnum;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A custom control extending an HBox to represent a track containing available player operations
 */
public class PlayerOperationsTrack extends VBox {
    public PlayerOperationsTrack() {
        super();
        this.setSpacing(8);
        this.getStylesheets().add("/fxml/style.css");

        // We retrieve all the supported operations
        List<PlayerOperationEnum> operations = Arrays.asList(PlayerOperationEnum.values());
        // We add the null one, that lets the player do nothing (when enabled)
        operations.add(null);
        // And we create overlays for them, adding eventually to the container
        operations.forEach(operation -> {
            PlayerOperationOverlay overlay = new PlayerOperationOverlay();
            overlay.setOperation(operation);
            this.getChildren().add(overlay);
        });
    }

    /**
     * Retrieves the overlays that can be selected according to the provided list of available operations
     *
     * @param operations The list of objects representing the operations
     */
    public Collection<SelectableOverlay<PlayerOperationEnum>> getSelectableOverlays(Collection<PlayerOperationEnum> operations) {
        // We iterate over our children and retrieve only those overlays whose data are among the available operations
        return this.getChildren()
                .stream()
                .map(node -> (PlayerOperationOverlay) node)
                .filter(overlay -> operations.contains(overlay.getData()))
                .collect(Collectors.toList());
    }
}

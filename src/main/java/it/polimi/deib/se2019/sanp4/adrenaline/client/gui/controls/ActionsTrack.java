package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import javafx.scene.layout.HBox;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * A custom control extending an HBox to represent a track containing available actions
 */
public class ActionsTrack extends HBox {

    public ActionsTrack(){
        super();
        this.setSpacing(8);
        this.getStylesheets().add("/fxml/style.css");

        for(ActionEnum action : ActionEnum.values()){
            ActionOverlay overlay = new ActionOverlay();
            overlay.setAction(action);
            this.getChildren().add(overlay);
        }

        ActionOverlay noActionOverlay = new ActionOverlay();
        noActionOverlay.setAction(null);
        this.getChildren().add(noActionOverlay);
    }

    /**
     * Retrieves the overlays that can be selected according to the provided list of available actions
     *
     * @param actions The list of objects representing the actions
     */
    public Collection<SelectableOverlay<ActionEnum>> getSelectableOverlays(Collection<ActionEnum> actions){
        return this.getChildren()
                .stream()
                .map(node -> (ActionOverlay) node)
                .filter(overlay -> actions.contains(overlay.getData()))
                .collect(Collectors.toList());
    }
}

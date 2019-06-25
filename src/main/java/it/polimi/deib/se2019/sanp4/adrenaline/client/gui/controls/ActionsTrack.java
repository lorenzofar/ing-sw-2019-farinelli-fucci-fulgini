package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.ActionCardView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import javafx.scene.layout.HBox;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * A custom control extending an HBox to represent a track containing available actions
 */
public class ActionsTrack extends HBox {

    public ActionsTrack() {
        super();
        this.setSpacing(8);
        this.getStylesheets().add("/fxml/style.css");
    }

    /**
     * Sets the action card that provides the supported actions
     *
     * @param actionCard The object representing the action card
     */
    public void setActionCard(ActionCardView actionCard) {
        if (actionCard == null) {
            return;
        }
        // First remove previous children
        this.getChildren().clear();
        // Then for each of the available actions, create an overlay and append to the container
        for (ActionEnum action : actionCard.getActions()) {
            ActionOverlay overlay = new ActionOverlay();
            overlay.setAction(action);
            this.getChildren().add(overlay);
        }
        // Then check whether a final action is present and add an overlay accordingly
        if (actionCard.getFinalAction() != null) {
            ActionOverlay finalActionOverlay = new ActionOverlay();
            finalActionOverlay.setAction(actionCard.getFinalAction());
            this.getChildren().add(finalActionOverlay);
        }
    }

    /**
     * Retrieves the overlays that can be selected according to the provided list of available actions
     *
     * @param actions The list of objects representing the actions
     * @return The collection of selectable overlays that correspond to the provided actions
     */
    public Collection<SelectableOverlay<ActionEnum>> getSelectableOverlays(Collection<ActionEnum> actions) {
        return this.getChildren()
                .stream()
                .map(node -> (ActionOverlay) node)
                .filter(overlay -> actions.contains(overlay.getData()))
                .collect(Collectors.toList());
    }
}

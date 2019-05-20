package it.polimi.deib.se2019.sanp4.adrenaline.client.modelviews;

import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;

import java.util.Collection;

/**
 * A lightweight representation of an action card in the view
 */
public class ActionCardView {

    private ActionCardEnum type;
    private Collection<ActionEnum> actions;
    private ActionEnum finalAction;

    public ActionCardView(ActionCardEnum type, Collection<ActionEnum> actions, ActionEnum finalAction) {
        //TODO: Check how to handle improper parameters
        this.type = type;
        this.actions = actions;
        this.finalAction = finalAction;
    }

    /**
     * Retrieves the type of the action card
     *
     * @return The object representing the type
     */
    public ActionCardEnum getType() {
        return type;
    }

    /**
     * Retrieves the actions
     *
     * @return The collection of object representing the actions
     */
    public Collection<ActionEnum> getActions() {
        return actions;
    }

    /**
     * Retrieves final action
     *
     * @return The object representing the action if present, {@code null} otherwise
     */
    public ActionEnum getFinalAction() {
        return finalAction;
    }
}

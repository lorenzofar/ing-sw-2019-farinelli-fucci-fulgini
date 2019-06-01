package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;

import java.io.Serializable;
import java.util.Collection;

/**
 * A lightweight representation of an action card in the view
 */
public class ActionCardView implements Serializable {

    private static final long serialVersionUID = -7594618211509042887L;
    private ActionCardEnum type;
    private Collection<ActionEnum> actions;
    private ActionEnum finalAction;

    /**
     * Creates a new action card view
     * @param type the type of the action card
     * @param actions the available actions
     * @param finalAction the final action
     */
    @JsonCreator
    public ActionCardView(
            @JsonProperty("actionCardEnum") ActionCardEnum type,
            @JsonProperty("actions") Collection<ActionEnum> actions,
            @JsonProperty("finalAction") ActionEnum finalAction) {
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

    /**
     * Sets the type of the action card
     * If a null object is provided, nothing happens
     *
     * @param type The object representing the action card
     */
    public void setType(ActionCardEnum type) {
        if (type != null) {
            this.type = type;
        }
    }

    /**
     * Sets the actions
     * If a null object is provided, nothing happens
     *
     * @param actions The collection of objects representing the actions
     */
    public void setActions(Collection<ActionEnum> actions) {
        if (actions != null && !actions.contains(null)) {
            this.actions = actions;
        }
    }

    /**
     * Sets the final action
     * If a null object is provided, nothing happens
     *
     * @param finalAction The object representing the final action
     */
    public void setFinalAction(ActionEnum finalAction) {
        if (finalAction != null) {
            this.finalAction = finalAction;
        }
    }
}

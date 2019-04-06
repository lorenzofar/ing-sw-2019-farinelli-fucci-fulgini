package it.polimi.deib.se2019.sanp4.adrenaline.model.action;

import java.util.Collection;
import java.util.Collections;

/**
 * Holds a representation of the actions the player can perform.
 * The actions can be distinguished in:
 * <ul>
 *     <li>Normal actions, of which a number indicated by {@link #maxActions} can be performed during turn</li>
 *     <li>A final action (which is optional) that can be performed when the normal actions are over, before
 *     passing the turn to the next player </li>
 * </ul>
 */
public class ActionCard {

    private final int maxActions;

    private final ActionCardEnum type;

    private final Collection<ActionEnum> actions;

    private final ActionEnum finalAction;

    /**
     * Construct the action card, it will be totally immutable.
     * @param maxActions Maximum number of actions performable during a turn, also referred to as <i>multiplier</i>
     * @param type Identifier of the type of action card
     * @param actions Collection of "normal" actions
     * @param finalAction Optional "final" action
     */
    public ActionCard(int maxActions, ActionCardEnum type, Collection<ActionEnum> actions, ActionEnum finalAction) {
        this.maxActions = maxActions;
        this.type = type;
        this.actions = Collections.unmodifiableCollection(actions);
        this.finalAction = finalAction;
    }

    /**
     * Returns maximum number of actions performable by player during turn.
     * @return maximum actions per turn (multiplier)
     */
    public int getMaxActions() {
        return maxActions;
    }

    /**
     * Returns the type of action card which identifies when it is used.
     * @return action card type
     */
    public ActionCardEnum getType() {
        return type;
    }

    /**
     * Returns the "normal" actions in this card.
     * @return <b>unmodifiable</b> collection of actions
      */
    public Collection<ActionEnum> getActions() {
        return actions;
    }

    /**
     * Returns wether an action is a "normal" action in this card or not.
     * @param action the action you want to check
     * @return {@code true} if it is a "normal" action, {@code false} otherwise
     */
    public boolean hasAction(ActionEnum action) {
        return actions.contains(action);
    }

    /**
     * Returns final action.
     * @return final action, if any, {@code null} otherwise
     */
    public ActionEnum getFinalAction() {
        return finalAction;
    }

    /**
     * Returns whether this card has a final action or not.
     * @return {@code true} if it has a final action, {@code false} otherwise
     */
    public boolean hasFinalAction() {
        return finalAction != null;
    }
}

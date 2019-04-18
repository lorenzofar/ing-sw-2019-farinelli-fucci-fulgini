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

    private int maxActions;

    private ActionCardEnum type;

    private Collection<ActionEnum> actions;

    private ActionEnum finalAction;

    /** Default constructor only to be used by Jackson */
    private ActionCard(){}

    /**
     * Construct the action card, it will be totally immutable.
     * @param maxActions Maximum number of actions performable during a turn, also referred to as <i>multiplier</i>, must be positive
     * @param type Identifier of the type of action card, not null
     * @param actions Collection of "normal" actions, not null and not empty
     * @param finalAction Optional "final" action, can be null
     */
    public ActionCard(int maxActions, ActionCardEnum type, Collection<ActionEnum> actions, ActionEnum finalAction) {
        if(type == null || actions == null){
            throw new NullPointerException("Found null parameters");
        }
        if(maxActions < 0){
            throw new IllegalArgumentException("Maximum number of actions cannot be negative");
        }
        if(actions.isEmpty()){
            throw new IllegalArgumentException("The list of actions cannot be empty");
        }
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
        return Collections.unmodifiableCollection(actions);
    }

    /**
     * Returns wether an action is a "normal" action in this card or not.
     * @param action the action you want to check, not null
     * @return {@code true} if it is a "normal" action, {@code false} otherwise
     */
    public boolean hasAction(ActionEnum action) {
        if(action == null){
            throw new NullPointerException("Action cannot be null");
        }
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

    @Override
    public boolean equals(Object obj){
        if(obj == this) return true;
        if(!(obj instanceof ActionCard)) return false;
        return ((ActionCard)obj).getType().toString().equals(this.type.toString());
    }

    @Override
    public int hashCode(){
        return 17 + 31*type.hashCode();
    }
}

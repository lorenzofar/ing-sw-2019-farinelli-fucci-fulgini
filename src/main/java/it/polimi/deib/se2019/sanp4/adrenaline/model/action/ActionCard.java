package it.polimi.deib.se2019.sanp4.adrenaline.model.action;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Holds a representation of the actions the player can perform.
 * The actions can be distinguished in:
 * <ul>
 *     <li>Normal actions, of which a number indicated by {@link #maxActions} can be performed during turn</li>
 *     <li>A final action (which is optional) that can be performed when the normal actions are over, before
 *     passing the turn to the next player </li>
 * </ul>
 * @see ActionCardCreator
 */
public class ActionCard implements Serializable {

    private static final long serialVersionUID = 7168463443952478997L;

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

    /**
     * Returns the action card used at the beginning of the match.
     * Uses {@link ActionCardCreator}, so make sure it has been properly set up before calling this
     * @return the action card used at the beginning of the match
     */
    public static ActionCard initial() {
        return ActionCardCreator.createActionCard(ActionCardEnum.REGULAR);
    }

    /**
     * Returns the action card which has to be substituted to this given the damage
     * of its owner.
     * This works to set/reset adrenaline actions, but it doesn't provide a way to
     * switch between the action cards for the regular mode and the ones for the frenzy mode
     * Uses {@link ActionCardCreator}, so make sure it has been properly set up before calling this
     *
     * @param currentDamage The current damage of the player, to determine adrenaline actions
     * @return The action card that has to be substituted to this. If it needs no substitution,
     * the same action card is returned
     */
    public ActionCard next(int currentDamage) {
        /* Don't change by default */
        ActionCardEnum nextType = type;
        switch (type) {
            case REGULAR:
            case ADRENALINE1:
            case ADRENALINE2:
                /* Here we handle the regular action cards */
                if (currentDamage < 3) { nextType = ActionCardEnum.REGULAR; break; }
                if (currentDamage < 6) { nextType = ActionCardEnum.ADRENALINE1; break; }
                nextType = ActionCardEnum.ADRENALINE2; break;
            case FRENZY2:
            case FRENZY1:
                /* In frenzy mode there are no adrenaline actions */
                break;
        }
        if (nextType != type) {
            return ActionCardCreator.createActionCard(nextType); /* Need to change the action card */
        } else {
            return this; /* Don't need to change the action card */
        }
    }

    @Override
    public boolean equals(Object obj){
        if(obj == this) return true;
        if(!(obj instanceof ActionCard)) return false;
        return ((ActionCard)obj).getType().equals(this.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    
}

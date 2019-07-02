package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.ActionCardView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;

/**
 * An update sent to a player when an {@link ActionCard} changes.
 */
public class ActionCardUpdate extends ModelUpdate {

    private static final long serialVersionUID = -3631441593966463898L;
    private String player;
    private ActionCardView actionCard;

    /**
     * Creates an action card update that will be sent in broadcast.
     *
     * @param actionCard the action card to send as update, not null
     * @param player     the username of the player who owns the action card, not null
     */
    @JsonCreator
    public ActionCardUpdate(
            @JsonProperty("actionCard") ActionCardView actionCard,
            @JsonProperty("player") String player) {
        super();
        this.actionCard = actionCard;
        this.player = player;
    }

    /**
     * Returns the changed version of the action card
     *
     * @return The action card
     */
    public ActionCardView getActionCard() {
        return actionCard;
    }

    /**
     * Returns the username of the player who owns the action card
     *
     * @return The username of the player who owns the action card
     */
    public String getPlayer() {
        return player;
    }


    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

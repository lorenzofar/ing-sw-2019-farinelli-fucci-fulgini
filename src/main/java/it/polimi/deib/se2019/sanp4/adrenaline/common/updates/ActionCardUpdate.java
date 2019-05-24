package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.ActionCardView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;

import java.io.Serializable;

/**
 * An update sent to a player when its action card changes.
 */
public class ActionCardUpdate extends ModelUpdate {

    private static final long serialVersionUID = -3631441593966463898L;
    private ActionCardView actionCard;

    /**
     * Creates an action card update that will be sent in broadcast.
     * @param actionCard the action card to send as update.
     */
    @JsonCreator
    public ActionCardUpdate (
            @JsonProperty("actionCard") ActionCardView actionCard) {
        super();
        this.actionCard = actionCard;
    }

    public ActionCardView getActionCard() {
        return actionCard;
    }

    public void setActionCard(ActionCardView actionCard) {
        this.actionCard = actionCard;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

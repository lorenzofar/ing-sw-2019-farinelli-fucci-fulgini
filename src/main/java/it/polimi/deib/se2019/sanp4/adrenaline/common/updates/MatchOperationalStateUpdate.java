package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.MatchOperationalState;

/**
 * An update sent when the {@link MatchOperationalState} changes
 *
 * @author Tiziano Fucci
 */
public class MatchOperationalStateUpdate extends ModelUpdate {

    private static final long serialVersionUID = 6915574392871764395L;
    private MatchOperationalState state;

    /**
     * Creates a match operational state update that will be sent in broadcast.
     *
     * @param state the match state to send as update.
     */
    @JsonCreator
    public MatchOperationalStateUpdate(
            @JsonProperty("state") MatchOperationalState state) {
        super();
        this.state = state;
    }

    public MatchOperationalState getState() {
        return state;
    }

    public void setState(MatchOperationalState state) {
        this.state = state;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

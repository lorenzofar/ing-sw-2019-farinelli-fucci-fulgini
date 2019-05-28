package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.MatchView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;

/**
 * An update sent when the {@link Match} changes its status.
 */
public class MatchUpdate extends ModelUpdate {

    private static final long serialVersionUID = -444182184506887570L;
    private MatchView match;

    /**
     * Creates a match update that will be sent in broadcast.
     * @param match the match to send as update.
     */
    @JsonCreator
    public MatchUpdate(
            @JsonProperty("match") MatchView match) {
        super();
        this.match = match;
    }

    public MatchView getMatch() {
        return match;
    }

    public void setMatch(MatchView match) {
        this.match = match;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

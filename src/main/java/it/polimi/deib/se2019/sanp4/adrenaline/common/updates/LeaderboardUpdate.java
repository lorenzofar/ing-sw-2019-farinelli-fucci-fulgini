package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Leaderboard;

/**
 * An update containing the leaderboard and the final scores of players sent once the match is over
 *
 * @author Alessandro Fulgini
 */
public class LeaderboardUpdate extends ModelUpdate {

    private static final long serialVersionUID = 9033152745287621905L;

    private final Leaderboard leaderboard;

    /**
     * Creates a new leaderboard update that will be sent in broadcast.
     *
     * @param leaderboard The leaderboard instance to be sent, not null
     */
    @JsonCreator
    public LeaderboardUpdate(@JsonProperty("leaderboard") Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    /**
     * Returns the leaderboard instance carried by this update
     *
     * @return The leaderboard instance carried by this update
     */
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    /**
     * Makes the provided visitor handle the update
     *
     * @param visitor The object representing the visitor
     */
    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

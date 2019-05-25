package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An update sent when a player is player by another player
 */
public class KillUpdate extends ModelUpdate {

    private static final long serialVersionUID = 6862006778640996838L;

    private String killer;
    private String killed;
    /**
     * The total count of deaths of the killed player
     */
    private int deaths;

    /**
     * Creates a kill update that will be sent in broadcast.
     *
     * @param killer the player who performed the kill.
     * @param killed the killed player.
     */
    @JsonCreator
    public KillUpdate(
            @JsonProperty("killer") String killer,
            @JsonProperty("killed") String killed,
            @JsonProperty("deaths") int deaths) {
        super();
        this.killed = killed;
        this.killer = killer;
        this.deaths = deaths;
    }

    public String getKiller() {
        return killer;
    }

    public void setKiller(String killer) {
        this.killer = killer;
    }

    public String getKilled() {
        return killed;
    }

    public void setKilled(String killed) {
        this.killed = killed;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
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

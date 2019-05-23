package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An update sent when a player overkills another player
 */
public class OverkillUpdate extends ModelUpdate{

    private static final long serialVersionUID = -4938280578294737513L;

    private String killer;
    private String killed;

    /**
     * Creates an overkill update that will be sent in broadcast.
     * @param killer the player who performed the kill.
     * @param killed the killed player.
     */
    @JsonCreator
    public OverkillUpdate(
            @JsonProperty("killer") String killer,
            @JsonProperty("killed") String killed) {
        super();
        this.killed = killed;
        this.killer = killer;
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
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;

/**
 * This update is sent when a player spawns or is moved from one square to another one.
 */
public class PlayerMoveUpdate extends ModelUpdate {

    private String player;
    private CoordPair start;
    private CoordPair end;

    /**
     * Creates a move update that will be sent in broadcast.
     * @param player The name of the moved player.
     * @param start The position of the starting square, null if the player is spawning.
     * @param end The position of the square the player is moved onto.
     */

    @JsonCreator
    public PlayerMoveUpdate(
            @JsonProperty("player") String player,
            @JsonProperty("start") CoordPair start,
            @JsonProperty("end") CoordPair end) {
        super();
        this.player = player;
        this.start = start;
        this.end = end;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public CoordPair getStart() {
        return start;
    }

    public void setStart(CoordPair start) {
        this.start = start;
    }

    public CoordPair getEnd() {
        return end;
    }

    public void setEnd(CoordPair end) {
        this.end = end;
    }
}

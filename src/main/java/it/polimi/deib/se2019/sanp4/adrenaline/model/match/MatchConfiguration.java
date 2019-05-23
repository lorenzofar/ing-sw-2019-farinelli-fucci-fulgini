package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Represent the configuration needed to create a new match.
 * This includes:
 * <ul>
 *     <li>the board to be used</li>
 *     <li>the number of skulls on the killshot track</li>
 * </ul>
 * Mind that the characters are randomly assigned to the players
 */
public class MatchConfiguration implements Serializable {

    private static final long serialVersionUID = -781125362884341805L;

    private int boardId;

    private int skulls;

    /**
     * Creates an empty match configuration
     */
    public MatchConfiguration() {
        /* Nothing to do */
    }

    /**
     * Creates a match configuration with given parameters
     * @param boardId id of the board to play on
     * @param skulls number of skulls to be put on the killshot track
     * @throws IllegalArgumentException if playerColors contains duplicate values or skulls
     * @throws NullPointerException if null arguments are provided
     */
    @JsonCreator
    public MatchConfiguration(
            @JsonProperty("boardId") int boardId,
            @JsonProperty("skulls") int skulls
    ) {
        this.boardId = boardId;
        this.skulls = skulls;
    }

    /**
     * Returns the board id
     * @return board id
     */
    public int getBoardId() {
        return boardId;
    }

    /**
     * Sets the board id
     * @param boardId board id
     */
    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }


    /**
     * Returns the number of skulls
     * @return the number of skulls
     */
    public int getSkulls() {
        return skulls;
    }

    /**
     * Sets the number of skulls
     * @param skulls the number of skulls to be set
     */
    public void setSkulls(int skulls) {
        this.skulls = skulls;
    }
}

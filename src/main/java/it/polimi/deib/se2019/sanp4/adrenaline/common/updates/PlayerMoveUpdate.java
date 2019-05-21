package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;

/**
 * This update is sent when a player spawns or is moved from one square to another one.
 */
public class PlayerMoveUpdate extends ModelUpdate {

    private String player;
    private CoordPair start;
    private CoordPair end;

    /**
     * Creates an update that will be sent in broadcast
     * @param player The name of the moved player.
     * @param start The position of the starting square, null if the player is spawning.
     * @param end The position of the square the player is moved onto.
     */
    public PlayerMoveUpdate(String player, CoordPair start, CoordPair end) {
        super();
        this.player = player;
        this.start = start;
        this.end = end;
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerBoardView;

/**
 * An update sent when a player gets hit, marked or killed.
 */
public class PlayerBoardUpdate extends ModelUpdate {

    private static final long serialVersionUID = 273778241128295118L;
    private PlayerBoardView playerBoard;
    private String player;

    /**
     * Crates a player board update that will be sent in broadcast.
     *
     * @param playerBoard the player board to send as update.
     * @param player      the player owning the player board
     */
    @JsonCreator
    public PlayerBoardUpdate(
            @JsonProperty("playerboard") PlayerBoardView playerBoard,
            @JsonProperty("player") String player) {
        super();
        this.playerBoard = playerBoard;
        this.player = player;
    }

    public PlayerBoardView getPlayerBoard() {
        return playerBoard;
    }

    public void setPlayerBoard(PlayerBoardView playerBoard) {
        this.playerBoard = playerBoard;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

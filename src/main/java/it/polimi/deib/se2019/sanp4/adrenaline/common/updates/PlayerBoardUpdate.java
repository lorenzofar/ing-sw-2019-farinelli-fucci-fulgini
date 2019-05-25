package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerBoardView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerView;

/**
 * An update sent when a player gets hit, marked or killed.
 */
public class PlayerBoardUpdate extends ModelUpdate {

    private static final long serialVersionUID = 273778241128295118L;
    private PlayerBoardView playerBoard;

    /**
     * Crates a player board update that will be sent in broadcast.
     * @param playerBoard the player board to send as update.
     */
    @JsonCreator
    public PlayerBoardUpdate (
            @JsonProperty("playerboard") PlayerBoardView playerBoard) {
        super();
        this.playerBoard = playerBoard;
    }

    public PlayerBoardView getPlayerBoard() {
        return playerBoard;
    }

    public void setPlayerBoard(PlayerBoardView playerBoard) {
        this.playerBoard = playerBoard;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

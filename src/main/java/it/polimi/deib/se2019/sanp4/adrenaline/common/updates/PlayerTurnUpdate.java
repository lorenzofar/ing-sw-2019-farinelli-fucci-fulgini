package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerTurnView;

public class PlayerTurnUpdate extends ModelUpdate {

    private static final long serialVersionUID = -1100700141945270565L;
    private PlayerTurnView playerTurn;

    /**
     * Creates a player turn update that will be sent in broadcast.
     * @param playerTurn the player turn to send as update.
     */
    @JsonCreator
    public PlayerTurnUpdate(
            @JsonProperty("playerTurn") PlayerTurnView playerTurn){
        super();
        this.playerTurn = playerTurn;
    }

    public PlayerTurnView getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(PlayerTurnView playerTurn) {
        this.playerTurn = playerTurn;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

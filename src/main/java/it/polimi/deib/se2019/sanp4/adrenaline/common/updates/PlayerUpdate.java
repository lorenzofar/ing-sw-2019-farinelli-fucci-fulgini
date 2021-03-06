package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

/**
 * An update sent when a {@link Player} changes its status.
 *
 * @author Tiziano Fucci
 */
public class PlayerUpdate extends ModelUpdate {

    private static final long serialVersionUID = 3100568705204381683L;
    private PlayerView player;

    /**
     * Creates a player update that will be sent in broadcast.
     *
     * @param player the player to send as update.
     */
    @JsonCreator
    public PlayerUpdate(
            @JsonProperty("player") PlayerView player) {
        super();
        this.player = player;
    }

    /**
     * Retrieves the view of the player
     *
     * @return The object representing the view
     */
    public PlayerView getPlayer() {
        return player;
    }

    /**
     * Sets the view of the player
     *
     * @param player The object representing the view
     */
    public void setPlayer(PlayerView player) {
        this.player = player;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;

/**
 * An update sent when a player draws a {@link PowerupCard}.
 */
public class DrawnPowerupUpdate extends ModelUpdate {

    private static final long serialVersionUID = -2700775845956625363L;
    private String player;
    private PowerupCard powerupCard;

    /**
     * Creates a drawn powerup update that will be sent in broadcast.
     * @param player the player who draws the card.
     * @param powerupCard the powerup card drawn.
     */
    @JsonCreator
    public DrawnPowerupUpdate(
            @JsonProperty("player") String player,
            @JsonProperty("powerupcard") PowerupCard powerupCard) {
        super();
        this.player = player;
        this.powerupCard = powerupCard;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public PowerupCard getPowerupCard() {
        return powerupCard;
    }

    public void setPowerupCard(PowerupCard powerupCard) {
        this.powerupCard = powerupCard;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

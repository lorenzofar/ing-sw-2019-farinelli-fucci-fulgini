package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;

/**
 * An update sent when a player draws a {@link PowerupCard}.
 *
 * @author Tiziano Fucci
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

    /**
     * Returns the player property
     * @return The player property
     */
    public String getPlayer() {
        return player;
    }

    /**
     * Sets the player property
     * @param player The player property, not null
     */
    public void setPlayer(String player) {
        this.player = player;
    }

    /**
     * Returns the drawn card
     * @return the drawn card
     */
    public PowerupCard getPowerupCard() {
        return powerupCard;
    }

    /**
     * Sets the drawn card
     * @param powerupCard The drawn card, not null
     */
    public void setPowerupCard(PowerupCard powerupCard) {
        this.powerupCard = powerupCard;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

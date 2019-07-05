package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;

/**
 * An update sent when a player draws a {@link WeaponCard}.
 *
 * @author Tiziano Fucci
 */
public class DrawnWeaponUpdate extends ModelUpdate {

    private static final long serialVersionUID = -2700775845956625363L;
    private String player;
    private WeaponCard weaponCard;

    /**
     * Creates a drawn weapon update that will be sent in broadcast.
     *
     * @param player     the player who draws the card.
     * @param weaponCard the weapon card drawn.
     */
    @JsonCreator
    public DrawnWeaponUpdate(
            @JsonProperty("player") String player,
            @JsonProperty("weaponcard") WeaponCard weaponCard) {
        super();
        this.player = player;
        this.weaponCard = weaponCard;
    }

    /**
     * Returns the player property
     *
     * @return The player property
     */
    public String getPlayer() {
        return player;
    }

    /**
     * Sets the player property
     *
     * @param player The player property, not null
     */
    public void setPlayer(String player) {
        this.player = player;
    }

    /**
     * Returns the drawn card
     *
     * @return the drawn card
     */
    public WeaponCard getWeaponCard() {
        return weaponCard;
    }

    /**
     * Sets the drawn card
     *
     * @param weaponCard The drawn card, not null
     */
    public void setWeaponCard(WeaponCard weaponCard) {
        this.weaponCard = weaponCard;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

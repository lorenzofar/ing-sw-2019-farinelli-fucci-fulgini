package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;

/**
 * An update sent when a player draws a {@link WeaponCard}.
 */
public class DrawnWeaponUpdate extends ModelUpdate {

    private static final long serialVersionUID = -2700775845956625363L;
    private String player;
    private WeaponCard weaponCard;

    /**
     * Creates a drawn weapon update that will be sent in broadcast.
     * @param player the player who draws the card.
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

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public WeaponCard getWeaponCard() {
        return weaponCard;
    }

    public void setWeaponCard(WeaponCard weaponCard) {
        this.weaponCard = weaponCard;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

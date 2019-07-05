package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;

/**
 * An update sent when a {@link WeaponCard} changes its status
 *
 * @author Tiziano Fucci
 */
public class WeaponCardUpdate extends ModelUpdate {

    private static final long serialVersionUID = -5173904430046452895L;
    private WeaponCard weaponCard;

    /**
     * Creates a weapon update that will be sent in broadcast.
     *
     * @param weaponCard the weapon to send as update
     */
    @JsonCreator
    public WeaponCardUpdate(
            @JsonProperty("weapon") WeaponCard weaponCard) {
        super();
        this.weaponCard = weaponCard;
    }

    /**
     * Retrieves the weapon card
     *
     * @return The object representing the weapon card
     */
    public WeaponCard getWeaponCard() {
        return weaponCard;
    }

    /**
     * Sets the weapon card
     *
     * @param weaponCard The object representing the weapon card
     */
    public void setWeaponCard(WeaponCard weaponCard) {
        this.weaponCard = weaponCard;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

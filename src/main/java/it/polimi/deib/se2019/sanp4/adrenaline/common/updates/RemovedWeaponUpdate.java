package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An update sent when a weapon is removed from a player
 */
public class RemovedWeaponUpdate extends ModelUpdate {

    private static final long serialVersionUID = -1996514254245416308L;

    private String player;
    private String weapon;

    /**
     * Creates a removed weapon update that will be sent in broadcast.
     * @param player the player that gets the weapon.
     * @param weaponId the id of the weapon removed from the player.
     */
    @JsonCreator
    public RemovedWeaponUpdate (
            @JsonProperty("player") String player,
            @JsonProperty("weapon") String weaponId) {
        super();
        this.player = player;
        this.weapon = weaponId;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getWeapon() {
        return weapon;
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }

    /**
     * Makes the provided visitor handle the update
     *
     * @param visitor The object representing the visitor
     */
    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An update sent when a weapon is added to a player
 */
public class AddedWeaponUpdate extends ModelUpdate {

    private String player;
    private String weapon;

    /**
     * Creates an added weapon update that will be sent in broadcast.
     * @param player the player that gets the weapon.
     * @param weapon the weapon added to the player.
     */
    @JsonCreator
    public AddedWeaponUpdate (
            @JsonProperty("player") String player,
            @JsonProperty("weapon") String weapon) {
        super();
        this.player = player;
        this.weapon = weapon;
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
}

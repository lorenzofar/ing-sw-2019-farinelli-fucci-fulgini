package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An update sent when a player reloads a weapon
 */
public class ReloadUpdate extends ModelUpdate {

    private static final long serialVersionUID = 4947375127591202178L;

    private String player;
    private String weapon;

    /**
     * Creates a reload update that will be sent in broadcast.
     * @param player the player who reloaded a weapon.
     * @param weapon the reloaded weapon.
     */
    @JsonCreator
    public ReloadUpdate (
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

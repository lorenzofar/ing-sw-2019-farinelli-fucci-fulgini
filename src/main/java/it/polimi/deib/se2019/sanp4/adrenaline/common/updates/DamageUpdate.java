package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An update sent when a player receives an amount of damage from another player
 */
public class DamageUpdate extends ModelUpdate {

    private static final long serialVersionUID = -6006199997700359777L;

    private String shooter;
    private String shot;
    private int damage;

    /**
     * Creates a damage update that will be sent in broadcast.
     * @param shooter the name of the shooting player.
     * @param shot the name of the shot player.
     * @param damage the marks to be added.
     */
    @JsonCreator
    public DamageUpdate(
            @JsonProperty("shooter") String shooter,
            @JsonProperty("shot") String shot,
            @JsonProperty("damage") int damage) {
        super();
        this.shooter = shooter;
        this.shot = shot;
        this.damage = damage;
    }

    public String getShooter() {
        return shooter;
    }

    public void setShooter(String shooter) {
        this.shooter = shooter;
    }

    public String getShot() {
        return shot;
    }

    public void setShot(String shot) {
        this.shot = shot;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}

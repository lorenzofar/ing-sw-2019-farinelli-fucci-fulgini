package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A lightweight representation of a player in the view
 */
public class PlayerView implements Serializable {

    private static final long serialVersionUID = 4565784735927811143L;
    /**
     * The name of the player
     */
    private String name;
    /**
     * The color of the player
     */
    private PlayerColor color;
    /**
     * The ammo belonging to the player
     */
    private Map<AmmoCube, Integer> ammo;
    /**
     * The list of weapons belonging to the player
     */
    private List<WeaponCard> weapons;
    /**
     * The list of powerups belonging to the player
     */
    private List<PowerupCard> powerups;
    /**
     * The current score of the player
     */
    private int score;

    /**
     * Private constructor to be used only by Jackson
     */
    @JsonCreator
    private PlayerView() {
    }

    public PlayerView(String name, PlayerColor color) {
        this.name = name;
        this.color = color;
        this.score = 0;
        this.ammo = new EnumMap<>(AmmoCube.class);
        this.weapons = new ArrayList<>();
        this.powerups = new ArrayList<>();
    }

    /**
     * Retrieves the name of the player
     *
     * @return The name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the color of the player
     *
     * @return The object representing the color
     */
    public PlayerColor getColor() {
        return color;
    }

    /**
     * Retrieves the ammo belonging to the player
     *
     * @return A map containing the count of each ammo cube
     */
    public Map<AmmoCube, Integer> getAmmo() {
        return ammo;
    }

    /**
     * Sets the map of ammo belonging to the player
     *
     * @param ammo The map containing the count of each ammo cube
     */
    public void setAmmo(Map<AmmoCube, Integer> ammo) {
        if (ammo != null && ammo.values().stream().noneMatch(ammoCount -> ammoCount < 0)) {
            // We only update the value if the map is not null and does not contain negative values
            this.ammo = ammo;
        }
    }

    /**
     * Retrieves the weapon cards owned by the player
     *
     * @return The list of objects representing the weapon cards
     */
    public List<WeaponCard> getWeapons() {
        return weapons;
    }

    /**
     * Sets the weapon cards owned by the player
     * If a null object is provided, nothing happens
     *
     * @param weapons The list of objects representing the weapon cards
     */
    public void setWeapons(List<WeaponCard> weapons) {
        if (weapons != null && !weapons.contains(null)) {
            this.weapons = weapons;
        }
    }

    /**
     * Retrieves the list of powerup cards owned by the player
     *
     * @return The list of objects representing the powerup cards
     */
    public List<PowerupCard> getPowerups() {
        return powerups;
    }

    /**
     * Sets the powerup cards owned by the players
     * If a null object is provided, nothing happens
     *
     * @param powerups The list of objects representing the powerup cards
     */
    public void setPowerups(List<PowerupCard> powerups) {
        if (powerups != null && !powerups.contains(null)) {
            this.powerups = powerups;
        }
    }

    /**
     * Retrieves the current score of the player
     *
     * @return The score of the player
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the current score of the player
     * If a negative value is passed, nothing happens
     *
     * @param score The score of the player
     */
    public void setScore(int score) {
        if (score >= 0) {
            this.score = score;
        }
    }
}

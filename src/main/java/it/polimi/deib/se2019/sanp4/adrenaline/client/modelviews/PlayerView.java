package it.polimi.deib.se2019.sanp4.adrenaline.client.modelviews;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerUpCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A lightweight representation of a player in the view
 */
public class PlayerView {

    /**
     * The name of the player
     */
    private String name;
    /**
     * The color of the player
     */
    private PlayerColor color;
    /**
     * The action card belonging to the player
     */
    private ActionCardView actionCard;
    /**
     * The player board belonging to the player
     */
    private PlayerBoardView playerBoard;
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
    private List<PowerUpCard> powerups;

    public PlayerView(String name, PlayerColor color) {
        this.name = name;
        this.color = color;
        this.ammo = new EnumMap<>(AmmoCube.class);
        this.weapons = new ArrayList<>();
        this.powerups = new ArrayList<>();
        // TODO: set action card and player board
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
     * Retrieves the action card of the player
     *
     * @return The object representing the action card
     */
    public ActionCardView getActionCard() {
        return actionCard;
    }

    /**
     * Sets the action card belonging to the player
     *
     * @param actionCard The object representing the action card
     */
    public void setActionCard(ActionCardView actionCard) {
        if (actionCard != null) {
            this.actionCard = actionCard;
        }
    }

    /**
     * Retrieves the player board
     *
     * @return The object representing the player board
     */
    public PlayerBoardView getPlayerBoard() {
        return playerBoard;
    }

    /**
     * Sets the player board belonging to the player
     *
     * @param playerBoard The object representing the player board
     */
    public void setPlayerBoard(PlayerBoardView playerBoard) {
        if (playerBoard != null) {
            this.playerBoard = playerBoard;
        }
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
     * Retrieves the weapon cards owned to the player
     *
     * @return The list of objects representing the weapon cards
     */
    public List<WeaponCard> getWeapons() {
        return weapons;
    }

    /**
     * Add a weapon to the ones owned by the player
     *
     * @param weaponCard The object representing the weapon card
     */
    public void addWeapon(WeaponCard weaponCard) {
        if (weaponCard != null) {
            weapons.add(weaponCard);
        }
    }

    /**
     * Add a weapon to the ones owned by the player
     *
     * @param weaponCard The object representing the weapon card
     */
    public void removeWeapon(WeaponCard weaponCard) {
        if (weaponCard != null) {
            weapons.remove(weaponCard);
        }
    }

    /**
     * Retrieves the list of powerup cards owned by the player
     *
     * @return The list of objects representing the powerup cards
     */
    public List<PowerUpCard> getPowerups() {
        return powerups;
    }

    /**
     * Add a powerup card to the ones owned by the player
     *
     * @param powerUpCard The object representing the powerup card
     */
    public void addPowerup(PowerUpCard powerUpCard) {
        if (powerUpCard != null) {
            powerups.add(powerUpCard);
        }
    }

    /**
     * Add a powerup card to the ones owned by the player
     *
     * @param powerUpCard The object representing the powerup card
     */
    public void removePowerup(PowerUpCard powerUpCard) {
        if (powerUpCard != null) {
            powerups.remove(powerUpCard);
        }
    }
}

package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A lightweight representation of a player board in the view
 */
public class PlayerBoardView implements Serializable {
    private static final long serialVersionUID = 4751123925171736434L;
    /**
     * Damages received by other players
     */
    private List<String> damages;
    /**
     * Number of times the player died
     */
    private int deaths;
    /**
     * Count of marks received by other players
     */
    private int marks;
    /**
     * String representation of the state of the board
     */
    private String state;

    /**
     * Creates a new player board view
     */
    public PlayerBoardView() {
        damages = new ArrayList<>();
        deaths = 0;
        marks = 0;
    }

    /**
     * Retrieves the list of damages inflicted by other players
     *
     * @return The list of players' username
     */
    public List<String> getDamages() {
        return damages;
    }

    /**
     * Retrieves the number of deaths of the player
     *
     * @return The number of deaths
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * Sets the number of deaths of the player
     * If a negative value is provided, nothing happens
     *
     * @param deaths The number of deaths
     */
    public void setDeaths(int deaths) {
        if (deaths >= 0) {
            this.deaths = deaths;
        }
    }

    /**
     * Increase the number of deaths by 1
     */
    public void addDeath() {
        deaths++;
    }

    /**
     * Retrieves the number of marks received by other players
     *
     * @return The number of marks
     */
    public int getMarks() {
        return marks;
    }

    /**
     * Sets the number of marks received
     * To be used when resetting the count or to synchronize it with the game status
     *
     * @param marks The number of marks
     */
    public void setMarks(int marks) {
        if (marks >= 0) {
            this.marks = marks;
        }
    }

    /**
     * Increase the number of marks of the provided quantity
     *
     * @param marks The number of marks to add
     */
    public void addMarks(int marks) {
        if (marks > 0) {
            this.marks += marks;
        }
    }

    /**
     * Sets the list of damages received by the player
     * If a null object is provided, nothing happens
     *
     * @param damages The list of
     */
    public void setDamages(List<String> damages) {
        if (damages != null && !damages.contains(null)) {
            this.damages = damages;
        }
    }

    /**
     * Retrieves the state of the board
     * @return The string representing the state
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state of the board
     * @param state The string representing the state
     */
    public void setState(String state) {
        this.state = state;
    }
}

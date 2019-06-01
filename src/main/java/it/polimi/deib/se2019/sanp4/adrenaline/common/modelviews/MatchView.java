package it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.Serializable;

/**
 * A lightweight representation of the match in the view
 */
public class MatchView implements Serializable {

    private static final long serialVersionUID = 4604955484168954165L;
    /**
     * The count of killshots in the killshots track
     */
    private int killshotsCount;
    /**
     * The total count of skulls in the killshots track
     */
    private int totalSkulls;
    /**
     * A flag indicating whether the game is in frenzy mode or not
     */
    private boolean frenzy;

    /**
     * Creates a new match view
     */
    @JsonCreator
    public MatchView() {
        this.killshotsCount = 0;
        this.frenzy = false;
    }

    /**
     * Get the current count of skulls in the killshots track
     *
     * @return The number of skulls
     */
    public int getKillshotsCount() {
        return killshotsCount;
    }

    /**
     * Sets the count of skulls in the killshots track
     * If a negative value is passed, nothing happens
     *
     * @param killshotsCount The number of skulls
     */
    public void setKillshotsCount(int killshotsCount) {
        if (killshotsCount >= 0) {
            this.killshotsCount = killshotsCount;
        }
    }

    /**
     * Retrieves the total number of skulls in the killshots track
     *
     * @return The number of skulls
     */
    public int getTotalSkulls() {
        return totalSkulls;
    }

    /**
     * Sets the total number of skulls in the killshots track
     * If a negative number is provided, nothing happens
     *
     * @param totalSkulls The number of skulls
     */
    public void setTotalSkulls(int totalSkulls) {
        if (totalSkulls >= 0) {
            this.totalSkulls = totalSkulls;
        }
    }

    /**
     * Determines whether the match is in frenzy mode
     *
     * @return {@code true} if the match is in frenzy mode, {@code false} otherwise
     */
    public boolean isFrenzy() {
        return frenzy;
    }

    /**
     * Sets the flag telling whether the match is in frenzy mode
     *
     * @param frenzy {@code true} if the match is in frenzy mode, {@code false} otherwise
     */
    public void setFrenzy(boolean frenzy) {
        this.frenzy = frenzy;
    }
}

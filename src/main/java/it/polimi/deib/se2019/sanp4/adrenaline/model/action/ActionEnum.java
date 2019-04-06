package it.polimi.deib.se2019.sanp4.adrenaline.model.action;

/**
 * Identifies actions provided by ActionCards. It is used to get the correct ActionController.
 *  <ul>
 *      <li>
 *          {@link #RUN}, {@link #GRAB}, {@link #SHOOT} are the standard actions
 *          available when the game starts
 *      </li>
 *      <li>
 *          {@code ADRENALINE} actions will substitute the respective default actions
 *          when the player gets enough damage
 *      </li>
 *      <li>
 *          {@code FRENZY2} actions are from the card with 2x multiplier (players
 *          before the first player), while {@code FRENZY1} refers to the card
 *          with multiplier 1x (players after and including the first player)
 *      </li>
 *      <li>
 *          {@link #RELOAD} is special and is only used as a final action
 *      </li>
 *  </ul>
 */
public enum ActionEnum {
    /** Run (max 3 moves) */
    RUN("Run (max 3 moves)"),
    /** Grab stuff (max 1 move before) */
    GRAB("Grab stuff (max 1 move before)"),
    /** Shoot people */
    SHOOT("Shoot people"),
    /** Reload weapon */
    RELOAD("Reload"),
    /** Grab stuff (max 2 moves before) */
    ADRENALINE_GRAB("Grab stuff (max 2 moves before)"),
    /** Shoot (max 1 move before) */
    ADRENALINE_SHOOT("Shoot (max 1 move before)"),
    /** Move (max 1), Reload and Shoot */
    FRENZY2_SHOOT("Move (max 1), Reload and Shoot"),
    /** Run (max 4 moves) */
    FRENZY2_RUN("Run (max 4 moves)"),
    /** Grab (max 2 moves before) */
    FRENZY2_GRAB("Grab (max 2 moves before)"),
    /** Move (max 2), Reload and Shoot */
    FRENZY1_SHOOT("Move (max 2), Reload and Shoot"),
    /** Grab (max 2 moves before) */
    FRENZY1_GRAB("Grab (max 2 moves before)");

    private final String message;

    private ActionEnum(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}

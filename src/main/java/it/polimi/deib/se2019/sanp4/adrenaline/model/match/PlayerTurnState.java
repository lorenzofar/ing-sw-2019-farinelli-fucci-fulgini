package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

/** Identifies the state of a turn */
public enum PlayerTurnState {
    /** The player is performing his main actions */
    MAIN_ACTIONS("Performing main actions"),
    /** The player is performing the final action */
    FINAL_ACTION("Performing final action"),
    /** The turn is over */
    OVER("Turn is over"),
    /* The player is selecting an action */
    SELECTING("Selecting action");

    private final String message;

    PlayerTurnState(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

}

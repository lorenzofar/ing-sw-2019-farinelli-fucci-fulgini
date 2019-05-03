package it.polimi.deib.se2019.sanp4.adrenaline.model.action;

/**
 * Identifies an action card.
 */
public enum ActionCardEnum {
    /** Used when user starts playing */
    REGULAR("Regular"),
    /** Regular with first adrenaline action */
    ADRENALINE1("Regular with adrenaline run"),
    /** Regular with second adrenaline action */
    ADRENALINE2("Regular with adrenaline run and shoot"),
    /** Frenzy mode (x2), used by players before first */
    FRENZY2("Frenzy - before first player"),
    /** Frenzy mode (x1), used by players from first onwards */
    FRENZY1("Frenzy - after first player");

    private final String message;

    ActionCardEnum(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return message;
    }
}

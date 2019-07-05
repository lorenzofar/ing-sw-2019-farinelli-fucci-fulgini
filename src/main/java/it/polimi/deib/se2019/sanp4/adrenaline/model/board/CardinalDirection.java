package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

/**
 * Represents the four cardinal directions
 *
 * @author Lorenzo Farinelli
 */
public enum CardinalDirection {
    /* North cardinal direction */
    N("North"),
    /* East cardinal direction */
    E("East"),
    /* West cardinal direction */
    W("West"),
    /* South cardinal direction */
    S("South");

    private String message;

    CardinalDirection(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

    /**
     * Retrieve the cardinal direction opposite to the current one
     *
     * @return The object representing the opposite direction
     */
    public CardinalDirection getOppositeDirection() {
        switch (this) {
            case N:
                return S;
            case S:
                return N;
            case W:
                return E;
            case E:
                return W;
            default:
                return null;
        }
    }
}

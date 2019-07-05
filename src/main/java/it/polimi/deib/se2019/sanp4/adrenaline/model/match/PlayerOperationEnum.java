package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

/**
 * Represents the operation that a player can perform during his turn
 *
 * @author Alessandro Fulgini
 */
public enum PlayerOperationEnum {
    PERFORM_ACTION("Perform action"),
    USE_POWERUP("Use powerup"),
    END_TURN("End turn");

    private String message;

    /**
     * Create a new operation
     *
     * @param message a human-readable message representing the operation
     */
    PlayerOperationEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}

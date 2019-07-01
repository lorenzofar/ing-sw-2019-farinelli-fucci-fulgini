package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

/**
 * Identifies how a square is connected to another square
 */
public enum SquareConnectionType {
    /**
     * The squares are separated by a door and belong to different rooms
     */
    DOOR("Door", "╺", "╏"),
    /**
     * The squares are not connected and there is a wall between them
     */
    WALL("Wall", "━", "┃"),
    /**
     * The squares are connected and belong to the same room
     */
    FLOOR("Floor", " ", " ");

    private String message;
    private String hCharacterRepresentation;
    private String vCharacterRepresentation;

    SquareConnectionType(String message, String hCharacterRepresentation, String vCharacterRepresentation) {
        this.message = message;
        this.hCharacterRepresentation = hCharacterRepresentation;
        this.vCharacterRepresentation = vCharacterRepresentation;
    }

    @Override
    public String toString() {
        return this.message;
    }

    /**
     * Returns a string with a single character to print this connection type, in horizontal direction
     * @return A single-char string
     */
    public String getHorizontalCharacterRepresentation() {
        return hCharacterRepresentation;
    }

    /**
     * Returns a string with a single character to print this connection type, in vertical direction
     * @return A single-char string
     */
    public String getVerticalCharacterRepresentation() {
        return vCharacterRepresentation;
    }
}
    
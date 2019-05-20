package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

/** Identifies how a square is connected to another square*/
public enum SquareConnectionType {
    /** The squares are separated by a door and belong to different rooms*/
    DOOR("Door", "░"),
    /** The squares are not connected and there is a wall between them */
    WALL("Wall", "█"),
    /** The squares are connected and belong to the same room */
    FLOOR("Floor", " ");

    private String message;
    private String characterRepresentation;

    SquareConnectionType(String message, String characterRepresentation){
        this.message = message;
        this.characterRepresentation = characterRepresentation;
    }

    @Override
    public String toString() {
        return this.message;
    }

    public String getCharacterRepresentation(){
        return characterRepresentation;
    }
}
    
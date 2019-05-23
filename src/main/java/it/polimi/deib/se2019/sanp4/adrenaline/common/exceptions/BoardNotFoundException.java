package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

public class BoardNotFoundException extends Exception {
    private static final long serialVersionUID = -357038850636423785L;

    public BoardNotFoundException() {
        super();
    }

    public BoardNotFoundException(String message) {
        super(message);
    }
}

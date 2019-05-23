package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

public class CardNotFoundException extends Exception {

    private static final long serialVersionUID = 1611554844941738693L;

    public CardNotFoundException() {
        super();
    }

    public CardNotFoundException(String message) {
        super(message);
    }
}

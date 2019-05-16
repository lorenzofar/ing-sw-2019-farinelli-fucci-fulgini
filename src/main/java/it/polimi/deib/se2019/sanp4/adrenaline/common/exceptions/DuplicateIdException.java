package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

/**
 * Thrown when trying to insert a new value in a map, which should have a unique key, but
 * the key is already associated to another value.
 */
public class DuplicateIdException extends Exception {

    public DuplicateIdException() {
        super();
    }

    public DuplicateIdException(String message) {
        super(message);
    }
}

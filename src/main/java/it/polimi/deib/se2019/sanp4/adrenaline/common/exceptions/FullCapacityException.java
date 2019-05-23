package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

/**
 * Indicates that an item you're trying to add to a collection (e.g. a Weapon to a Player) cannot be
 * added because that collection is not allowed by game rules to have more than a certain amount of
 * items (capacity).
 */
public class FullCapacityException extends Exception {
    private static final long serialVersionUID = 3060596949371896899L;
    private final int capacity;

    /**
     * Constructor: assigns capacity and sets a proper message for the throwable.
     * @param capacity maximum capacity which has been reached
     */
    public FullCapacityException(int capacity) {
        super(String.format("Cannot add this element because maximum capacity %s has been reached", capacity));
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }
}

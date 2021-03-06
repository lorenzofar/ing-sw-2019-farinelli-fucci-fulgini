package it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions;

/**
 * Indicates that an item you're trying to add to a collection (e.g. a Weapon to a Player) cannot be
 * added because that collection is not allowed by game rules to have more than a certain amount of
 * items (capacity).
 *
 * @author Alessandro Fulgini
 */
public class FullCapacityException extends Exception {
    private static final long serialVersionUID = 3060596949371896899L;
    private final int capacity;

    /**
     * Constructor: assigns capacity and sets a proper message for the throwable.
     *
     * @param capacity The maximum capacity, which has been reached
     */
    public FullCapacityException(int capacity) {
        super(String.format("Cannot add this element because maximum capacity %s has been reached", capacity));
        this.capacity = capacity;
    }

    /**
     * Returns the capacity which has been reached
     *
     * @return The maximum capacity, which has been reached
     */
    public int getCapacity() {
        return capacity;
    }
}

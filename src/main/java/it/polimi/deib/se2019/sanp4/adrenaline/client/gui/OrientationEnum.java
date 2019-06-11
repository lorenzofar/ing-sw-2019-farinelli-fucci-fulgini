package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

/**
 * An enumerator describing the orientation of an object in the space
 */
public enum OrientationEnum {
    UP(0),
    DOWN(180),
    LEFT(270),
    RIGHT(90);

    /**
     * The degree the object is rotated with respect to the up-facing orientation (clock-wise)
     */
    private int rotation;

    OrientationEnum(int rotation) {
        this.rotation = rotation;
    }

    /**
     * Retrieves the degree the object has to be rotated from the up-facing orientation to obtain its current orientation
     *
     * @return The amount of degrees the object has to be rotated
     */
    public int getRotation() {
        return rotation;
    }
}

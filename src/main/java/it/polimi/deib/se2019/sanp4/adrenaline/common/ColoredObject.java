package it.polimi.deib.se2019.sanp4.adrenaline.common;

public interface ColoredObject {

    /**
     * Retrieves the ANSI code representing the color of the object
     * @return The ANSI code
     */
    String getAnsiCode();

    /**
     * Retrieves the HEX code representing the color of the object
     * @return The HEX code
     */
    String getHexCode();
}

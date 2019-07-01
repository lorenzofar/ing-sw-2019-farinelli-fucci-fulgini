package it.polimi.deib.se2019.sanp4.adrenaline.common;

/**
 * Provides methods to get color codes of an object with a color.
 * The color can be retrieved either as an ANSI escape code or
 * as an HEX (RGB, 8 bits per channel) value.
 */
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

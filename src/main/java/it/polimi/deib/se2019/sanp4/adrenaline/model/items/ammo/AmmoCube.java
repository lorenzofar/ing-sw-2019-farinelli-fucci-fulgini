package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Identifies the color of an ammo cube
 */
public enum AmmoCube implements ColoredObject {

    /**
     * Blue color
     */
    BLUE("Blue", "\u001B[34m", "#3779ff"),
    /**
     * Yellow color
     */
    YELLOW("Yellow", "\u001B[33m", "#f9cc00"),
    /**
     * Red color
     */
    RED("Red", "\u001B[31m", "#f63a30");

    private String message;
    private String ansiCode;
    private String hexCode;

    AmmoCube(String message, String ansiCode, String hexCode) {
        this.message = message;
        this.ansiCode = ansiCode;
        this.hexCode = hexCode;
    }

    /**
     * Given a collection of {@link AmmoCube} computes a map where each key is
     * the {@link AmmoCube} and the value is the number of occurrences of that in the
     * given collection
     * @param ammoCubes A collection of {@link AmmoCube}, not null
     * @return A map with counted occurrences
     */
    public static Map<AmmoCube, Integer> mapFromCollection(Collection<AmmoCube> ammoCubes) {
        Map<AmmoCube, Integer> map = new EnumMap<>(AmmoCube.class);
        ammoCubes.forEach(e -> map.merge(e, 1, Integer::sum));
        return map;
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public String getAnsiCode() {
        return ansiCode;
    }

    @Override
    public String getHexCode() {
        return hexCode;
    }
}
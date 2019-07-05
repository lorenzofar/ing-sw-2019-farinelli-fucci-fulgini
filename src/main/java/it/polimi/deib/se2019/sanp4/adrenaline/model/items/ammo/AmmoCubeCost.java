package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Integer.max;

/**
 * Describes the possible costs in terms of ammo cube colors
 *
 * @author Alessandro Fulgini, Lorenzo Farinelli
 */
public enum AmmoCubeCost implements ColoredObject {

    /**
     * Red cube
     */
    RED("Red"),
    /**
     * Yellow cube
     */
    YELLOW("Yellow"),
    /**
     * Blue cube
     */
    BLUE("Blue"),
    /**
     * Can be any of the cubes
     */
    ANY("Any");

    private String message;

    AmmoCubeCost(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }


    /**
     * Determines whether the provided cube can suffice as a payment method
     *
     * @param cube The object representing the cube, not null
     * @return {@code true} if the cube is sufficient, {@code false} otherwise
     */
    public boolean canPayFor(AmmoCubeCost cube) {
        if (cube == null) {
            throw new NullPointerException("Cube cost cannot be null");
        }
        return this == AmmoCubeCost.ANY || this == cube;
    }

    /**
     * Determines whether the provided cube can suffice as a payment method
     *
     * @param cube The object representing the cube
     * @return {@code true} if the cube is sufficient, {@code false} otherwise
     */
    public boolean canPayFor(AmmoCube cube) {
        if (cube == null) {
            throw new NullPointerException("Cube cannot be null");
        }
        return this == AmmoCubeCost.ANY || this.toString().equals(cube.toString());
    }

    /**
     * Returns the corresponding ammo cube if this cost is
     * {@link AmmoCubeCost#RED}, {@link AmmoCubeCost#YELLOW} or {@link AmmoCubeCost#BLUE};
     * if this cost is {@link AmmoCubeCost#ANY} returns {@code null}
     *
     * @return The corresponding ammo cube if it can be uniquely determined, {@code null} otherwise
     */
    public AmmoCube getCorrespondingCube() {
        switch (this) {
            case RED:
                return AmmoCube.RED;
            case BLUE:
                return AmmoCube.BLUE;
            case YELLOW:
                return AmmoCube.YELLOW;
            default:
                return null;
        }
    }

    /**
     * Given a cost which has to be covered and a the ammo cubes that are available to pay that cost,
     * tries to cover the cost with the given ammo cubes and determines the part of the cost that can't
     * be covered with those ammo cubes.
     *
     * @param initialCost   A map representing the cost which has to be paid. A missing key will be interpreted
     *                      as a 0 for that particular cube cost. All values must be &gt; 0.
     * @param availableAmmo A map with the ammo available for a specific color.A missing key will be interpreted
     *                      as a 0 for that particular cube color. All values must be &gt; 0.
     * @return A map containing the remaining costs. A missing key must be interpreted as a 0, the values are all &gt; 0
     */
    public static Map<AmmoCubeCost, Integer> calculateRemainingCost(
            Map<AmmoCubeCost, Integer> initialCost, Map<AmmoCube, Integer> availableAmmo) {

        /* Make a copy of the original maps */
        Map<AmmoCubeCost, Integer> remainingCost = new EnumMap<>(AmmoCubeCost.class);
        remainingCost.putAll(initialCost);
        Map<AmmoCube, Integer> remainingAmmo = new EnumMap<>(AmmoCube.class);
        remainingAmmo.putAll(availableAmmo);

        /* First we try to cover the cost for all the colors except ANY */
        for (Map.Entry<AmmoCubeCost, Integer> e : remainingCost.entrySet()) {
            AmmoCubeCost costColor = e.getKey();
            int costCount = e.getValue() == null ? 0 : e.getValue();
            if (costColor == ANY || costCount == 0) continue; /* Skip cost ANY or costs not to cover */

            /* Determine the corresponding cube and the number of cubes available to pay */
            AmmoCube cubeColor = costColor.getCorrespondingCube();
            int cubeCount = remainingAmmo.getOrDefault(cubeColor, 0);

            /* Update the number of available cubes and the remaining cost */
            remainingCost.put(costColor, max(0, costCount - cubeCount));
            remainingAmmo.put(cubeColor, max(0, cubeCount - costCount));
        }

        /* Then we try to cover the cost for ANY */
        int anyCount = remainingCost.getOrDefault(ANY, 0);

        if (anyCount > 0) {
            /* Determine how many cubes can pay for ANY */
            int canPayAnyCount = remainingAmmo.entrySet().stream()
                    .filter(e -> ANY.canPayFor(e.getKey()))
                    .mapToInt(e -> e.getValue() != null ? e.getValue() : 0) /* Default value is 0 */
                    .sum();

            remainingCost.put(ANY, max(0, anyCount - canPayAnyCount));
        }

        return remainingCost;
    }

    /**
     * Determines if the given ammo cost can be payed using the available ammo
     *
     * @param cost          A map containing the number of cubes to pay for each color, not null and with no negative values
     * @param availableAmmo A map containing the number of cubes available to pay for each color, not null
     *                      and with no negative values
     * @return If the cost can be paid or not
     */
    public static boolean canPayAmmoCost(Map<AmmoCubeCost, Integer> cost, Map<AmmoCube, Integer> availableAmmo) {
        /* Calculate the remaining cost */
        Map<AmmoCubeCost, Integer> remainingCost = calculateRemainingCost(cost, availableAmmo);

        /* The cost can be paid if the remaining cost is zero */
        return remainingCost.values().stream().mapToInt(i -> i == null ? 0 : i).sum() == 0;
    }

    /**
     * Given a collection of {@link AmmoCubeCost} computes a map where each key is
     * the {@link AmmoCubeCost} and the value is the number of occurrences of that in the
     * given collection
     *
     * @param cost A collection of {@link AmmoCubeCost}, not null
     * @return A map with counted occurrences
     */
    public static Map<AmmoCubeCost, Integer> mapFromCollection(Collection<AmmoCubeCost> cost) {
        return cost.stream().collect(Collectors.toMap(k -> k, k -> 1, Integer::sum));
    }

    @Override
    public String getAnsiCode() {
        AmmoCube correspondingCube = this.getCorrespondingCube();
        return correspondingCube != null ? correspondingCube.getAnsiCode() : "";
    }

    @Override
    public String getHexCode() {
        AmmoCube correspondingCube = this.getCorrespondingCube();
        return correspondingCube != null ? correspondingCube.getHexCode() : "";
    }
}